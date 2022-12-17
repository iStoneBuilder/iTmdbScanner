package com.stone.it.micro.rcms.tmdb.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/11/9
 * @Desc
 */
@Data
public class BaseTmdbVO {

    /**
     * api_key
     */
    private String api_key;
    private Integer page;
    /**
     * 语言，是否成人模式，模式>网络/本地（系统设置）
     */
    private String language;
    private String include_adult;
    private String mode;

}
