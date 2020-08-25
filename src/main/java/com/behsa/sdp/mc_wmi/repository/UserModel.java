package com.behsa.sdp.mc_wmi.repository;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;


@Entity
@Table(name = "tbl_users_api")
@Cacheable
public class UserModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "passwords")
    private String passwords;

    @Column(name = "createdate")
    private Date createDate;

    @Column(name = "state")
    private boolean state;

    @Column(name = "lastlogin")
    private Date lastLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", passwords='" + passwords + '\'' +
                ", createDate=" + createDate +
                ", state=" + state +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
