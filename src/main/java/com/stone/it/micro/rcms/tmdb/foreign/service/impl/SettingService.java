package com.stone.it.micro.rcms.tmdb.foreign.service.impl;

import com.stone.it.micro.rcms.tmdb.dao.ISettingDao;
import com.stone.it.micro.rcms.tmdb.foreign.service.ISettingService;
import com.stone.it.micro.rcms.tmdb.vo.SettingVO;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/11
 * @Desc
 */
@Named
public class SettingService implements ISettingService {

    @Inject
    private ISettingDao settingDao;
    @Override
    public List<SettingVO> getServerSetting() throws Exception {
        return settingDao.findSettingList();
    }
}
