package com.jkys.sailerxwalkview.event;

/**
 * Created by on
 * Author: Zern
 * DATE: 16/12/28
 * Time: 14:24
 * Email:AndroidZern@163.com
 */

public class CopyAssetOrDownLoadStatus {
    // 100 表示 开始了， 200 表示更新完成。 300 表示其他，暂时未定
    private int copyStatus;

    public static final int Start = 100;
    public static final int Finish = 200;
    public static final int Fail = 300;
    public static final int DownFileError = 400; // 下载文件出错! 新增院内。
    public static final int DownFileFinish = 500; // 下载文件成功!。

    public CopyAssetOrDownLoadStatus(int copyStatus) {
        this.copyStatus = copyStatus;
    }

    public int getCopyStatus() {
        return copyStatus;
    }
}
