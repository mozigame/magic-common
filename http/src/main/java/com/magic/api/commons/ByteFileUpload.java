package com.magic.api.commons;

/**
 * byte[]文件上传
 */
public class ByteFileUpload {

    /**
     * 上传文件名称
     */
    private String fileName;

    /**
     * 上传数据
     */
    private byte[] uploadData;

    public ByteFileUpload(String fileName, byte[] uploadData) {
        this.fileName = fileName;
        this.uploadData = uploadData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getUploadData() {
        return uploadData;
    }

    public void setUploadData(byte[] uploadData) {
        this.uploadData = uploadData;
    }
}
