package com.stone.it.micro.rcms.tmdb.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/11/12
 * @Desc
 */
@Data
public class SearchVO extends BaseTmdbVO {

    private String sort_by;
    /**
     * 搜索条件
     */
    private String query;
    /**
     * 时间
     */
    private Integer year;

}
