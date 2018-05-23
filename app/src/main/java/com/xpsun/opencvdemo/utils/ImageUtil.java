package com.xpsun.opencvdemo.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.xpsun.opencvdemo.OnSaveImageCallBack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageUtil {

    private final static String FILEDIR = "ImageDataDir";

    public static String getImageFileDir() {
//        return AppApplication.Companion.getInstance().getFilesDir().getAbsolutePath() + File.separator + FILEDIR + File.separator;
        return FileUtil.getExternalCacheDir() + File.separator + FILEDIR + File.separator;
    }

    /**
     * 保存图片文件到默认文件目录
     *
     * @param bitmap   Bitmap
     * @param fileName 图片名
     */
    public static void saveBitmapToFileDir(Bitmap bitmap, String fileName,OnSaveImageCallBack onSaveImageCallBack) {
        saveBitmapToFileDir(bitmap, fileName, Bitmap.CompressFormat.JPEG, 95,onSaveImageCallBack);
    }

    /**
     * 保存图片文件到默认文件目录
     *
     * @param bitmap   Bitmap
     * @param fileName 图片名
     * @param format   压缩格式
     * @param quality  压缩质量
     */
    public static void saveBitmapToFileDir(Bitmap bitmap, String fileName, Bitmap.CompressFormat format, int quality,OnSaveImageCallBack onSaveImageCallBack) {
        saveBitmapToStorage(bitmap, getImageFileDir() + fileName, format, quality,onSaveImageCallBack);
    }

    /**
     * 保存图片文件到本地
     *
     * @param bitmap  Bitmap
     * @param path    图片路径
     * @param format  压缩格式
     * @param quality 压缩质量
     */
    public static void saveBitmapToStorage(Bitmap bitmap, String path, Bitmap.CompressFormat format, int quality,OnSaveImageCallBack onSaveImageCallBack) {
        if (quality < 0 || quality > 100) {
            quality = 100;
        }
        BufferedOutputStream bos = null;
        try {
            File file = FileUtil.createImageFile(path);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(format != null ? format : Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            onSaveImageCallBack.onSuccess(path);
        } catch (IOException e) {
            Logger.e(ImageUtil.class.getName(), e.getMessage(), e);
            onSaveImageCallBack.onFail();
        } finally {
            FileUtil.closeSilently(bos);
        }
    }

    public static Bitmap getFitSampleBitmap(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        double ratio = Math.max(options.outWidth * 1.0d / 1024f, options.outHeight * 1.0d / 1024f);
////        double ratio = Math.max(options.outWidth * 1.0d / 1.0f, options.outHeight * 1.0d / 1.0f);
//        options.inSampleSize = (int) Math.ceil(ratio);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Camera.Size getProperSize1(List<Camera.Size> sizeList, int screenWidth, int screenHeight) {
        //先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeComparator());

        Camera.Size result = null;
        for (Camera.Size size : sizeList) {
            if (size.height == screenWidth && size.width == screenHeight) {
                return result = size;
            }
        }

        if (result == null) {
            List<Camera.Size> list = new ArrayList<>();
            for (Camera.Size size : sizeList) {
                if (size.height >= screenWidth) {
                    list.add(size);
                }
            }
            Collections.sort(list, new SizeComparator());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).width < screenHeight) {
                    return result = list.get(i);
                }
            }

            if (result == null) {
                if(list != null && list.size() != 0){
                    return result = list.get(list.size() - 1);
                }
            }
        }
        return result;
    }

    static class SizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            Camera.Size size1 = lhs;
            Camera.Size size2 = rhs;
            if (size1.width < size2.width
                    || size1.width == size2.width && size1.height < size2.height) {
                return 1;
            } else if (!(size1.width == size2.width && size1.height == size2.height)) {
                return -1;
            }
            return 0;
        }
    }

    //从相册获取图片转化为bitmap
    public static Bitmap onAlbumToPhoto(Context context, Uri uri){
        return onAlbumToPhoto(context,uri,1080,1920);
    }

    public static Bitmap onAlbumToPhoto(Context context, Uri uri,int width,int height){
        Bitmap bitmap;
        String path;
        int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
        if (sdkVersion >= 19) {  // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
            path = uri.getPath();//5.0直接返回的是图片路径 Uri.getPath is ：  /document/image:46 ，5.0以下是一个和数据库有关的索引值
            // path_above19:/storage/emulated/0/girl.jpg 这里才是获取的图片的真实路径
            path =getPathForAbove19(context, uri);
        } else {
            path = getFilePathForBelow19(context, uri);
        }
        bitmap = getSmallBitmap(path,width,height);
        return bitmap;
    }

    /**
     * 获取小于api19时获取相册中图片真正的uri
     * @param context
     * @param uri
     * @return
     */
    private static String getFilePathForBelow19(Context context, Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 获取大于api19时获取相册中图片真正的uri
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPathForAbove19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //只返回图片的大小信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static Bitmap getSDImageToBitmap(String filePath){
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(filePath);
            bitmap  = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
        }
        return bitmap;
    }

    private static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int scale = 1;
        if (width > reqWidth || height > reqHeight) {
            // 使用需要的宽高的最大值来计算比率
            int suitedValue = Math.max(reqWidth, reqHeight);
            // 计算图片高度和我们需要高度的最接近比例值
            int heightRatio = Math.round((float) height / (float) suitedValue);
            // 宽度比例值
            int widthRatio = Math.round((float) width / (float) suitedValue);
            // 取比例值中的较大值作为inSampleSize
            scale = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return scale;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
