package com.stone.it.micro.rcms.tmdb.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/11/17
 * @Desc
 */
@Data
public class SettingVO {

    private String settingId;
    private String userAccount;
    private String password;
    private String domain;
    private String port;
    private String rootPath;
    private String type;

}
