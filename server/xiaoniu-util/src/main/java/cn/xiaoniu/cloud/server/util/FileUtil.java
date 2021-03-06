package cn.xiaoniu.cloud.server.util;

/**
 * @author 孔明
 * @date 2020/4/26 10:30
 * @description cn.xiaoniu.cloud.server.util.FileUtil
 */
public final class FileUtil {

    private FileUtil() {
    }

    /**
     * 使用MD5值计算一个Long类型
     *
     * @param md5
     * @return
     */
    public static long md5Code(String md5) {
        AssertUtil.isNotNull(md5, "参数md5不能为空");
        AssertUtil.isTrue(md5.length() == 32, "当前方法只支持32位md5值计算");

        char[] source = md5.toCharArray();
        char[] temp = new char[4];
        long result = 0;
        long sum;
        for (int i = 0; i < 8; i++) {
            System.arraycopy(source, i * temp.length, temp, 0, temp.length);
            sum = 0;
            for (int i1 = 0; i1 < temp.length; i1++) {
                long f = ((long) temp[i1]) << (16 * (3 - i1));
                sum = (sum | f);
            }
            result += sum;
        }
        return result;
    }

    /**
     * 加密
     *
     * @param sources 源
     * @param key     秘钥
     * @return
     */
    public static byte[] encode(final byte[] sources, byte key) {
        return code(sources, key, true);
    }

    /**
     * 解密
     *
     * @param sources
     * @param key
     * @return
     */
    public static byte[] decode(final byte[] sources, byte key) {
        return code(sources, key, false);
    }

    private static byte[] code(final byte[] sources, byte key, boolean isEncode) {
        AssertUtil.isNotNull(sources, "参数source不能为空！");
        AssertUtil.isTrue(sources.length > 0, "参数source的长度必须大于0！");
        AssertUtil.isNotNull(key, "参数key不能为空！");

        byte[] temp = new byte[sources.length];
        System.arraycopy(sources, 0, temp, 0, sources.length);

        for (int i = 0; i < temp.length; i++) {
            if (isEncode) {
                temp[i] = (byte) (((temp[i] & 0xff) + key) % 256);
            } else {
                temp[i] = (byte) (((temp[i] & 0xff) + 256 - key) % 256);
            }
        }

        return temp;
    }

    /**
     * 计算分片数量
     *
     * @param size          文件大小
     * @param fileSliceSize 文件分片大小
     * @return
     */
    public static long slice(Long size, Long fileSliceSize) {
        AssertUtil.isTrue(size != null && size > 0, "参数size不能小于0！");
        AssertUtil.isTrue(fileSliceSize != null && fileSliceSize > 0, "参数fileSliceSize不能小于0！");
        if (size.compareTo(fileSliceSize) <= 0) {
            return 1;
        }
        return size % fileSliceSize == 0 ? size / fileSliceSize : (size % fileSliceSize) + 1;
    }

    public static long getSliceSize(Integer index, Long fileSliceSize, Long fileSize) {
        AssertUtil.isTrue(index != null && index > 0, "参数index不能小于0！");
        AssertUtil.isTrue(fileSliceSize != null && fileSliceSize > 0, "参数fileSliceSize不能小于0！");
        AssertUtil.isTrue(fileSize != null && fileSize > 0, "参数fileSize不能小于0！");
        if (index * fileSliceSize < fileSize) {
            return fileSliceSize;
        }
        return fileSize - ((index - 1) * fileSliceSize);
    }
}
