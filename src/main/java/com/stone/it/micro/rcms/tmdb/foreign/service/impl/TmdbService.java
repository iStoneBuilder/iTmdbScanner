package com.stone.it.micro.rcms.tmdb.foreign.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.rcms.base.util.UUIDUtil;
import com.stone.it.micro.rcms.tmdb.dao.IRefreshDao;
import com.stone.it.micro.rcms.tmdb.dao.ITmdbDao;
import com.stone.it.micro.rcms.tmdb.foreign.service.INetworkService;
import com.stone.it.micro.rcms.tmdb.foreign.service.ISettingService;
import com.stone.it.micro.rcms.tmdb.foreign.service.ITmdbService;
import com.stone.it.micro.rcms.tmdb.internal.service.ISardineService;
import com.stone.it.micro.rcms.tmdb.vo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/20
 * @Desc
 */
@Named
public class TmdbService extends CommonService implements ITmdbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmdbService.class);

    @Inject
    private ITmdbDao tmdbDao;

    @Inject
    private IRefreshDao refreshDao;

    @Inject
    private INetworkService networkService;

    @Inject
    private ISettingService settingService;

    @Inject
    private ISardineService sardineService;


    @Override
    public JSONObject refreshData() throws Exception {
        JSONObject result = new JSONObject();
        result.put("result", "refresh success");
        if (StringUtils.isNotEmpty(refreshId)) {
            result.put("result", "loading");
            // 正在更新跳过
            return result;
        }
        refreshId = UUIDUtil.getUuid();
        try {
            // 1：读取配置的资源（smb/webdav），更新数据库
            readNasResource();
            // 2：删除不存在数据
            deleteNotExistData();
            // 重置刷新ID
            refreshId = UUIDUtil.getUuid();
            // 3：查询数据库数据,刷新数据
            readDbResource();
        } finally {
            refreshId = null;
        }
        return result;
    }

    private void deleteNotExistData() throws Exception {
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setRefreshId(refreshId);
        // 删除没有刷新的数据
        refreshDao.deleteResource(fileInfoVO);
    }

    private void readNasResource() throws Exception {
        // 初始化配置
        getTmdbSetting(null);
        for (int i = 0; i < settings.size(); i++) {
            SettingVO iSetting = settings.get(i);
            String rootPath = iSetting.getRootPath();
            if ("webdav".equals(iSetting.getType())) {
                handleWebDavNasMovie(iSetting, rootPath);
                handleWebDavNasTv(iSetting, rootPath);
            }
        }

    }

    private SettingVO getTmdbSetting(String settingId) throws Exception {
        settings = settingService.getServerSetting();
        if (StringUtils.isEmpty(settingId)) {
            return null;
        }
        for (SettingVO setting : settings) {
            if (settingId.equals(setting.getSettingId())) {
                return setting;
            }
        }
        return null;
    }

    private void handleWebDavNasMovie(SettingVO settingVO, String rootPath) throws Exception {
        settingVO.setRootPath(rootPath + "/iMovie/");
        List<FileInfoVO> lists = sardineService.getWebDavData(settingVO);
        for (int li = 0; li < lists.size(); li++) {
            operationData(lists.get(li));
        }
    }

    private void operationData(FileInfoVO fileInfoVO) throws Exception {
        fileInfoVO.setRefreshId(refreshId);
        List<TmdbVO> resExist = refreshDao.findResourceExists(fileInfoVO);
        // 不存在插入
        if (resExist.size() == 0) {
            LOGGER.info("get new file , name : " + fileInfoVO.getFileName());
            refreshDao.createResource(fileInfoVO);
        } else {
            // 存在更新刷新ID
            fileInfoVO.setFileId(resExist.get(0).getResourceId());
            fileInfoVO.setTmdbId(resExist.get(0).getTmdbId());
            refreshDao.updateResource(fileInfoVO);
        }
    }

    private void handleWebDavNasTv(SettingVO settingVO, String rootPath) throws Exception {
        settingVO.setRootPath(rootPath + "/iTv/");
        String res_path = settingVO.getRootPath();
        List<FileInfoVO> lists = sardineService.getWebDavData(settingVO);
        for (int li = 0; li < lists.size(); li++) {
            // 非当前目录
            if (!res_path.equals(lists.get(li).getFilePath())) {
                operationData(lists.get(li));
            }
        }
    }

    /**
     * 查询数据库
     *
     * @throws Exception
     */
    private void readDbResource() throws Exception {
        TmdbVO tmdbVO = new TmdbVO();
        tmdbVO.setPage(1);
        // 刷新未手动确认的数据
        tmdbVO.setIsConfirm("0");
        tmdbVO.setRefreshId(refreshId);
        List<TmdbVO> list = null;
        do {
            list = refreshDao.findNotMatchData(tmdbVO);
            // 刷新ID排除查询
            list.forEach(t -> {
                FileInfoVO fileInfoVO = new FileInfoVO();
                fileInfoVO.setRefreshId(refreshId);
                fileInfoVO.setFileId(t.getResourceId());
                fileInfoVO.setTmdbId(t.getTmdbId());
                try {
                    refreshDao.updateResource(fileInfoVO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            // 查询tmdb数据
            handleDbDataToRefresh(list);
            // 更新查询page
            tmdbVO.setPage(tmdbVO.getPage() + 1);
        } while (list.size() == 20);
    }

    private void handleDbDataToRefresh(List<TmdbVO> list) throws Exception {
        SearchVO searchVO = new SearchVO();
        // 循环处理数据
        for (int i = 0; i < list.size(); i++) {
            searchVO.setPage(1);
            TmdbVO dbDataVO = list.get(i);
            // 查询条件
            String searchKey = dbDataVO.getResourceName();
            // 处理（2022）
            searchKey = searchKey.split(" \\(")[0];
            // TV 判断是否是剧集
            searchKey = searchKey.split(" S")[0];
            // 处理数据
            handleNameSearch(searchVO, dbDataVO, searchKey);
        }
    }

    private void handleNameSearch(SearchVO searchVO, TmdbVO dbDataVO, String searchKey) throws Exception {
        searchVO.setQuery(searchKey.replaceAll(" ", "%20"));
        // 查询数据，返回为空处理成功，不为空判断是否有其他分页
        JSONObject newData = handleSearchData(searchVO, dbDataVO, searchVO.getQuery());
        if (newData != null) {
            Integer totalPage = newData.getInteger("total_pages");
            for (int j = 2; j < totalPage + 1; j++) {
                searchVO.setPage(j);
                // 查询数据
                newData = handleSearchData(searchVO, dbDataVO, searchVO.getQuery());
                if (newData == null) {
                    break;
                }
            }
        }
    }


    private JSONObject handleSearchData(SearchVO searchVO, TmdbVO dbDataVO, String query) throws Exception {
        // 都用关键字查询
        JSONObject newData = networkService.getTypeSearch(searchVO, dbDataVO.getMedia_type());
        if (StringUtils.isEmpty(searchVO.getLanguage())) {
            dbDataVO.setLanguage(DEFAULT_LANGUAGE);
        }
        if (newData.getJSONArray("results") == null) {
            return null;
        }
        // 处理数据
        return handleKeyWordData(newData, dbDataVO, query);
    }

    private JSONObject handleKeyWordData(JSONObject newData, TmdbVO dbDataVO, String resourceName) throws Exception {
        dbDataVO.setIsConfirm("0");
        JSONArray results = newData.getJSONArray("results");
        // 先查询是否有ID相等的
        JSONObject matchData = null;
        if (results.size() == 1) {
            matchData = results.getJSONObject(0);
        } else {
            for (int j = 0; j < results.size(); j++) {
                matchData = null;
                JSONObject keyData = results.getJSONObject(j);
                // 判断是否匹配处理数据
                String title = keyData.getString("title") == null ? "" : keyData.getString("title");
                String orgTitle = keyData.getString("original_title") == null ? "" : keyData.getString("original_title");
                String name = keyData.getString("name") == null ? "" : keyData.getString("name");
                String orgName = keyData.getString("original_name") == null ? "" : keyData.getString("original_name");
                // 判断名称是否相等
                if (resourceName.equals(title) || resourceName.equals(name)) {
                    matchData = keyData;
                    break;
                }
                if (resourceName.equals(orgTitle) || resourceName.equals(orgName)) {
                    matchData = keyData;
                    break;
                }
                if (resourceName.equals(title.replaceAll(".", "")) || resourceName.equals(name.replaceAll(".", ""))) {
                    matchData = keyData;
                    break;
                }
            }
        }
        if (matchData != null) {
            matchData.put("media_type", dbDataVO.getMedia_type());
            handleResourceData(dbDataVO, matchData);
            return null;
        }
        return newData;
    }

    private void handleResourceData(TmdbVO tmdbOrgData, JSONObject resource) throws Exception {
        if (resource.getString("media_type").equals(MOVIE_KEY)) {
            resource.put("original_name", resource.getString("original_title"));
            resource.put("name", resource.getString("title"));
            resource.remove("original_title");
            resource.remove("title");
        }
        // tmdbId
        tmdbOrgData.setTmdbId(resource.getString("id"));
        // 处理类型上映国家，TV详细信息
        handleGenreCountry(tmdbOrgData, resource);
        String release_date = resource.getString("release_date");
        if (StringUtils.isEmpty(release_date)) {
            release_date = resource.getString("first_air_date");
        }
        // 上映时间
        tmdbOrgData.setRelease_date(release_date);
        // 显示标题
        tmdbOrgData.setTitle(resource.getString("name"));
        // 是否成人
        tmdbOrgData.setInclude_adult(resource.getString("adult"));
        LOGGER.info("INFO DETAIL : " + JSON.toJSONString(tmdbOrgData));
        // 设置JSON数据
        tmdbOrgData.setResourceData(JSON.toJSONString(resource));
        // 处理数据
        handleDataToDb(tmdbOrgData, resource);
    }

    private void handleDataToDb(TmdbVO tmdbOrgData, JSONObject resource) throws Exception {
        List<TmdbVO> list = refreshDao.findTmdbInfoById(tmdbOrgData);
        // 创建详细信息
        if (list.size() == 0) {
            refreshDao.createDetailResource(tmdbOrgData);
        }
        FileInfoVO fileInfoVO = new FileInfoVO();
        // 刷新ID
        fileInfoVO.setRefreshId(refreshId);
        // 资源ID
        fileInfoVO.setFileId(tmdbOrgData.getResourceId());
        // TMDB ID
        fileInfoVO.setTmdbId(tmdbOrgData.getTmdbId());
        // 更新资源表
        refreshDao.updateResource(fileInfoVO);
    }

    private void handleGenreCountry(TmdbVO tmdbOrgData, JSONObject resource) throws Exception {
        JSONObject detailData = null;
        TmdbIdVO tmdbIdVO = new TmdbIdVO();
        // 查询数据
        if (MOVIE_KEY.equals(tmdbOrgData.getMedia_type())) {
            tmdbIdVO.setMovieId(tmdbOrgData.getTmdbId());
            detailData = networkService.getMovieDetail(tmdbIdVO, new BaseTmdbVO());
        } else {
            tmdbIdVO.setTvId(tmdbOrgData.getTmdbId());
            detailData = networkService.getTvDetail(tmdbIdVO, new BaseTmdbVO());
            // seasons
            JSONArray seasons = detailData.getJSONArray("seasons");
            if (seasons.size() == 1) {
                handleTvDetailInfo(tmdbOrgData, resource, seasons.getJSONObject(0));
            }
            if (seasons.size() > 1) {
                int seasionId = -1;
                String resourceName = tmdbOrgData.getResourceName();
                if (resourceName.contains(" S")) {
                    String[] names = resourceName.split(" S");
                    resourceName = resourceName.replace(" S" + names[names.length - 1], " ");
                    seasionId = Integer.parseInt(names[names.length - 1]);
                }
                for (int i = 0; i < seasons.size(); i++) {
                    if (seasionId == seasons.getJSONObject(i).getInteger("season_number")) {
                        String sNum = seasons.getJSONObject(i).getString("name");
                        resource.put("name", resourceName + sNum);
                        handleTvDetailInfo(tmdbOrgData, resource, seasons.getJSONObject(i));
                        break;
                    }
                    if (resourceName.equals(seasons.getJSONObject(i).getString("name"))) {
                        resource.put("name", resourceName);
                        handleTvDetailInfo(tmdbOrgData, resource, seasons.getJSONObject(i));
                        break;
                    }
                }
            }
        }
        if (detailData != null) {
            JSONArray genres = detailData.getJSONArray("genres");
            StringBuffer genre = new StringBuffer();
            for (int i = 0; i < genres.size(); i++) {
                genre.append(genres.getJSONObject(i).getString("name")).append(",");
            }
            // 类型
            tmdbOrgData.setGenres(genre.toString());
            JSONArray countries = detailData.getJSONArray("production_countries");
            StringBuffer country = new StringBuffer();
            for (int i = 0; i < countries.size(); i++) {
                country.append(countries.getJSONObject(i).getString("iso_3166_1")).append(",");
            }
            JSONArray orgCountry = detailData.getJSONArray("origin_country");
            if (orgCountry != null) {
                for (int i = 0; i < orgCountry.size(); i++) {
                    if (!country.toString().contains(orgCountry.getString(i))) {
                        country.append(orgCountry.getString(i)).append(",");
                    }
                }
            }
            // 上映地点
            tmdbOrgData.setProduction_countries(country.toString());
        }
    }

    private void handleTvDetailInfo(TmdbVO tmdbOrgData, JSONObject resource, JSONObject tvDetail) {
        // 设置为tmdbId
        tmdbOrgData.setTmdbId(tvDetail.getString("id"));
        // 设置
        resource.put("poster_path", tvDetail.getString("poster_path"));
        if (StringUtils.isNotEmpty(tvDetail.getString("overview"))) {
            resource.put("overview", tvDetail.getString("overview"));
        }
        resource.put("release_date", tvDetail.getString("air_date"));
        resource.put("episode_count", tvDetail.getInteger("episode_count"));
    }


    @Override
    public JSONObject refreshDataById(String resourceId, TmdbVO tmdbDataVO) throws Exception {
        // 根据资源ID查询数据
        TmdbVO tmdbOrgData = refreshDao.getResourceById(resourceId);
        // 解析选择数据
        JSONObject matchData = JSON.parseObject(tmdbDataVO.getResourceData());
        // 手动确认过的刷新不会再处理
        tmdbOrgData.setIsConfirm("0");
        // 解析数据
        handleResourceData(tmdbOrgData, matchData);
        return new JSONObject();
    }

    @Override
    public JSONObject getPageDataList(int page, TmdbVO tmdbVO) throws Exception {
        LOGGER.info("-------------");
        JSONObject jsonObject = new JSONObject();
        // 判断是否本地模式
//        if (LOC_KEY.equals(tmdbVO.getMode())) {
//            // jsonObject = getDbDataList(searchVO);
//        } else {
//            // 网络模式判断是否有查询条件
//            if (StringUtils.isNotEmpty(searchVO.getQuery())) {
//                // jsonObject = searchService.getSearch(searchVO);
//            } else {
//                // jsonObject = discoverService.getDiscover(null);
//            }
//        }
        return jsonObject;
    }


}
