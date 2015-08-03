package com.github.yanglw.java.channel;

import com.github.yanglw.java.channel.util.ChannelUtil;
import com.github.yanglw.java.util.FileUtil;
import com.github.yanglw.java.util.IOUtil;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * <p>
 * <a href="http://tech.meituan.com/mt-apk-packaging.html">美团 {@code META-INF} 方法生成渠道 apk</a> 的 Java 实现。
 * </p>
 * <p>本例使用 Zip4j 实现文件解压缩。</p>
 * Created by yanglw on 2015/7/30.
 */
public class MainZip4J
{
    private static void addChannelFile2Zip(File apkFile, File temp, String channelFileName) throws ZipException, FileNotFoundException
    {
        ZipFile zipFile = new ZipFile(apkFile);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setRootFolderInZip(ChannelUtil.META_INF);
        // 由于 zip4j 的 ZipParameters#setFileNameInZip(String) 方法不起作用，所以无限创建以渠道文件名称为名称的空文件加入 zip 。
        parameters.setFileNameInZip(channelFileName);
        zipFile.addFile(temp, parameters);
    }

    public static void main(String[] args) throws Exception
    {
        String apkDirPath = args[0];
        String channelListPath = args[1];
        String outApkDir = args[2];

        File apkDir = new File(apkDirPath);
        File outDir = new File(outApkDir);

        // 获取 apk 文件（夹）中的 apk 文件。
        File[] files = ChannelUtil.getApkList(apkDir);
        // 获取渠道列表。
        List<String> list = ChannelUtil.getChannelList(new File(channelListPath));

        for (File file : files)
        {
            // 将源 apk 文件读入内存当做缓存。
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = IOUtil.toByteArrayOutputStream(inputStream);
            IOUtil.close(inputStream);

            for (String channelLine : list)
            {
                String[] split = ChannelUtil.splitChannelLine(channelLine);
                String channelFileName = split[0];
                String channelName = split[1];

                // 将内存缓存文件写到磁盘中。
                File apkFile = ChannelUtil.createNewApkFile(file, outDir, channelName);
                FileOutputStream out = new FileOutputStream(apkFile);
                outputStream.writeTo(out);
                outputStream.flush();
                IOUtil.close(out);

                File temp = new File(outDir, channelFileName);
                FileUtil.createNewFile(temp);
                // 将渠道文件写入到新创建的 apk 文件中。
                addChannelFile2Zip(apkFile, temp, channelFileName);
            }
        }
    }
}
