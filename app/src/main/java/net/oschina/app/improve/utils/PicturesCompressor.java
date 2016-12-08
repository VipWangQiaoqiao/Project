package net.oschina.app.improve.utils;

import android.graphics.BitmapFactory;

import net.oschina.common.utils.BitmapUtil;

import java.io.File;

import static net.oschina.common.utils.StreamUtil.copyFile;


/**
 * Created by JuQiu
 * on 16/7/21.
 */
@SuppressWarnings("WeakerAccess")
public final class PicturesCompressor {
    private PicturesCompressor() {

    }

    public static boolean compressImage(final String srcPath,
                                        final String savePath,
                                        final long targetSize) {
        return compressImage(srcPath, savePath, targetSize, 75, 1280, 1280 * 6, null, null, true);
    }

    /**
     * 压缩图片
     *
     * @param srcPath     原图地址
     * @param savePath    存储地址
     * @param maxSize     最大文件地址byte
     * @param minQuality  最小质量
     * @param maxWidth    最大宽度
     * @param maxHeight   最大高度
     * @param byteStorage 用于批量压缩时的buffer，不必要为null，
     *                    需要时，推荐 {{@link BitmapUtil#DEFAULT_BUFFER_SIZE}}
     * @param options     批量压缩时复用参数，可调用 {{@link BitmapUtil#createOptions()}} 得到
     * @param exactDecode 是否精确解码， TRUE： 在4.4及其以上机器中能更节约内存
     * @return 是否压缩成功
     */
    public static boolean compressImage(final String srcPath,
                                        final String savePath,
                                        final long maxSize,
                                        final int minQuality,
                                        final int maxWidth,
                                        final int maxHeight,
                                        byte[] byteStorage,
                                        BitmapFactory.Options options,
                                        boolean exactDecode) {
        // build source file
        final File sourceFile = new File(srcPath);
        if (!sourceFile.exists())
            return false;

        // build save file
        final File saveFile = new File(savePath);
        File saveDir = saveFile.getParentFile();
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs())
                return false;
        }

        // End clear the out file data
        if (saveFile.exists()) {
            if (!saveFile.delete())
                return false;
        }

        // if the in file size <= maxSize, we can copy to savePath
        if (sourceFile.length() <= maxSize && confirmImage(srcPath, options)) {
            return copyFile(sourceFile, saveFile);
        }

        // Doing
        File tempFile = BitmapUtil.Compressor.compressImage(sourceFile, maxSize, minQuality, maxWidth,
                maxHeight, byteStorage, options, exactDecode);

        // Rename to out file
        return tempFile != null && copyFile(tempFile, saveFile) && tempFile.delete();
    }

    public static boolean confirmImage(String filePath, BitmapFactory.Options opts) {
        if (opts == null) opts = BitmapUtil.createOptions();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        String mimeType = opts.outMimeType.toLowerCase();
        return mimeType.contains("jpeg") || mimeType.contains("png") || mimeType.contains("gif");
    }

    public static String verifyPictureExt(String filePath) {
        BitmapFactory.Options option = BitmapUtil.createOptions();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, option);
        String mimeType = option.outMimeType.toLowerCase();
        int doIndex = filePath.lastIndexOf(".") + 1;
        String ext = filePath.substring(doIndex).toLowerCase();
        if (mimeType.contains("x-ico")) {
            //TODO
        } else if (mimeType.contains("jpeg")) {
            ext = "jpg";
        } else if (mimeType.contains("png")) {
            ext = "png";
        } else if (mimeType.contains("webp")) {
            //TODO
        } else if (mimeType.contains("vnd.wap.wbmp")) {
            //TODO
        }
        String newFilePath = filePath.substring(0, doIndex) + ext;

        if (!filePath.equals(newFilePath)) {
            if (new File(filePath).renameTo(new File(newFilePath)))
                return newFilePath;
        }
        return filePath;
    }
}
