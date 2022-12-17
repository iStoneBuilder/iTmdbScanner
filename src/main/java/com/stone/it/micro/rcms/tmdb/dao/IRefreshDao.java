package com.stone.it.micro.rcms.tmdb.dao;

import com.stone.it.micro.rcms.tmdb.vo.FileInfoVO;
import com.stone.it.micro.rcms.tmdb.vo.TmdbVO;

import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/11
 * @Desc
 */
public interface IRefreshDao {

    /**
     *
     * @param resourceId
     * @return
     * @throws Exception
     */
    TmdbVO getResourceById(String resourceId)throws Exception;

    void createResource(FileInfoVO fileInfoVO) throws Exception;
    void updateResource(FileInfoVO fileInfoVO)throws Exception;
    void deleteResource(FileInfoVO fileInfoVO)throws Exception;

    void createDetailResource(TmdbVO tmdbOrgData) throws Exception;
    void updateDetailResource(TmdbVO tmdbOrgData) throws Exception;
    void deleteDetailResource(TmdbVO tmdbOrgData) throws Exception;

    List<TmdbVO> findNotMatchData(TmdbVO tmdbVO) throws Exception;

    List<TmdbVO> findResourceExists(FileInfoVO fileInfoVO) throws Exception;

    List<TmdbVO> findTmdbInfoById(TmdbVO tmdbOrgData);
}
