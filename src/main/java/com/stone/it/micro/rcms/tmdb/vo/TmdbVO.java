package com.stone.it.micro.rcms.tmdb.vo;

import com.stone.it.micro.rcms.base.vo.BaseVO;
import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/10/23
 * @Desc
 */
@Data
public class TmdbVO extends BaseVO {

    private String resourceId;
    private String tmdbId;
    private String isConfirm;
    private String resourcePath;
    private String resourceName;
    private String resourceData;
    private String title;
    private String settingId;
    private String language;
    private String refreshId;
    /**
     * 过滤查询（上映日期，上映地区，类型，资源类型，是否成人模式）
     */
    private String release_date;
    private String production_countries;
    private String genres;
    private String media_type;
    private String include_adult;

    /**
     * 分页使用
     */
    private Integer page;
    private Integer startIndex;
    private Integer pageSize;

    public TmdbVO() {
        page = 1;
        pageSize = 20;
        startIndex = (page - 1) * pageSize;
    }

}
