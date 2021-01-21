package com.behsa.sdp.mc_wmi.controller;

import com.behsa.sdp.mc_wmi.common.*;
import com.behsa.sdp.mc_wmi.dto.*;
import com.behsa.sdp.mc_wmi.enums.ErrorApiGw;
import com.behsa.sdp.mc_wmi.enums.EventTypeEnums;
import com.behsa.sdp.mc_wmi.enums.ServiceTypeEnums;
import com.behsa.sdp.mc_wmi.log.APILogger;
import com.behsa.sdp.mc_wmi.repository.RestApiRepository;
import com.behsa.sdp.mc_wmi.repository.WebViewModel;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import sdpMsSdk.SdpHelper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@RestController
public class ApiGwRequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGwRequestController.class);
    private final String errorTemplatePage = "templateError";


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
    private CheckBilling checkBilling;

    @Autowired
    private APILogger apiLogger;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    void init() {
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }


    @RequestMapping(method = {RequestMethod.POST/*, RequestMethod.GET*/}, value = "/havij")
    public void test1(@RequestBody(required = false) JSONObject jsonObject) {
        System.out.println("got it");
    }


    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/api/call/{serviceName}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>>
    triggerSync(@PathVariable("serviceName") String serviceName,
                @RequestParam("ver") String version,
                @RequestBody(required = false) JSONObject payload,
                HttpServletRequest request) {

        long startTime = new Date().getTime();
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        String trackCode = String.valueOf(UUID.randomUUID());
        try {
            if (checkVersion(version)) {
                ResponseEntity<JSONObject> response = errorResponse("You have entered a blank version", trackCode, HttpStatus.BAD_REQUEST);
                LOGGER.debug("version is null or empty  , payload:{}  ,   trackCode:{}", payload, trackCode);
                output.setResult(response);
                return output;
            }
            String host = request.getServerName().trim();
            LOGGER.info("------------------- > host is :{}", host);

            if (validationBilling(serviceName)) {
                ResponseEntity<JSONObject> response = errorResponse("Billing lock your Account", trackCode, HttpStatus.LOCKED);
                LOGGER.debug("Service is block by billing  , payload:{}  , serviceName:{}  , trackCode:{}"
                        , payload, serviceName, trackCode);
                output.setResult(response);
                return output;
            }

            //todo validationInputService();

            TreeInfoDto infoDtoCache = cacheTreeGw.getHashMap(serviceName);//todo mojtaba

            TreeGwDto treeGwDto = configurationTreeGw(serviceName, ServiceTypeEnums.rest.getCode(), version, host);//type make enum ,
            TreeInfoDto treeInfoDto = restApiRepository.getTreeId(treeGwDto);

            if (infoDtoCache == null) {
                cacheTreeGw.setHashMap(serviceName, treeInfoDto);
            }

            boolean haveTree = validationInputService.isHaveTree(treeInfoDto);
            if (!haveTree) {
                ResponseEntity<JSONObject> response = errorResponse("Service is wrong or is not Active", trackCode, HttpStatus.NOT_FOUND);
                LOGGER.debug("Service is wrong , payload:{}  , treeInfoDto:{}  , trackCode:{}", payload, treeInfoDto, trackCode);
                output.setResult(response);
                return output;
            }

            JSONObject mapPayLoad = mapPayLoad(treeInfoDto, payload);
            if (mapPayLoad.isEmpty()) {
                ResponseEntity<JSONObject> response = errorResponse("Input service Fields are wrong", trackCode, HttpStatus.BAD_REQUEST);
                LOGGER.debug("can not mach input and tree Info , payload:{}  , treeInfoDto:{}  , trackCode:{}"
                        , payload, treeInfoDto.toString(), trackCode);
                output.setResult(response);
                return output;
            }


            payloadConfig(mapPayLoad, treeInfoDto.getTreeId(), serviceName,
                    version, host, request.getServerPort(), payload);


            sessionManager.setSession(trackCode, new SessionDto(output, null, serviceName, version, ServiceTypeEnums.rest));
            LOGGER.debug("track code:{} , instanceKey:{} , mapPayLoad:{} , channel name:sdp_api and serviceName api_request "
                    , trackCode, serviceUtils.getServiceInstanceKey(), mapPayLoad);

            sdpHelper.sendStartProcess("sdp_api",
                    serviceName,
                    serviceUtils.getServiceInstanceKey(), mapPayLoad, null, trackCode);

            System.out.println(serviceUtils.getServiceInstanceKey());
            LOGGER.debug("send Success************************************************* ");

            this.apiLogger.insert(serviceName,
                    trackCode,
                    EventTypeEnums.sendToWorker.getValue(),
                    "Trace",
                    version,
                    serviceName,
                    "",
                    " _ ",
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), null, null);
        } catch (Exception e) {
            LOGGER.debug("triggerSync: ", e);
            this.apiLogger.insert(serviceName,
                    trackCode,
                    EventTypeEnums.sendToWorker.getValue(),
                    "Trace",
                    version,
                    serviceName,
                    "",
                    " _ ",
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), "90009", e.getMessage());
        }
        return output;

    }


    //---------------------------


    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/web/call/{serviceName}")
    public @ResponseBody
    DeferredResult<ModelAndView>
    webViewApiGw(@PathVariable("serviceName") String serviceName,
                 @RequestParam("ver") String version,
                 @RequestBody(required = false) JSONObject payload,
                 HttpServletRequest request) {


        long startTime = new Date().getTime();
        DeferredResult<ModelAndView> output = new DeferredResult<>();
        String trackCode = String.valueOf(UUID.randomUUID());
        try {
            if (checkVersion(version)) {
                LOGGER.debug("version is null or empty  , payload:{}  ,   trackCode:{}", payload, trackCode);
                output.setResult(new ModelAndView(errorTemplatePage, "errorModel", new WebViewModel(ErrorApiGw.versionWeb.getValue())));
                return output;
            }
            String host = request.getServerName().trim();
            LOGGER.info("------------------- > host is :{}", host);

            if (validationBilling(serviceName)) {
                LOGGER.debug("Service is block by billing  , payload:{}  , serviceName:{}  , trackCode:{}"
                        , payload, serviceName, trackCode);
                output.setResult(new ModelAndView(errorTemplatePage, "errorModel", new WebViewModel(ErrorApiGw.billingWeb.getValue())));
                return output;
            }

            TreeInfoDto infoDtoCache = cacheTreeGw.getHashMap(serviceName);//todo mojtaba

            TreeGwDto treeGwDto = configurationTreeGw(serviceName, ServiceTypeEnums.rest.getCode(), version, host);//type make enum ,
            TreeInfoDto treeInfoDto = restApiRepository.getTreeId(treeGwDto);

            if (infoDtoCache == null) {
                cacheTreeGw.setHashMap(serviceName, treeInfoDto);
            }

            boolean haveTree = validationInputService.isHaveTree(treeInfoDto);
            if (!haveTree) {
                String response = "<html><body><h1>Erorr,Service is wrong or is not Active !</h1></body></html>";//todo clean
                LOGGER.debug("Service is wrong , payload:{}  , treeInfoDto:{}  , trackCode:{}", payload, treeInfoDto, trackCode);
                output.setResult(new ModelAndView(errorTemplatePage, "errorModel", new WebViewModel(ErrorApiGw.serviceWeb.getValue())));
                return output;
            }

            JSONObject mapPayLoad = mapPayLoad(treeInfoDto, payload);
            if (mapPayLoad.isEmpty()) {
                String response = "<html><body><h1>Erorr,Input service Fields are wrong !</h1></body></html>";//todo clean
                LOGGER.debug("can not mach input and tree Info , payload:{}  , treeInfoDto:{}  , trackCode:{}"
                        , payload, treeInfoDto.toString(), trackCode);
                output.setResult(new ModelAndView(errorTemplatePage, "errorModel", new WebViewModel(ErrorApiGw.inputWeb.getValue())));
                return output;
            }


            payloadConfig(mapPayLoad, treeInfoDto.getTreeId(), serviceName,
                    version, host, request.getServerPort(), payload);

            sessionManager.setSession(trackCode, new SessionDto(null, output, serviceName, version, ServiceTypeEnums.web));
            LOGGER.debug("track code:{} , instanceKey:{} , mapPayLoad:{} , channel name:sdp_api and serviceName api_request "
                    , trackCode, serviceUtils.getServiceInstanceKey(), mapPayLoad);

            sdpHelper.sendStartProcess("sdp_api",
                    serviceName,
                    serviceUtils.getServiceInstanceKey(), mapPayLoad, null, trackCode);

            LOGGER.info(serviceUtils.getServiceInstanceKey());
            LOGGER.debug("send Success************************************************* ");

            this.apiLogger.insert(serviceName,
                    trackCode,
                    EventTypeEnums.sendToWorker.getValue(),
                    "Trace",
                    version,
                    serviceName,
                    "",
                    " _ ",
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), null, null);
        } catch (Exception e) {
            LOGGER.debug("triggerSync: ", e);
            this.apiLogger.insert(serviceName,
                    trackCode,
                    EventTypeEnums.sendToWorker.getValue(),
                    "Trace",
                    version,
                    serviceName,
                    "",
                    " _ ",
                    "", "", "true",
                    String.valueOf(new Date().getTime() - startTime), "90009", e.getMessage());
        }
        return output;

    }


    //---------------------------

    private boolean checkVersion(String version) {
        return StringUtils.isEmpty(version);
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String usernameApiGw = principal.getUsername();
        userParamReq.put("api_userName", usernameApiGw);


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
     * treeInfoDto is not null cause check this before
     *
     * @param treeInfoDto
     * @param payload
     * @return
     * @throws JsonProcessingException
     */
    private JSONObject mapPayLoad(TreeInfoDto treeInfoDto, JSONObject payload) throws JsonProcessingException {

        ApiInputDto[] dbResultApiInput = objectMapper.readValue(treeInfoDto.getInputs(), ApiInputDto[].class);
        JSONObject apiJsonObj = new JSONObject();
        if (treeInfoDto.getInputs().equals("[]")) {
            apiJsonObj.put("apiType", "String");
            apiJsonObj.put("", "");
            return apiJsonObj;
        }

        if ((dbResultApiInput == null || dbResultApiInput.length == 0 || payload == null)) {
            return apiJsonObj;
        }


        for (ApiInputDto apiInputDto : dbResultApiInput) {
            String value = "";
            if (apiInputDto.getExpose() != null && payload.get(apiInputDto.getTitle()) != null) {
                value = String.valueOf(payload.get(apiInputDto.getTitle()));
            } else {
                value = apiInputDto.getDefaultValue();
            }
            apiJsonObj.put("apiType", apiInputDto.getType());
            apiJsonObj.put(apiInputDto.getName(), value);
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


    private ResponseEntity<JSONObject> errorResponse(String textError, String trackCode, HttpStatus httpStatus) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Track_Code", trackCode);
        jsonObject.put("Message", textError);
        return new ResponseEntity<>(jsonObject, httpStatus);
    }

    private boolean validationBilling(String serviceName) {
        return checkBilling.billingCheck(serviceName);
    }


    public void cacheMatchTopUp(String urls) throws IOException {
        // TODO: 1/16/2021 check this
        URL url = new URL(urls);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }


    }

    //--------------------------------
    @PostMapping(value = "/api/response/{trackCode}")
    public @ResponseBody
    DeferredResult<ResponseEntity<?>> responseSync(@PathVariable("trackCode") String trackCode,
                                                   @RequestParam("key") String apiKey,
                                                   @RequestBody JSONObject payload,
                                                   HttpServletRequest request) throws Exception {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        SessionDto session = sessionManager.getSession(trackCode);
        session.setRestDeferredResult(output);
        sdpHelper.sendResponse(payload, trackCode);
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
