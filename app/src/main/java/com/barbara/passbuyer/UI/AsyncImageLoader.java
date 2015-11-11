package com.barbara.passbuyer.UI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.HttpUtil;
import com.barbara.passbuyer.Utils.ImageCache;

import java.lang.ref.WeakReference;

/**
 * Created by barbara on 7/8/15.
 */
public class AsyncImageLoader {
    private Context mContext;
    private ImageCache mImageCache;


    public AsyncImageLoader(Context context) {
        mContext = context;
        mImageCache = new ImageCache(mContext);
    }

    /**
     * 异步加载图片到ImageView中,AsyncDrawable持有task引用，防止乱序
     *
     * @param url
     * @param imageView
     */
    public void loadBitmap(String url, ImageView imageView) {
        final Bitmap image = mImageCache.getBitmapFromMemCache(url);
        //先从缓存中获取图片，如果缓存中没有图片则开启异步任务下载图片
        if (image!=null) {
            imageView.setImageBitmap(image);
        } else {
            if (cancelPotentialWork(url, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), R.drawable.img_placeholder, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(url);
            }
        }

    }

    /**
     * 给定imageView所绑定的下载任务不是最新的任务时，则取消当前下载任务，返回true
     * 为解决加载之后乱序的问题
     *
     * @param url
     * @param imageView
     * @return
     */
    public boolean cancelPotentialWork(String url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapUrl = bitmapWorkerTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                // 当前imagView对应的下载任务需要更新，则取消当前下载任务
                bitmapWorkerTask.cancel(true);
            } else {
                // 当前imageView对应的下载线程正在执行
                return false;
            }
        }
        return true;
    }

    /**
     * 得到与ImageView关联的异步加载图片任务
     *
     * @param imageView
     * @return
     */
    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }


    /**
     * 持有BitmapWorkerTask引用的BitmapDrawable类
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, int id, BitmapWorkerTask task) {
            super(res, BitmapFactory.decodeResource(res, id));
            bitmapWorkerTaskReference = new WeakReference<>(task);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }

    }

    /**
     * 从网络下载图片的异步任务
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<>(imageView);

        }

        // 从网络获取图片并按尺寸进行压缩
        @Override
        protected Bitmap doInBackground(String... strings) {
            url = strings[0];
            Bitmap bitmap = null;

            //从磁盘缓存获取图片，如果磁盘中没有图片则从网络获取图片
            bitmap = mImageCache.getBitmapFromDisk(url);
            if (bitmap==null) {
                //TODO scale的长宽需要适配
                bitmap = HttpUtil.getScaledImageFromUrl(url, 45, 55);
                //将从网络获取到的Bitmap存到缓存中
                mImageCache.addBitmapToCache(url, bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

    }

}
