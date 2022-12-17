package com.stone.it.micro.rcms.tmdb.foreign.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.rcms.base.vo.HttpResponseVO;
import com.stone.it.micro.rcms.http.HttpRequestUtil;
import com.stone.it.micro.rcms.tmdb.foreign.service.INetworkService;
import com.stone.it.micro.rcms.tmdb.vo.BaseTmdbVO;
import com.stone.it.micro.rcms.tmdb.vo.SearchVO;
import com.stone.it.micro.rcms.tmdb.vo.TmdbIdVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Map;

/**
 * @author cj.stone
 * @Date 2022/11/20
 * @Desc
 */
@Named
public class NetworkService extends CommonService implements INetworkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);


    @Override
    public JSONObject getDiscover(SearchVO searchVO) throws Exception {
        // resourceType : movie/tv (电影，剧集)
        LOGGER.info("DISCOVER DATA START========");
        if (searchVO.getPage() == 0) {
            searchVO.setPage(1);
        }
        // 查询参数处理
//        JSONObject param = JSONObject.parseObject(JSON.toJSONString(searchVO));
        Map<String, String> params = handleQuery(searchVO);
        LOGGER.info("DISCOVER DATA PARAMS : " + JSONObject.toJSONString(params));

        HttpResponseVO movies = HttpRequestUtil.doGet(tmdbAddress + "/discover/movie", params);
        HttpResponseVO tvs = HttpRequestUtil.doGet(tmdbAddress + "/discover/tv", params);
        return handleResponse(movies, tvs);
    }

    private JSONObject handleResponse(HttpResponseVO movies, HttpResponseVO tvs) {
        return null;
    }

    @Override
    public JSONObject getMovieDetail(TmdbIdVO tmdbIdVO, BaseTmdbVO baseVO) throws Exception {
        String uri = "/movie/" + tmdbIdVO.getMovieId();
        return getDetail(baseVO, uri);
    }

    @Override
    public JSONObject getTvDetail(TmdbIdVO tmdbIdVO, BaseTmdbVO baseVO) throws Exception {
        String uri = "/tv/" + tmdbIdVO.getTvId();
        return getDetail(baseVO, uri);
    }

    @Override
    public JSONObject getTvSeasonDetail(TmdbIdVO tmdbIdVO, BaseTmdbVO baseVO) throws Exception {
        String uri = "/tv/" + tmdbIdVO.getTvId() + "/season/" + tmdbIdVO.getSeasonId();
        return getDetail(baseVO, uri);
    }

    @Override
    public JSONObject getTvEpisodeDetail(TmdbIdVO tmdbIdVO, BaseTmdbVO baseVO) throws Exception {
        String uri = "/tv/" + tmdbIdVO.getTvId() + "/season/" + tmdbIdVO.getSeasonId() + "/episode/" + tmdbIdVO.getEpisodeId();
        return getDetail(baseVO, uri);
    }

    @Override
    public JSONObject getSearch(SearchVO searchVO) throws Exception {
        return getTypeSearch(searchVO, "multi");
    }

    @Override
    public JSONObject getTypeSearch(SearchVO searchVO, String mediaType) throws Exception {
        if (searchVO.getPage() == 0) {
            searchVO.setPage(1);
        }
        LOGGER.info("SEARCH DATA START=======");
        // 查询参数处理
        Map<String, String> params = handleQuery(searchVO);
        LOGGER.info("SEARCH DATA PARAMS : " + JSONObject.toJSONString(params));
        HttpResponseVO discoverData = HttpRequestUtil.doGet(tmdbAddress + "/search/" + mediaType, params);
        return JSON.parseObject(discoverData.getResponseBody());
    }

    private JSONObject getDetail(BaseTmdbVO baseVO, String uri) throws Exception {
        LOGGER.info("GET DETAIL INFO START=========");
        // 查询参数处理
        Map<String, String> params = handleQuery(baseVO);
        LOGGER.info("GET DETAIL PARAMS : " + JSONObject.toJSONString(params));
        HttpResponseVO discoverData =
                HttpRequestUtil.doGet(tmdbAddress + uri, params);
        return JSON.parseObject(discoverData.getResponseBody());
    }

}