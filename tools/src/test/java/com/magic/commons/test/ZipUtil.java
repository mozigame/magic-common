package com.magic.commons.test;

import com.magic.api.commons.ApiLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtil
 *
 * @author zj
 * @date 2017/5/4
 */
public class ZipUtil {

    /**
     * 功能：压缩多个文件成一个zip文件
     * @param srcFile：源文件列表
     * @param zipFile：压缩后的文件
     */
    public static boolean zipFiles(File[] srcFile, File zipFile){
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < srcFile.length; i++) {
                FileInputStream in = new FileInputStream(srcFile[i]);
                out.putNextEntry(new ZipEntry(srcFile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0){
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            return true;
        }catch (Exception e){
            ApiLogger.error(String.format("zip files error! srcFile: %s, zipFile: %s", srcFile, zipFile.getName()));
        }
        return false;
    }


    public static void main(String[] args) {
        File file1 = new File("d:\\fix.txt");
        File file2 = new File("d:\\2015-11-16.xls");
        File file3 = new File("d:\\iOS 首播beta0.7.zip");
        File file4 = new File("D:\\360安全浏览器下载\\经典Photoshop_CS5入门教程(完整免费版).ppt");

        File[] files = new File[]{file1, file2, file3, file4};
        File zipFile = new File("d:\\test.zip");

        zipFiles(files, zipFile);
    }


}
