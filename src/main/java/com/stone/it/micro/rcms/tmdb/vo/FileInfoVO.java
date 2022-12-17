package com.stone.it.micro.rcms.tmdb.vo;

import lombok.Data;

/**
 * @author cj.stone
 * @Date 2022/11/17
 * @Desc
 */
@Data
public class FileInfoVO {

    private String settingId;
    private String fileId;
    private String fileName;
    private String fileType;
    private String fileSize;
    private String filePath;
    private String fileInfo;
    private String tmdbId;
    /**
     * TV剧集信息
     */
    private String tvSeries;
    private String refreshId;


}
