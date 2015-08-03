package com.github.yanglw.java.util;

import java.io.*;

/**
 * <p>
 * I/O 工具类。
 * </p>
 * Created by yanglw on 2015/7/30.
 */
public class IOUtil
{
    /**
     * 关闭流。
     *
     * @param closeable
     *         流。
     */
    public static void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 讲一个输入流中的内容读到一个 {@link ByteArrayOutputStream} 中。
     *
     * @param inputStream
     *         数据输入流。
     *
     * @return 包含有输入流数据的  {@link ByteArrayOutputStream} 对象。
     *
     * @throws IOException
     */
    public static ByteArrayOutputStream toByteArrayOutputStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        copy(inputStream, outputStream);

        return outputStream;
    }

    /**
     * 将输入流中的数据输出至输出流中。
     *
     * @param inputStream
     *         输入流。
     * @param outputStream
     *         输出流。
     *
     * @throws IOException
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        byte[] arrays = new byte[1024 * 4];
        int length;
        while ((length = inputStream.read(arrays)) > 0)
        {
            outputStream.write(arrays, 0, length);
        }
        outputStream.flush();
    }
}
