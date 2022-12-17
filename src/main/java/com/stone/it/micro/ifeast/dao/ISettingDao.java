package com.stone.it.micro.ifeast.dao;

import com.stone.it.micro.ifeast.vo.SettingVO;

import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */
public interface ISettingDao {

    List<SettingVO> getSettingList(SettingVO setting) throws Exception;

}
