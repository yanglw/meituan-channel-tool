/**
 * 代码来自 http://www.cnblogs.com/ct2011/p/4152323.html 。
 * 稍作修改。
 */
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AndroidGetChannel
{
	/**
     * 从apk中获取渠道信息。
     *
     * @param context
     *         上下文
     * @param channelKey
     *         渠道文件的名称识别字段。
     * @param split
     *         渠道文件的名称识别字段与渠道信息的分隔符。
     * @param defaultChannel
     *         获取渠道号失败时返回的默认渠道信息。
     *
     * @return 渠道信息，如果从渠道文件获取失败，则返回 {@code defaultChannel} 。
     */
    private static String getChannelFromApk(Context context,
                                            String channelKey, String split,
                                            String defaultChannel)
    {
        //从apk包中获取
        ApplicationInfo appInfo = context.getApplicationInfo();
        String sourceDir = appInfo.sourceDir;
        String key = "META-INF/" + channelKey;
        String ret = null;
        ZipFile zipfile = null;
        try
        {
            zipfile = new ZipFile(sourceDir);
            Enumeration<? extends ZipEntry> entries = zipfile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(key))
                {
                    ret = entryName;
                    break;
                }
            }
        }
        catch (IOException e)
        {
            return defaultChannel;
        }
        finally
        {
            if (zipfile != null)
            {
                try
                {
                    zipfile.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(ret))
        {
            return defaultChannel;
        }

        String[] strings = ret.split(split);
        if (strings.length > 1)
        {
            return ret.substring(strings[0].length() + 1);
        }
        else
        {
            return defaultChannel;
        }
    }
}