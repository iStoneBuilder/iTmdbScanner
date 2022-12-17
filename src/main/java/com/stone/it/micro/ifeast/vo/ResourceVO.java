package com.stone.it.micro.ifeast.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/12/10
 * @Desc
 */
@Data
public class ResourceVO {

    private String tmdbId;
    private String title;
    private String language;
    private String mediaType;
    private String releaseDate;
    private String adult;
    private String regions;
    private String genres;
    private String resourceData;

}
