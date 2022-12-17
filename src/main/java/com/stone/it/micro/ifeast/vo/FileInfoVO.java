package com.stone.it.micro.ifeast.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/12/10
 * @Desc
 */
@Data
public class FileInfoVO {

    /**
     * 配置ID
     */
    private String settingId;
    /**
     * 文件ID
     */
    private String fileId;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * tv,movie
     */
    private String fileType;
    /**
     * 文件大小
     */
    private String fileSize;
    /**
     * 资源路径
     */
    private String filePath;
    /**
     * 文件信息
     */
    private String fileInfo;

    private String tmdbId;
    private String refreshId;

}
