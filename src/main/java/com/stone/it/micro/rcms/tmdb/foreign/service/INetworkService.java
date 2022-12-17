package com.stone.it.micro.rcms.tmdb.foreign.service;

import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.rcms.tmdb.vo.BaseTmdbVO;
import com.stone.it.micro.rcms.tmdb.vo.SearchVO;
import com.stone.it.micro.rcms.tmdb.vo.TmdbIdVO;

import javax.ws.rs.*;

/**
 * @author cj.stone
 * @Date 2022/11/20
 * @Desc
 */
@Path("/getDataService")
@Produces("application/json")
@Consumes("application/json")
public interface INetworkService {

    /**
     * 发现数据
     *
     * @param searchVO
     * @return
     * @throws Exception
     */
    @GET
    @Path("/discover")
    public JSONObject getDiscover(SearchVO searchVO)throws Exception;

    @GET
    @Path("/movie/${movieId}")
    JSONObject getMovieDetail(@PathParam("") TmdbIdVO tmdbIdVO, @QueryParam("") BaseTmdbVO baseVO) throws Exception;

    @GET
    @Path("/tv/${tvId}")
    JSONObject getTvDetail(@PathParam("")TmdbIdVO tmdbIdVO, @QueryParam("") BaseTmdbVO baseVO) throws Exception;

    @GET
    @Path("/tv/${tvId}/${seasonId}")
    JSONObject getTvSeasonDetail(@PathParam("")TmdbIdVO tmdbIdVO, @QueryParam("") BaseTmdbVO baseVO) throws Exception;

    @GET
    @Path("/tv/${tvId}/${seasonId}/${episodeId}")
    JSONObject getTvEpisodeDetail(@PathParam("")TmdbIdVO tmdbIdVO, @QueryParam("") BaseTmdbVO baseVO) throws Exception;

    /**
     * 查询数据
     *
     * @param tmdbVO
     * @return
     * @throws Exception
     */
    JSONObject getSearch(SearchVO tmdbVO) throws Exception;

    JSONObject getTypeSearch(SearchVO searchVO,String mediaType) throws Exception;

}
