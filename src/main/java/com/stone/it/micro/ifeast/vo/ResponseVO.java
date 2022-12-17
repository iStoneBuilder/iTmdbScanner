package com.stone.it.micro.ifeast.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/12/10
 * @Desc
 */

@Data
public class ResponseVO {

    private String statusCode;
    private String statusMessage;
    private String responseBody;

}
