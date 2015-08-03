package com.github.yanglw.java.util;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 文件处理工具类。
 * </p>
 * Created by yanglw on 2015/7/30.
 */
public class FileUtil
{
    /**
     * 创建文件。如果文件文件不存在，会创建文件所在目录以及文件本身；如果文件存在，则会删除就文件再创建新文件。
     *
     * @param file
     *         file 实例对象。
     *
     * @return true：新文件创建成功；新文件创建失败。
     */
    public static boolean createNewFile(File file) throws IOException
    {
        return createNewFile(file, true);
    }

    /**
     * 创建文件。
     *
     * @param file
     *         file 实例对象。
     * @param deleteOldFile
     *         如果所要创建的文件存在，是否删除旧文件。
     *
     * @return true：文件创建成功；文件创建失败。
     */
    public static boolean createNewFile(File file, boolean deleteOldFile) throws IOException
    {
        if (!file.exists())
        {
            if (!file.getParentFile().exists())
            {
                if (!file.getParentFile().mkdirs())
                {
                    return false;
                }
            }
        }
        else
        {
            if (!deleteOldFile)
            {
                return true;
            }

            if (!file.delete())
            {
                return false;
            }
        }

        return file.createNewFile();
    }
}

