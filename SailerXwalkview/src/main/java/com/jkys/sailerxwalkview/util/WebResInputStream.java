package com.jkys.sailerxwalkview.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 用于包装异步获取缓存资源的输入流
 * <p/>
 * 自定义web资源缓存，当请求的资源需要异步获取时，先用该类包装一个
 * InputStream返回WebResourceResponse，并根据异步下载的状态设置
 * 当前流是否可用。
 */
public class WebResInputStream extends InputStream {

    private static final String TAG = "CacheResInputStream";

    /**
     * 当前资源是否可用锁
     */
    public final Lock mLock = new ReentrantLock();
    /**
     * 流可用等待条件
     */
    private final Condition mStreamAvailable = mLock.newCondition();
    /**
     * 是否已经下载完成（不论成功失败）
     */
    private boolean mDownloadFinished = false;
    /**
     * 真正提供数据的流
     */
    private InputStream mInputStream;
    /**
     * 资源url
     */
    private String mUrl;

    /**
     * 构造函数
     *
     * @param url 资源url
     */
    public WebResInputStream(String url) {
        mUrl = url;
    }

    /**
     * 获取资源url
     *
     * @return 资源url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * 设置输入流
     *
     * @param is 输入流
     */
    public void setInputStream(InputStream is) {
        mDownloadFinished = true;
        mLock.lock();
        try {
            mInputStream = is;
            mStreamAvailable.signalAll();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 从输入流中读取buffer.length的数据放到字节数组buffer中
     *
     * @param buffer 字节数组
     * @return 真正读取到的字节数组长度
     */
    @Override
    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    /**
     * 从输入流中读取字节数组
     *
     * @param buffer
     * @param offset
     * @param count
     * @return
     */
    @Override
    public int read(byte[] buffer, int offset, int count) {
        if (!mDownloadFinished) {
            mLock.lock();

            try {
//                mStreamAvailable.await();
                while (!mDownloadFinished) {
                    try {
                        mStreamAvailable.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
        }
        if (mInputStream == null) {
            return -1;//出错 TODO 要不要通知外部做处理
        } else {
            int byteCount = -1;
            try {
                byteCount = mInputStream.read(buffer, offset, count);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return byteCount;
        }
    }

    /**
     * 关闭输入流
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        StreamUtil.closeQuietly(mInputStream);
    }

    /**
     * 从输入流中读取数据，空实现
     *
     * @return 真正读取的字节数组
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        return 0;
    }
}
