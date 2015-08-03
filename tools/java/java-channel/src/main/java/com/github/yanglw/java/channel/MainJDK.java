package com.github.yanglw.java.channel;

import com.github.yanglw.java.channel.util.ChannelUtil;
import com.github.yanglw.java.util.IOUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * <a href="http://tech.meituan.com/mt-apk-packaging.html">美团 {@code META-INF} 方法生成渠道 apk</a> 的 Java 实现。
 * </p>
 * <p>本例使用 JDK 自带的 zip 工具类实现文件解压缩。</p>
 * Created by yanglw on 2015/7/30.
 */
public class MainJDK
{
    public static void main(String[] args) throws Exception
    {
        String apkDirPath = args[0];
        String channelFilePath = args[1];
        String outApkDir = args[2];

        File apkDir = new File(apkDirPath);
        File outDir = new File(outApkDir);

        // 获取 apk 文件（夹）中的 apk 文件。
        File[] files = ChannelUtil.getApkList(apkDir);
        // 获取渠道列表。
        List<String> list = ChannelUtil.getChannelList(new File(channelFilePath));

        for (File file : files)
        {
            // 将源 apk 文件的信息读入内存当做缓存。
            ZipFile zipFile = new ZipFile(file);
            Map<String, byte[]> data = new LinkedHashMap<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry zipEntry = entries.nextElement();
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                IOUtil.copy(inputStream, out);
                IOUtil.close(inputStream);
                data.put(zipEntry.getName(), out.toByteArray());
                out.reset();
            }
            IOUtil.close(zipFile);

            // 根据渠道列表进行添加渠道文件操作。
            for (String channelLine : list)
            {
                // 获取渠道信息
                String[] split = ChannelUtil.splitChannelLine(channelLine);
                String channelFileName = split[0];
                String channelName = split[1];
                // 创建内存渠道 apk 文件信息输出流，用于存储渠道 apk 文件内容。
                ByteArrayOutputStream newDate = new ByteArrayOutputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(newDate));
                // 首先将渠道文件信息写入内存中的渠道 apk ，这样 Android 客户端会首先读取到这个渠道信息文件。
                zipOutputStream.putNextEntry(new ZipEntry(ChannelUtil.META_INF + channelFileName));
                zipOutputStream.closeEntry();
                // 将源 apk 文件的内容写入内存中的渠道 apk 。
                for (Map.Entry<String, byte[]> entry : data.entrySet())
                {
                    zipOutputStream.putNextEntry(new ZipEntry(entry.getKey()));
                    zipOutputStream.write(entry.getValue());
                    zipOutputStream.closeEntry();
                }
                IOUtil.close(zipOutputStream);

                // 将内存中的渠道 apk 文件写入到磁盘中。
                File apkFile = ChannelUtil.createNewApkFile(file, outDir, channelName);
                BufferedOutputStream writeToFileSteam = new BufferedOutputStream(new FileOutputStream(apkFile));
                writeToFileSteam.write(newDate.toByteArray());
                writeToFileSteam.flush();
                IOUtil.close(writeToFileSteam);
            }
        }
    }
}
