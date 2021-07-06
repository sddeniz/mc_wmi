package com.behsa.sdp.mcwmi.repository;

import com.behsa.sdp.mcwmi.entity.UserModel;
import oracle.sql.DATE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserModel, Long> {

    @Override
    List<UserModel> findAll();

    @Override
    Optional<UserModel> findById(Long aLong);

    //@Query("select u from tbl_users_api u where u.username =:username")
    UserModel findByUserName(String username);

    UserModel findUserModelByUserNameAndPasswords(String userName, String password);


    UserModel findAllByCreateDate(DATE date);
}
