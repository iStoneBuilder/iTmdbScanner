package com.stone.it.micro.ifeast.service.impl;

import com.stone.it.micro.ifeast.util.CommonBaseUtil;
import com.stone.it.micro.ifeast.vo.SearchVO;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cj.stone
 * @Date 2022/11/9
 * @Desc
 */
public class CommonService {

    protected final static String DEFAULT_LANGUAGE = "zh-CN";
    private final static String LANGUAGE_KEY = "language";
    protected final static String MOVIE_KEY = "movie";
    protected final static String LOC_KEY = "loc";

    @Value(value = "${tmdb.api.address}")
    protected String tmdbAddress;
    @Value(value = "${tmdb.api.key}")
    private String apiKey;

    public Map<String, String> handleQuery(Object query) throws Exception {
        Map<String, String> params = new HashMap<>();
        params = CommonBaseUtil.convertToMap(query);
        // 设置api_key
        params.put("api_key", apiKey);
        // 判断语言是否传语言，否设置默认值
        if(!params.containsKey(LANGUAGE_KEY)){
            params.put(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        }
        return params;
    }


    public void handleFileName(SearchVO searchVO, String fileName) {
        // 处理（2022）
        fileName = fileName.split(" \\(")[0];
        // TV 判断是否是剧集
        fileName = fileName.split(" S")[0];
        fileName = fileName.replaceAll(" ", "%20");
        searchVO.setQuery(fileName);
    }

}
