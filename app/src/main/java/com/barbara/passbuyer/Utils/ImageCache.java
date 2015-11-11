package com.barbara.passbuyer.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.LruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by barbara on 7/7/15.
 */
public class ImageCache {
    /**
     * 图像的内存缓存，一级缓存(声明为static，保证多实例共享;volatile？？)
     */
    private volatile static LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图像的磁盘缓存，二级缓存
     */
    private DiskLruCache mDiskCache;
    private Context mContext;

    public ImageCache(Context context) {
        mContext = context;
        initMemCache();
        initDiskCache();
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        //添加图片到内存缓存中
        addBitmapToMemCache(key, bitmap);
        //同时将图片添加到磁盘缓存中
        addBitmapToDisk(key, bitmap);
    }


    /*********************内存缓存相关操作***********************/
    /**
     * 初始化内存缓存
     */
    private void initMemCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * 添加Bitmap到内存缓存中
     */
    private void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key)==null)
            mMemoryCache.put(key, bitmap);
    }

    /**
     * 从内存缓存中获取Bitmap
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }



    /*********************磁盘缓存相关操作***********************/
    /**
     * 初始化磁盘缓存
     */
    private void initDiskCache() {
        try {
            // 获取图片缓存路径
            File cacheDir = getDiskCacheDir(mContext, "thumb");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            // 创建DiskLruCache实例，初始化缓存数据
            mDiskCache = DiskLruCache
                    .open(cacheDir, getAppVersion(mContext), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加图片到磁盘缓存中
     */
    private void addBitmapToDisk(String url, Bitmap bitmap) {
        String hashedUrl = MD5.getMD5(url);

        try {
            if (mDiskCache!=null && mDiskCache.get(hashedUrl)==null) {
                DiskLruCache.Editor editor = mDiskCache.edit(hashedUrl);
                if (editor!=null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                    editor.commit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从磁盘缓存获取图片
     */
    public Bitmap getBitmapFromDisk(String url) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;

        String hashedUrl = MD5.getMD5(url);
        try {
            snapshot = mDiskCache.get(hashedUrl);

            if (snapshot!=null) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                FileDescriptor fileDescriptor = fileInputStream.getFD();

                if (fileDescriptor!=null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 根据传入的uniqueName获取磁盘缓存的路径地址。
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        //具有SD卡或者不可移动的External Storage时，获取External storage上的缓存路径；
        //否则获取Internal storage上的缓存路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取当前应用程序的版本号。
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
