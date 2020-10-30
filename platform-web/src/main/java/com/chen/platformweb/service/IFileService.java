package com.chen.platformweb.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    /**
     * 原用于接收上传的附件，后来发现国产中间价不支持
     * ajax上传文件后废弃不用
     * 本方法会将接收到的文件存放于配置文件中指定的位置并返回文件的磁盘路径
     * 记录1：现在的文件存储方式不支持集群部署，会出现上传的附件在审核的时候无法现在
     * 的问题
     * @param multipartFile 接收到的文件
     * @return 文件磁盘路径
     */
    String saveFile(MultipartFile multipartFile);

    List<String> readFileTypeByFile();


//    /**
//     * 原用于生成服务器证书，后废弃
//     * @param certificate 证书实体
//     * @return cer和 pfx证书的base64编码
//     */
//    Map<String, String> getCertBase64(Certificate certificate);
}
