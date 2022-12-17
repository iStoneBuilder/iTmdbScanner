package com.stone.it.micro.rcms.tmdb.dao;

import com.stone.it.micro.rcms.tmdb.vo.SettingVO;

import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/18
 * @Desc
 */
public interface ISettingDao {

    /**
     * findSettingList
     *
     * @return
     * @throws Exception
     */
    List<SettingVO> findSettingList() throws Exception;

}
