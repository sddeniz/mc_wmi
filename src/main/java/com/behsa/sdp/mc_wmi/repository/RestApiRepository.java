package com.behsa.sdp.mc_wmi.repository;

import com.behsa.sdp.mc_wmi.common.ConnectionProvider;
import com.behsa.sdp.mc_wmi.dto.PermissionDto;
import com.behsa.sdp.mc_wmi.dto.TreeGwDto;
import com.behsa.sdp.mc_wmi.dto.TreeInfoDto;
import com.behsa.sdp.mc_wmi.utils.AppConfig;
import common.CoreException;
import models.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RestApiRepository {
    private final static Logger logger = LoggerFactory.getLogger(RestApiRepository.class);


    @Autowired
    ConnectionProvider connectionProvider;
    @Autowired
    AppConfig appConfig;


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

        String command = "select * from VW_TreeGw tr  where tr.type=? and tr.verApi=?  and  tr.title=? and tr.domain=? and tr.state=1";

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     command
//                     "select * from VW_TreeGw tr " +
//                     "where tr.type=? " +
//                     "and tr.verApi=? " +
//                     "and  tr.title=? " +
//                     "and tr.domain=? " +
//                     "and tr.state=1")

             )) {

            preparedStatement.setInt(1, treeGwDto.getType());
            preparedStatement.setString(2, treeGwDto.getVersion());
            preparedStatement.setString(3, treeGwDto.getTitle());
            preparedStatement.setString(4, treeGwDto.getDomain());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new TreeInfoDto(resultSet.getLong("TREEID"), resultSet.getString("INPUTS"), resultSet.getString("OUTPUTS"));
            }

            return null;

        } catch (SQLException throwables) {
            logger.error("SQL Error For Find Tree ", throwables);
        } catch (Exception e) {
            logger.error("Exception ", e);
        }

        return null;
    }


    public List<PermissionDto> getPermission(String userName) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "select * from vw_userPermission pr " +
                             "where pr.username=? ")) {

            preparedStatement.setString(1, userName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<PermissionDto> permissionDtos = new ArrayList<>();
            if (resultSet.next()) {
                permissionDtos.add(new PermissionDto
                        (
                                resultSet.getString("username"),
                                resultSet.getString("serviceTitle"),
                                resultSet.getString("tps"),
                                resultSet.getString("tpd"),
                                resultSet.getString("startdate"),
                                resultSet.getString("enddate"),
                                resultSet.getString("maxbind"),
                                resultSet.getString("servicetimeout"),
                                resultSet.getLong("userId"),
                                resultSet.getLong("serviceid")
                        ));
            }
            return permissionDtos;
        } catch (SQLException throwables) {
            logger.error("SQL Error For Find Tree ", throwables);
        } catch (Exception e) {
            logger.error("Exception ", e);
        }
        return null;//todo clean this
    }


}
