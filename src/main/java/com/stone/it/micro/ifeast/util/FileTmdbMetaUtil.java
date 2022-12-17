package com.stone.it.micro.ifeast.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.ifeast.vo.FileInfoVO;
import com.stone.it.micro.ifeast.vo.ResourceVO;
import com.stone.it.micro.ifeast.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author cj.stone
 * @Date 2022/12/10
 * @Desc
 */
public class FileTmdbMetaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTmdbMetaUtil.class);

    private static int total = 1;

    public static ResourceVO getTmdbMeta(Map<String, String> params, FileInfoVO fileInfoVO, String searchUri) throws Exception {
        total = 1;
        JSONArray data = getNetData(searchUri, params);
        if (data.size() == 1) {
            return handleTmdbResource(data.getJSONObject(0));
        }
        if (data.size() > 1) {
            return matchTmdbResource(params, searchUri, fileInfoVO, data);
        }
        return null;
    }

    private static JSONArray getNetData(String searchUri, Map<String, String> params) throws Exception {
        ResponseVO response = HttpRequestUtil.doGet(searchUri, params, null);
        if ("200".equals(response.getStatusCode())) {
            JSONObject jsonObject = JSONObject.parseObject(response.getResponseBody());
            total = jsonObject.getInteger("total_pages");
            return jsonObject.getJSONArray("results");
        }
        return new JSONArray();
    }

    private static ResourceVO matchTmdbResource(Map<String, String> params, String searchUri, FileInfoVO fileInfoVO,
                                                JSONArray datas) throws Exception {
        JSONObject matchData = getMatchResource(datas, fileInfoVO);
        if (matchData != null) {
            return handleTmdbResource(matchData);
        }
        for (int i = 1; i < total; i++) {
            params.put("page", String.valueOf(i + 1));
            JSONArray data = getNetData(searchUri, params);
            matchData = getMatchResource(datas, fileInfoVO);
            if (matchData != null) {
                return handleTmdbResource(matchData);
            }
        }
        return null;
    }

    private static JSONObject getMatchResource(JSONArray datas, FileInfoVO fileInfoVO) {
        return null;
    }

    private static ResourceVO handleTmdbResource(JSONObject jsonObject) {
        return null;
    }
}
