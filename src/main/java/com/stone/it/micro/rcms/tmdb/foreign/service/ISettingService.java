package com.stone.it.micro.rcms.tmdb.foreign.service;

import com.stone.it.micro.rcms.tmdb.vo.SettingVO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * 配置资源
 *
 * @author cj.stone
 * @Date 2022/11/11
 * @Desc
 */
@Path("/settingService")
@Produces("application/json")
@Consumes("application/json")
public interface ISettingService {

    /**
     * 获取配置列表
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path("/list")
    List<SettingVO> getServerSetting() throws Exception;

}
