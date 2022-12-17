package com.stone.it.micro.ifeast.dao;


import com.stone.it.micro.ifeast.vo.FileInfoVO;
import com.stone.it.micro.ifeast.vo.ResourceVO;

import java.util.List;

/**
 * @author cj.stone
 * @Date 2022/11/11
 * @Desc
 */
public interface IRefreshDao {

    List<FileInfoVO> findFileExist(FileInfoVO fileInfoVO) throws Exception;

    void createFileInfo(FileInfoVO fileInfoVO) throws Exception;

    void updateFileInfo(FileInfoVO fileInfoVO) throws Exception;

    void deleteFileInfo(FileInfoVO fileInfoVO) throws Exception;

    List<FileInfoVO> findNotMatchData(FileInfoVO fileInfoVO) throws Exception;

    void createFileMeta(ResourceVO resourceVO) throws Exception;

    void updateFileMeta(ResourceVO resourceVO) throws Exception;

    void deleteFileMeta(ResourceVO resourceVO) throws Exception;

}
