package com.jkys.sailerxwalkview.dbservice;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/12/19
 * Time: 14:31
 * Email:AndroidZern@163.com
 */

public class SailerSQLData extends DataSupport implements Serializable {
    private String fileName; // H5文件本地文件名字
    private String fileMD5; // H5文件本地MD5值 存起来不需要每次计算了。

    public SailerSQLData(String fileName, String fileMD5) {
        this.fileName = fileName;
        this.fileMD5 = fileMD5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMD5() {
        return fileMD5;
    }

    public void setFileMD5(String fileMD5) {
        this.fileMD5 = fileMD5;
    }
}
