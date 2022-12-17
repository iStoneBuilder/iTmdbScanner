package com.stone.it.micro.rcms.tmdb.foreign.service;

import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.rcms.tmdb.vo.TmdbVO;

import javax.ws.rs.*;

/**
 * @author cj.stone
 * @Date 2022/11/20
 * @Desc
 */
@Path("/tmdbService")
@Produces("application/json")
@Consumes("application/json")
public interface ITmdbService {

    /**
     * 获取setting配置的资源数据
     *
     * @return
     * @throws Exception
     */
    @PATCH
    @Path("/refresh")
    JSONObject refreshData() throws Exception;

    /**
     * 刷新资源根据资源ID
     *
     * @param resourceId
     * @param tmdbDataVO
     * @return
     * @throws Exception
     */
    @PATCH
    @Path("/refresh/${resourceId}")
    JSONObject refreshDataById(@PathParam("resourceId") String resourceId, @QueryParam("") TmdbVO tmdbDataVO) throws Exception;

    /**
     * 主页获取数据
     *
     * @param page
     * @param tmdbVO
     * @return
     * @throws Exception
     */
    @GET
    @Path("/page/list/${page}")
    JSONObject getPageDataList(@PathParam("page") int page, @QueryParam("") TmdbVO tmdbVO) throws Exception;


}
