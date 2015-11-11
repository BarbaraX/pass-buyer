package com.barbara.passbuyer.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by barbara on 6/26/15.
 */
public class ImageUtil {

    public static Bitmap base64ToBitmap(String base64data) {
        byte[] data = Base64.decode(base64data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap decodeScaledBitmapFromStream(InputStream is, int reqWidth, int reqHeight){
        //在不生成Bitmap实例的情况下获得图片原始尺寸
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len=is.read(buffer))!=-1) {
                baos.write(buffer, 0, len);
            }

            byte[] bytes = baos.toByteArray();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);//因为设置了inJustDecodeBounds，这里解码得不到bitmap对象，只能得到bitmap的信息(存在options中)

            //根据原始尺寸计算出图片的缩小比例
            options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight);
            //解码得到图片
            options.inJustDecodeBounds = false;
            return  BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
