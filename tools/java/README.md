# Java 实现的美团渠道打包代码

Java 对 Zip 文件的处理，使用了两种方式，因此也有两份代码： `MainJDK` 和 `MainZip4J` 。

至于两个代码的执行效率如下：
- 小于 1M 大小的 apk ， `MainJDK` 使用时间低于 `MainZip4J` 。
- 大于 1M 大小的 apk ， `MainJDK` 使用时间高于 `MainZip4J` 。
