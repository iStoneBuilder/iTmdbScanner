package com.stone.it.micro.rcms.tmdb.internal.service.impl;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.stone.it.micro.rcms.base.util.UUIDUtil;
import com.stone.it.micro.rcms.tmdb.foreign.service.TmdbConstant;
import com.stone.it.micro.rcms.tmdb.internal.service.ISardineService;
import com.stone.it.micro.rcms.tmdb.vo.FileInfoVO;
import com.stone.it.micro.rcms.tmdb.vo.SettingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author cj.stone
 * @Date 2022/11/17
 * @Desc
 */
@Named
public class SardineService implements ISardineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SardineService.class);

    private Sardine sardine = null;

    void initSardine(SettingVO settingVO) {
        if (sardine == null) {
            sardine = new SardineImpl();
            sardine.setCredentials(settingVO.getUserAccount(), settingVO.getPassword());
        }
    }

    @Override
    public List<FileInfoVO> getWebDavData(SettingVO settingVO) throws Exception {
        boolean isMovie = settingVO.getRootPath().contains("iMovie");
        List<FileInfoVO> list = new ArrayList<>();
        // 初始化WebDav
        initSardine(settingVO);
        // 获取请求连接
        String reqUri = getHttpUri(settingVO);
        // 请求数据
        List<DavResource> resources = sardine.list(reqUri);
        FileInfoVO fileInfoVO = null;
        // 处理数据
        for (DavResource res : resources) {
            fileInfoVO = new FileInfoVO();
            fileInfoVO.setSettingId(settingVO.getSettingId());
            // 文件ID
            fileInfoVO.setFileId(UUIDUtil.getUuid());
            // 文件路径
            fileInfoVO.setFilePath(res.getPath());
            if (isMovie) {
                if (res.isDirectory()) {
                    continue;
                }
                if (!isMovieFile(res.getDisplayName())) {
                    continue;
                }
                fileInfoVO.setFileType("movie");
            } else {
                if (!res.isDirectory()) {
                    continue;
                }
                if ("iTv".equals(res.getDisplayName())) {
                    continue;
                }
                fileInfoVO.setFileType("tv");
            }
            handleFileName(fileInfoVO, res.getDisplayName(), isMovie);
            list.add(fileInfoVO);
        }
        return list;
    }

    private boolean isMovieFile(String displayName) {
        if (displayName.startsWith(".")) {
            return false;
        }
        String[] names = displayName.split("\\.");
        if (TmdbConstant.MEDIA_TYPE.contains(names[names.length - 1].toLowerCase(Locale.ROOT))) {
            return true;
        }
        return false;
    }

    private void handleFileName(FileInfoVO fileInfoVO, String displayName, boolean isMovie) {
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

    public String getHttpUri(SettingVO settingVO) {
        String reqUri = settingVO.getDomain();
        if (!reqUri.startsWith(TmdbConstant.HTTP)) {
            reqUri = TmdbConstant.HTTP + reqUri;
        } else if (reqUri.startsWith(TmdbConstant.HTTPS)) {
            reqUri = TmdbConstant.HTTPS + reqUri;
        }
        if (settingVO.getPort() != null) {
            reqUri = reqUri + ":" + settingVO.getPort();
        }
        return reqUri + settingVO.getRootPath();
    }
}
