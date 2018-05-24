package com.jkys.sailerxwalkview.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 流操作工具类
 * <p>
 * 提供一些流的通用操作，如流与字节的转化，流的关闭等。
 */
public class StreamUtil {

    private StreamUtil() {}

    /**
     * 将输入流转变为字节数组
     * @param is
     * @return
     */
    public static byte[] streamToByteArray(InputStream is) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            byte[] buf = new byte[2048];
            int read = -1;
            while((read = is.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }
            byte[] data = bos.toByteArray();
            return data;
        }finally {
            closeQuietly(is);
            closeQuietly(bos);
        }
    }

    /**
     * Reads the contents of an InputStream into a byte[].
     * */
    public static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        int count;
        int pos = 0;
        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
            pos += count;
        }
        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        }
        return bytes;
    }


    /**
     * 关闭流，可以在流处理的finally中调用,忽略null和io错误。
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            //ignore
        }
    }
}
