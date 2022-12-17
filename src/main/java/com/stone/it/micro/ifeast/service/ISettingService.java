package com.stone.it.micro.ifeast.service;

import com.alibaba.fastjson.JSONObject;
import com.stone.it.micro.ifeast.vo.SettingVO;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/12/9
 * @Desc
 */

@Path("/settingService")
@Produces("application/json")
@Consumes("application/json")
public interface ISettingService {

    @GET
    @Path("/find/list")
    List<SettingVO> getSettingList(@QueryParam("")SettingVO setting) throws Exception;

    @GET
    @Path("/find/detail/${settingId}")
    SettingVO getSettingById(@PathParam("")SettingVO settingVO) throws Exception;

    @GET
    @Path("/refresh")
    JSONObject refreshResource() throws Exception;

}
