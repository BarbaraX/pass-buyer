package com.barbara.passbuyer.UI;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.barbara.passbuyer.Download.FileDownloadManager;
import com.barbara.passbuyer.Download.PassFile;
import com.barbara.passbuyer.Model.Pass;
import com.barbara.passbuyer.R;
import com.barbara.passbuyer.Utils.FileUtil;
import com.dd.CircularProgressButton;
import com.xiong.client.PKPassLibrary;
import com.xiong.service.aidl.PKPass;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by barbara on 7/9/15.
 */
public class PassAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private List<Pass> mPasses;
    private AsyncImageLoader mImageLoader;
    private FileDownloadManager mDownloadManager;
    private Handler mHandler;
    private ListView mListView;

    public PassAdapter(Context mContext, int mResource, List<Pass> mPasses) {
        this.mContext = mContext;
        this.mResource = mResource;
        this.mPasses = mPasses;

        this.mImageLoader = new AsyncImageLoader(mContext);

        this.mHandler = new MyHandler(this);

        this.mDownloadManager = FileDownloadManager.getInstance();
        this.mDownloadManager.setHandler(this.mHandler);
    }

    /**
     * 获得数据总数
     * @return
     */
    @Override
    public int getCount() {
        return mPasses==null ? 0 : mPasses.size();
    }

    /**
     * 根据索引i获得第i个数据
     * @param i
     * @return
     */
    @Override
    public Object getItem(int i) {
        return mPasses.get(i);
    }

    /**
     * 通过索引i得到id（不明觉厉）
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final Pass pass = (Pass) getItem(i);
        final ViewHolder holder;
        View view;

        if (convertView==null) {
            view = LayoutInflater.from(mContext).inflate(mResource, null);

            holder = new ViewHolder();

            holder.passImg = (ImageView) view.findViewById(R.id.pass_img);
            holder.passInfo = (TextView) view.findViewById(R.id.pass_info_view);
            holder.btn = (CircularProgressButton) view.findViewById(R.id.pass_download_btn);

            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        //根据Pass内容设置item布局显示
        holder.passInfo.setText(pass.getDescription());
        mImageLoader.loadBitmap(pass.getImgUrl(), holder.passImg);

        setButtonState(i, pass.getDownloadUrl(), holder.btn);

        return view;

    }

    public void setAttachedListView(ListView mPassListView) {
        this.mListView = mPassListView;
    }

    class ViewHolder {
        ImageView passImg;
        TextView passInfo;
        CircularProgressButton btn;
    }

    static class MyHandler extends Handler {
        WeakReference<PassAdapter> passAdapterWeakRef;

        public MyHandler(PassAdapter passAdapterWeakRef) {
            this.passAdapterWeakRef = new WeakReference<>(passAdapterWeakRef);
        }

        @Override
        public void handleMessage(Message msg) {
            int id = msg.arg1;
            int progress = msg.arg2;

            updateItemViewOfId(id, progress);
        }

        private void updateItemViewOfId(int id, int progress) {
            if (passAdapterWeakRef.get()!=null) {
                int first = passAdapterWeakRef.get().mListView.getFirstVisiblePosition();
                int last = passAdapterWeakRef.get().mListView.getLastVisiblePosition();
                //不在屏幕上的view不更新，交给系统调用getView的时候更新
                if (id<first || id>last) {
                    return;
                }

                View view = passAdapterWeakRef.get().mListView.getChildAt(id-first);
                ViewHolder viewHolder = (ViewHolder)view.getTag();

                viewHolder.btn.setProgress(progress);

            }
        }

    }

    private void setButtonState(final int id, final String url, final CircularProgressButton button) {
        //根据位置id查找下载任务表中的PassFile对象
        final PassFile file = mDownloadManager.getPassFileOfId(id);
        if (file!=null) {
            switch (file.downloadState) {
                case FileDownloadManager.DOWNLOAD_STATE_FINISH:
                    button.setProgress(100);
                    break;
                case FileDownloadManager.DOWNLOAD_STATE_DOWNLOADING:
                    //继续下载
                    mDownloadManager.continueDownloading(file);
//                    button.setProgress(file.getDownloadProgress());
                case FileDownloadManager.DOWNLOAD_STATE_ERROR:
                    button.setProgress(-1);
                    break;
            }
        } else {
            button.setProgress(0);
        }

        //设置button的点击处理逻辑
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (button.getProgress()) {
                    //当前为"error"状态，点击按钮后跳转至"idle"状态，删除已下载的文件
                    case -1:
                        button.setProgress(0);
                        mDownloadManager.getPassFileOfId(id).deleteFile();
                        break;
                    //当前状态为"init"状态，点击按钮后跳转至"downloading"状态，并开启异步任务进行下载
                    case 0:
                        mDownloadManager.startDownloading(new PassFile(id, url));
                        break;
                    //当前状态为"complete"状态，点击按钮之后将pass添加到Passbook中
                    case 100:
                        PassAdapter.this.add2Passbook(file.getFile());
                        break;
                    //当前状态为"downloading"状态，点击按钮无响应
                    default:
                        break;
                }
            }
        });
    }

    private void add2Passbook(File file) {
        String fromFile = file.toString();
        String toFile = FileUtil.getExternalCacheStorage()+File.separator+fromFile;
        //TODO 显示进度条，在当前activity之上
        //生成PKPass对象
        PKPass pkPass = new PKPass();
        pkPass.initWithData(fromFile, toFile);
        //生成PKLibrary对象
        PKPassLibrary library = new PKPassLibrary(mContext);
        try {
            //调用PKLibrary的方法将PKPass文件添加过去
            if (library.IsPassLibraryAvailable()) {
                if (library.containPass(pkPass)) {
                    Toast.makeText(mContext, "已经添加过啦，请不要重复添加~", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isAdded = library.addPkpass(pkPass);
                    String resultInfo = isAdded ? "添加成功!!" : "添加失败";
                    Toast.makeText(mContext, resultInfo, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
        }

        //删除本地解压后的缓存文件
        FileUtil.deleteRecursive(new File(toFile));
        //为了看见效果,需要删除??
//        file.delete();
//        Log.i("passBuyer", file==null?"file is null":"file is not null");
    }

}
