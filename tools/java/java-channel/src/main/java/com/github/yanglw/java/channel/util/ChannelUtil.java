package com.github.yanglw.java.channel.util;

import com.github.yanglw.java.util.FileUtil;
import com.github.yanglw.java.util.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 渠道工具类。
 * Created by yanglw on 2015/7/31.
 */
public class ChannelUtil
{
    /**
     * 渠道文件名称与渠道号分隔符。前面的是渠道文件名称，后面的是渠道号。
     * 渠道文件名称即 {@code META-INF/} 目录下渠道文件的名称。
     */
    public static final String SPLIT_CHARACTER = "-";
    /** apk 文件中的 {@code META-INF} 文件夹的名称。 */
    public static final String META_INF = "META-INF/";

    /**
     * <p>获取目录中的所有有效的 apk 。</p>
     * <p>所谓的有效的 apk 是指：文件后缀为 {@code apk} 且文件名中不包含 {@code -unaligned} 。</p>
     *
     * @param file
     *         目录 file 。
     *
     * @return 返回 {@code file} 中所有有效的 apk 。
     * 如果 {@code file} 为一个 apk 文件，则返回一个包含  {@code file} 的  {@code File[]} 。
     */
    public static File[] getApkList(File file)
    {
        if (file == null)
        {
            return new File[0];
        }

        if (checkFileIsApk(file))
        {
            return new File[]{file};
        }

        return file.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return checkFileIsApk(pathname);
            }
        });
    }

    /**
     * <p>判断一个文件是否是有效的 apk 文件。</p>
     * <p>所谓的有效的 apk 是指：文件后缀为 {@code apk} 且文件名中不包含 {@code -unaligned} 。</p>
     *
     * @param file
     *         待判断的文件。
     *
     * @return true：有效的 apk 文件；false：不是有效的 apk 文件。
     */
    private static boolean checkFileIsApk(File file)
    {
        if (file.isDirectory())
        {
            return false;
        }
        String name = file.getName();
        return name.endsWith(".apk") && !name.contains("-unaligned");
    }

    /**
     * 根据旧的 apk 文件和渠道名称创建新的 apk 文件。
     *
     * @param oldFile
     *         旧的 apk 文件。
     * @param newFileDir
     *         新的 apk 文件存放的目录。
     * @param channel
     *         渠道号。
     *
     * @return 新的 apk 文件。
     */
    public static File createNewApkFile(File oldFile, File newFileDir, String channel) throws IOException
    {
        String newName = buildNewApkFileNameByChannel(oldFile, channel);
        File file = new File(newFileDir, newName);
        FileUtil.createNewFile(file);
        return file;
    }

    /**
     * 根据旧的 apk 文件的名称和渠道名称创建新的 apk 文件的名称。
     *
     * @param oldFile
     *         旧的 apk 文件。
     * @param channel
     *         渠道号。
     *
     * @return 新的 apk 文件名称。
     */
    public static String buildNewApkFileNameByChannel(File oldFile, String channel)
    {
        String name = oldFile.getName();

        if (channel == null)
        {
            return name;
        }

        if (!channel.startsWith("-"))
        {
            channel = "-" + channel;
        }

        int index = name.lastIndexOf('.');
        String startStr = name.substring(0, index);
        String endStr = name.substring(index, name.length());
        return startStr + channel + endStr;
    }

    /**
     * 从渠道列表文件中获取渠道列表。
     * 关于渠道列表文件的每一行内容，有以下准则：
     * <ol>
     * <li>一行只有一个渠道号。</li>
     * <li>每一行均需要含有 {@link #SPLIT_CHARACTER} 字符。</li>
     * <li>{@link #SPLIT_CHARACTER} 字符前为写入 apk 文件的渠道文件的名字，字符后为写入 apk 文件名称的内容。</li>
     * <li>以 {@code #} 开头的行将会被忽略。</li>
     * </ol>
     * 例如，在这么一个渠道列表文件中：
     * <pre>
     *     channel_channel1-channel1
     *     #channel_channel2-channel2
     * </pre>
     * 第一行为有效的数据，第二行会被自动忽略。
     *
     * @param file
     *         渠道列表文件。
     *
     * @return 渠道列表。
     *
     * @throws IOException
     */
    public static List<String> getChannelList(File file) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        ArrayList<String> list = new ArrayList<>();
        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith("#"))
            {
                continue;
            }
            int index = line.indexOf(SPLIT_CHARACTER);
            if (index <= 0 || index >= line.length())
            {
                continue;
            }
            list.add(line);
        }
        IOUtil.close(reader);
        return list;
    }

    /**
     * 拆分渠道信息。
     * @param line  渠道列表文件中的关于渠道信息的一行内容。
     * @return  {@link String#split(String)}
     */
    public static String[] splitChannelLine(String line)
    {
        return line.split(SPLIT_CHARACTER);
    }
}
