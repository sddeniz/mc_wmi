package com.behsa.sdp.mc_wmi.controller;

import com.behsa.sdp.mc_wmi.common.*;
import com.behsa.sdp.mc_wmi.config.BeanConfig;
import com.behsa.sdp.mc_wmi.dto.*;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import com.behsa.sdp.mc_wmi.utils.ServiceTypeEnums;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import sdpMsSdk.SdpHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class RestApiController {

    private static final String MICROSERVICE_NAME = "ms_rest";
    private static final String TRIGGER_SYNC_SERVICE = "ms_rest";

    private final static Logger LOGGER = LoggerFactory.getLogger(RestApiRepository.class);


    @Autowired
    private SdpHelper sdpHelper;
    @Autowired
    private RestApiRepository restApiRepository;

    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private Gson gson;
    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    ValidationInputService validationInputService;

    @Autowired
    private CacheRestAPI cacheTreeGw;

    @Autowired
    private BeanConfig beanConfig;

    @Autowired
    private CheckBilling checkBilling;


    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/api/call/{serviceName}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> triggerSync(@PathVariable("serviceName") String serviceName,
                                                  @RequestParam("ver") String version,
                                                  @RequestBody JSONObject payload,
                                                  HttpServletRequest request) throws Exception {


        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        String host = request.getServerName().trim();// "localhost".trim();//todo back this  request.getServerName() .trim;
        String trackCode = String.valueOf(UUID.randomUUID());

        if (validationBilling(serviceName)) {
            errorResponse("Service is wrong", trackCode, output, HttpStatus.NOT_FOUND);
            LOGGER.debug("Service is block by billing  , payload:{}  , serviceName:{}  , trackCode:{}"
                    , payload, serviceName, trackCode);
            return output;
        }

        //todo validationInputService();

        TreeInfoDto infoDtoCache = cacheTreeGw.getHashMap(serviceName);


        TreeGwDto treeGwDto = configurationTreeGw(serviceName, ServiceTypeEnums.rest.getCode(), version, host);//type make enum ,
        TreeInfoDto infoDto = restApiRepository.getTreeId(treeGwDto);

        if (infoDtoCache == null) {
            cacheTreeGw.setHashMap(serviceName, infoDto);
        }

        boolean haveTree = validationInputService.isHaveTree(infoDto);

        if (!haveTree) {
            errorResponse("Service is wrong", trackCode, output, HttpStatus.NOT_FOUND);
            LOGGER.debug("Service is wrong , payload:{}  , infoDto:{}  , trackCode:{}", payload, infoDto.toString(), trackCode);
            return output;

        }
        JSONObject mapPayLoad = mapPayLoad(infoDto, payload);
        if (mapPayLoad.isEmpty()) {
            errorResponse("Input service Fields are wrong", trackCode, output, HttpStatus.BAD_REQUEST);
            LOGGER.debug("can not mach input and tree Info , payload:{}  , infoDto:{}  , trackCode:{}"
                    , payload, infoDto.toString(), trackCode);
            return output;
        }

        payloadConfig(mapPayLoad, infoDto.getTreeId(), serviceName,
                version, host, request.getServerPort(), payload);


        sessionManager.setSession(trackCode, new SessionDto(output));
        System.out.println("track code :" + trackCode);
        System.out.println("instanceKey :" + serviceUtils.getServiceInstanceKey());
        sdpHelper.sendStartProcess("sdp_api", "api_request",
                serviceUtils.getServiceInstanceKey(), mapPayLoad, null, trackCode);

        LOGGER.debug(mapPayLoad.toJSONString());
        LOGGER.info("request sent, trackCode: " + trackCode);
        return output;
    }

    /**
     * add some params Json for send to dispatcher
     *
     * @param jsonObjectConfigure
     * @param treeId              tree id
     * @param serviceName         method or service that user call
     * @param version             ver api
     * @param host                domain
     * @param port                port domain
     */
    private void payloadConfig(JSONObject jsonObjectConfigure, long treeId,
                               String serviceName, String version,
                               String host, int port, JSONObject userParamReq) {
        jsonObjectConfigure.put("api_TreeId", String.valueOf(treeId));
        jsonObjectConfigure.put("api_serviceName", serviceName);
        jsonObjectConfigure.put("api_version", String.valueOf(version));
        jsonObjectConfigure.put("api_host", host);
        jsonObjectConfigure.put("api_port", String.valueOf(port));
        jsonObjectConfigure.put("api_Request", userParamReq);
    }

    /**
     * map of payload  by insert data in db and input by user service
     * if is expose =>{ if user null then default  else user}
     * if expose is null => default
     *
     * @param infoDto
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    private JSONObject mapPayLoad(TreeInfoDto infoDto, JSONObject payload) throws JsonProcessingException {
        JSONObject apiJsonObj = new JSONObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        ApiInputDto[] dbResultApiInput = objectMapper.readValue(infoDto.getInputs(), ApiInputDto[].class);

        for (ApiInputDto apiInputDto : dbResultApiInput) {
            if (apiInputDto != null) {
                String value = "";
                if (apiInputDto.getExpose() != null && payload.get(apiInputDto.getTitle()) != null) {
                    value = String.valueOf(payload.get(apiInputDto.getTitle()));
                } else {
                    value = apiInputDto.getDefaultValue();
                }
                apiJsonObj.put("apiType", apiInputDto.getType());
                apiJsonObj.put(apiInputDto.getName(), value);
            }
        }
        return apiJsonObj;
    }


    /**
     * configuration for get treeGw
     *
     * @param domain domain name like 192.168.4.200
     * @param title  service name
     * @param ver    ver for user and get tree info
     * @param type   type of service like rest /soap /ussd
     * @return treegw dto
     */
    private TreeGwDto configurationTreeGw(String title, int type,
                                          String ver, String domain) {
        return new TreeGwDto(title, type, null, ver, domain);
    }


    private void errorResponse(String textError, String trackCode, DeferredResult<ResponseEntity<?>> output, HttpStatus httpStatus) {
        JSONObject jsonObject = new JSONObject();
        sessionManager.setSession(trackCode, new SessionDto(output));
        SessionDto session = sessionManager.getSession(trackCode);
        jsonObject.put("DSDP_Code", trackCode);
        ResponseEntity<JSONObject> jsonObjectResponseEntity = new ResponseEntity<>(jsonObject, httpStatus);
        jsonObject.put("Message", textError);
        jsonObject.put("httpStatus", jsonObjectResponseEntity.getStatusCode());
        jsonObject.put("httpStatusCode", jsonObjectResponseEntity.getStatusCodeValue());
        session.getDeferredResult().setResult(jsonObjectResponseEntity);
        session.setExpectResponse(true);
        sessionManager.setSession(trackCode, session);
    }



    private boolean validationBilling(String serviceName) {
        return checkBilling.billingCheck(serviceName);
    }

    //--------------------------------
    @PostMapping(value = "/api/response/{trackCode}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> responseSync(@PathVariable("trackCode") String trackCode,
                                                   @RequestParam("key") String apiKey,
                                                   @RequestBody JSONObject payload,
                                                   HttpServletRequest request) throws Exception {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        System.out.println("response");
        SessionDto session = sessionManager.getSession(trackCode);
        session.setDeferredResult(output);
        sdpHelper.sendResponse(payload, trackCode);
        System.out.println("service response sent, trackCode: " + trackCode);
        return output;
    }

    @PostMapping(value = "/trigger/{channelName}/{triggerName}")
    public @ResponseBody
    ResponseEntity<TriggerAsyncResponseDto> triggerAsync(@PathVariable("channelName") String
                                                                   channelName, @PathVariable("triggerName") String triggerName,
                                                         @RequestBody JSONObject payload, HttpServletRequest request) throws Exception {
        String trackCode = "";
        try {
            trackCode = sdpHelper.sendStartProcess(channelName, triggerName, null, payload, null);
            if (trackCode == null || trackCode.equals("")) {
                throw new Exception("خطا در ثبت درخواست");
            }
            System.out.println("request sent, trackCode: " + trackCode);
            return new ResponseEntity<>(new TriggerAsyncResponseDto("ORDINARY", trackCode, null),
                    HttpStatus.OK);
        } catch (Exception e) {
            JSONObject jo = new JSONObject();
            jo.put("errorCode", 1);
            jo.put("errorMessage", "خطا در ثبت درخواست");
            return new ResponseEntity<>(new TriggerAsyncResponseDto("ERROR", trackCode, jo),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
