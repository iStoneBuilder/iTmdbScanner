package com.stone.it.micro.rcms.tmdb.internal.service;

import com.stone.it.micro.rcms.tmdb.vo.FileInfoVO;
import com.stone.it.micro.rcms.tmdb.vo.SettingVO;

import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/17
 * @Desc
 */
public interface ISardineService {

    /**
     * 获取WebDav数据
     *
     * @param settingVO
     * @return
     * @throws Exception
     */
    public List<FileInfoVO> getWebDavData(SettingVO settingVO) throws Exception;

}
