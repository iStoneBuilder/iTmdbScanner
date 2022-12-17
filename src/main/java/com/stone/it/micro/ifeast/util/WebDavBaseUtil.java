package com.stone.it.micro.ifeast.util;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.stone.it.micro.ifeast.service.TmdbConstant;
import com.stone.it.micro.ifeast.vo.FileInfoVO;
import com.stone.it.micro.ifeast.vo.SettingVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */
public class WebDavBaseUtil {

    private static Sardine sardine = null;

    static void initSardine(SettingVO settingVO) {
        sardine = new SardineImpl();
        sardine.setCredentials(settingVO.getAccount(), settingVO.getPassword());
    }

    /**
     * 获取webdav信息
     *
     * @param settingVO
     * @param filePath
     * @return
     * @throws Exception
     */
    public static List<FileInfoVO> getWebDavData(SettingVO settingVO, String filePath, boolean isMovie) throws Exception {
        // 初始化WebDav
        initSardine(settingVO);
        // 请求地址
        String uri = getRequestUri(settingVO);
        // 请求数据
        List<DavResource> resources = sardine.list(uri + filePath);
        // 处理数据
        return handleNasData(resources, settingVO, uri + filePath, isMovie);
    }

    private static List<FileInfoVO> handleNasData(List<DavResource> resources, SettingVO settingVO, String uri, boolean isMovie) {
        List<FileInfoVO> movieData = new ArrayList<>();
        for (DavResource dr : resources) {
            FileInfoVO iFile = new FileInfoVO();
            if (isMovie) {
                if (dr.isDirectory() || !checkRs(dr.getDisplayName())) {
                    continue;
                }
            } else {
                if (!dr.isDirectory() || "iTv".equals(dr.getDisplayName())) {
                    continue;
                }
            }
            iFile.setFileType(isMovie ? "movie" : "tv");
            iFile.setSettingId(settingVO.getSettingId());
            // 文件ID
            iFile.setFileId(CommonBaseUtil.getUuid());
            // 文件路径
            iFile.setFilePath(uri + dr.getDisplayName());
            handleFileName(iFile, dr.getDisplayName(), isMovie);
            movieData.add(iFile);
        }
        return movieData;
    }

    private static void handleFileName(FileInfoVO fileInfoVO, String displayName, boolean isMovie) {
        displayName = displayName.replaceAll("�", "");
        displayName = displayName.replaceAll("\uF028", "");
        if (displayName.endsWith("/")) {
            String[] names = displayName.split("/");
            displayName = names[names.length - 1];
        }
        if (isMovie) {
            String[] names = displayName.split("\\.");
            if (names.length > 1 && TmdbConstant.MEDIA_TYPE.contains(names[names.length - 1].toLowerCase(Locale.ROOT))) {
                displayName = displayName.replace("." + names[names.length - 1], "");
            }
        }
        // 文件名字
        fileInfoVO.setFileName(displayName);
    }

    private static boolean checkRs(String displayName) {
        if (displayName.startsWith(".")) {
            return false;
        }
        String[] names = displayName.split("\\.");
        if (TmdbConstant.MEDIA_TYPE.contains(names[names.length - 1].toLowerCase(Locale.ROOT))) {
            return true;
        }
        return false;
    }

    private static String getRequestUri(SettingVO setting) {
        // 拼接路径
        String reqUri = setting.getDomain();
        if (setting.getPort() != null && setting.getPort() != "") {
            reqUri = reqUri + ":" + setting.getPort();
        }
        reqUri = reqUri + setting.getRootPath();
        if (reqUri.startsWith(TmdbConstant.HTTP) || reqUri.startsWith(TmdbConstant.HTTPS)) {
            return reqUri;
        }
        return TmdbConstant.HTTP + reqUri;
    }

}
