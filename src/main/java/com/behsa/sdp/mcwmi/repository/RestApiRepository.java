package com.behsa.sdp.mcwmi.repository;

import com.behsa.sdp.mcwmi.common.ConnectionProvider;
import com.behsa.sdp.mcwmi.dto.MaxBind;
import com.behsa.sdp.mcwmi.dto.PermissionDto;
import com.behsa.sdp.mcwmi.dto.TreeGwDto;
import com.behsa.sdp.mcwmi.dto.TreeInfoDto;
import com.behsa.sdp.mcwmi.enums.ServiceTypeEnums;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.CoreException;
import models.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RestApiRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiRepository.class);

    private Type maxBindType = new TypeToken<List<MaxBind>>() {
    }.getType();
    @Autowired
    private ConnectionProvider connectionProvider;
    @Autowired
    private Gson gson;

    public List<Recipe> getRestApiRepository() throws CoreException {
        List<Recipe> recipe = new ArrayList<>(); //todo edit this
        try (Connection connection = connectionProvider.getConnection(); Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery("SELECT * FROM VW_RECIPE_FOR_USSD");
            if (result.next()) {
                recipe.add(new Recipe(result.getLong("ID"), result.getInt("TRIGGER_ID"), result.getString("TRIGGER_CARD_CODE"),
                        result.getByte("SHORT_CODE_TYPE")));
            }

        } catch (SQLException e) {
            throw new CoreException(10000, "failed to ger recipe by ussd code", e); //todo define code
        } catch (Exception ex) {
            throw new CoreException(20000, "failed to ger recipe by ussd code", ex); //todo define code
        }

        return recipe;
    }

    /**
     * find tree by domain name and service name and type and version if Active
     *
     * @return
     */
    public TreeInfoDto getTreeId(TreeGwDto treeGwDto) {

        String command = "select * from VW_TreeGw tr  where tr.type=? and tr.verApi=?  and  tr.GETWAYNAME=? and tr.domain=?  and tr.state=1";

        LOGGER.debug("input for find Tree: type:{} , verApi:{} , getwayname:{} , domain:{} ,and show Actives "
                , treeGwDto.getType(), treeGwDto.getVersion(), treeGwDto.getTitle(), treeGwDto.getDomain());

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(command)) {
            preparedStatement.setInt(1, treeGwDto.getType());
            preparedStatement.setString(2, treeGwDto.getVersion());
            preparedStatement.setString(3, treeGwDto.getTitle());
            preparedStatement.setString(4, treeGwDto.getDomain());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new TreeInfoDto(resultSet.getLong("TREEID"), resultSet.getString("INPUTS"), resultSet.getString("OUTPUTS"));
            }
            LOGGER.debug("can not find Treee");
        } catch (SQLException e) {
            LOGGER.error("SQL Error For Find Tree ", e);
        } catch (Exception ex) {
            LOGGER.error("Exception ", ex);
        }

        return null;
    }


    public Map<String, PermissionDto> getPermission(String userName) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "select * from vw_userPermission pr " +
                             "where pr.username=? ")) {

            preparedStatement.setString(1, userName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            Map<String, PermissionDto> permissionMap = new HashMap<>();
            while (resultSet.next()) {

                permissionMap.put(ServiceTypeEnums.getEnum(resultSet.getInt("apitype")).getValue() + resultSet.getString("serviceTitle"), new PermissionDto
                        (
                                resultSet.getLong("id"),
                                resultSet.getString("username"),
                                resultSet.getString("serviceTitle"),
                                resultSet.getLong("tps"),
                                resultSet.getLong("tpd"),
                                resultSet.getString("startdate"),
                                resultSet.getString("enddate"),
                                gson.fromJson(resultSet.getString("maxbind"), maxBindType),
                                resultSet.getString("servicetimeout"),
                                resultSet.getLong("userId"),
                                resultSet.getLong("serviceid"),
                                ServiceTypeEnums.getEnum(resultSet.getInt("apitype"))
                        ));
            }
            return permissionMap;
        } catch (SQLException throwables) {
            LOGGER.error("SQL Error For Find Tree ", throwables);
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }
        return null;//todo clean this
    }

    public Map<String, Map<String, PermissionDto>> getAllPermissions(List<UserModel> userModels) {
        Map<String, Map<String, PermissionDto>> userPermission = new ConcurrentHashMap<>();

        for (UserModel userModel : userModels) {
            userPermission.put(userModel.getUserName(), new HashMap<>());
        }

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "select * from vw_userPermission pr ")) {


            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String key = ServiceTypeEnums.getEnum(resultSet.getInt("apitype")).getValue() + resultSet.getString("serviceTitle");
                if (userPermission.get(resultSet.getString("username")) == null) {
                    Map<String, PermissionDto> permissionMap = new HashMap<>();
                    permissionMap.put(key, configurationPermissionDto(resultSet));
                    userPermission.put(resultSet.getString("username"), permissionMap);
                } else {
                    Map<String, PermissionDto> permissionMap = userPermission.get(resultSet.getString("username"));
                    permissionMap.put(key, configurationPermissionDto(resultSet));
                    userPermission.put(resultSet.getString("username"), permissionMap);
                }
            }
            return userPermission;
        } catch (SQLException throwables) {
            LOGGER.error("SQL Error For Find Tree ", throwables);
        } catch (Exception e) {
            LOGGER.error("Exception ", e);
        }
        return null;//todo clean this
    }

    private PermissionDto configurationPermissionDto(ResultSet resultSet) throws SQLException {
        return new PermissionDto
                (
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("serviceTitle"),
                        resultSet.getLong("tps"),
                        resultSet.getLong("tpd"),
                        resultSet.getString("startdate"),
                        resultSet.getString("enddate"),
                        gson.fromJson(resultSet.getString("maxbind"), maxBindType),
                        resultSet.getString("servicetimeout"),
                        resultSet.getLong("userId"),
                        resultSet.getLong("serviceid"),
                        ServiceTypeEnums.getEnum(resultSet.getInt("apitype"))
                );
    }


}
