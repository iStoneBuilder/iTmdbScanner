package com.stone.it.micro.ifeast.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */

@Data
public class SettingVO {

    private String settingId;
    private String settingName;
    private String account;
    private String password;
    private String domain;
    private String port;
    private String rootPath;
    private String type;

}
