package com.stone.it.micro.ifeast.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/12/10
 * @Desc
 */
@Data
public class SearchVO {

    private String api_key;
    private Integer page;
    private String language;
    private String include_adult;
    private String mode;
    private String sort_by;
    private String query;
    private Integer year;

}
