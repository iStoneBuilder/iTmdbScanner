package com.stone.it.micro.rcms.tmdb.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/11/9
 * @Desc
 */
@Data
public class TmdbIdVO {

    private String resourceId;
    /**
     * 电影ID
     */
    private String movieId;
    private String tvId;
    private String seasonId;
    private String episodeId;
}
