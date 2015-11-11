package com.barbara.passbuyer.Download;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.barbara.passbuyer.Utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件下载管理器
 * Created by barbara on 7/22/15.
 */
public class FileDownloadManager {

    /**
     * 下载状态常量
     */
    public static final int DOWNLOAD_STATE_INIT = 0;
    public static final int DOWNLOAD_STATE_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATE_ERROR = 2;
    public static final int DOWNLOAD_STATE_FINISH = 3;

    //单例
    private static final FileDownloadManager instance = new FileDownloadManager();
    //用于并行下载的线程池
    private ExecutorService executor;
    //正在下载的任务
    private SparseArray<DownloadTask> downloadTasks = new SparseArray<>();
    //正在下载的PassFile的Hash表，方便用id来查找PassFile
    private SparseArray<PassFile> downloadFiles = new SparseArray<>();
    //用于更新UI的handler
    private Handler mHandler;


    private FileDownloadManager() {
        this.executor = Executors.newFixedThreadPool(3);
    }

    public static FileDownloadManager getInstance(){
        return instance;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void addToDownload(int id, PassFile file) {
        file.id = id;
        downloadFiles.put(id, file);
        DownloadTask task = new DownloadTask(file.id, file.url);
        downloadTasks.put(file.id, task);
    }

    //开始新的文件下载任务
    public void startDownloading(PassFile file) {
        downloadFiles.put(file.id, file);
        DownloadTask task = new DownloadTask(file.id, file.url);
        downloadTasks.put(file.id, task);
        executor.submit(task);
    }

    //继续文件下载任务
    //todo 测试发现有问题，需要调试
    public void continueDownloading(PassFile file) {
        DownloadTask task = downloadTasks.get(file.id);
        task.restart();
        executor.submit(task);
    }

    //停止所有下载文件的任务,清空Passfile的Hash表
    public void stopAllDownloadTask() {
        for (int i=0; i<downloadTasks.size(); i++ ) {
            DownloadTask task = downloadTasks.get(i);
            if (task!=null) {
                task.stopTask();
            }
        }
        //将PassFile对象存到sharedPreferences中
        for (int i=0; i<downloadFiles.size(); i++) {
            PassFile file = downloadFiles.get(i);
            if (file!=null) {
                PassFileStorage.savePassFile(file);
                Log.i("passBuyer", "saved PassFile "+file.url+" at "+file.id);
            }
        }
        downloadTasks.clear();
        downloadFiles.clear();
        // 会停止正在进行的任务和拒绝接受新的任务
//        executor.shutdownNow();
    }
    public PassFile getPassFileOfId(int id) {
        return downloadFiles.get(id);
    }


    class DownloadTask implements Runnable {
        int downloadId;
        String downloadUrl;
        boolean isWorking = false;

        public DownloadTask(int id, String downloadUrl) {
            this.downloadId = id;
            this.downloadUrl = downloadUrl;
            isWorking = true;
        }

        @Override
        public void run() {
            PassFile downloadFile = downloadFiles.get(downloadId);
            downloadFile.downloadState = DOWNLOAD_STATE_DOWNLOADING;
            while (isWorking) {
                // 检测是否下载完成
                if (downloadFile.downloadState != DOWNLOAD_STATE_DOWNLOADING) {
//                    downloadFiles.remove(downloadFile.id);
                    downloadTasks.remove(downloadId);
                    isWorking = false;
                    break;
                }

                HttpURLConnection mConnection = null;
                RandomAccessFile outputStream = null;
                InputStream inputStream = null;
                try {
                    // 设置开始写文件的位置
                    URL url = new URL(HttpUtil.modifyUrl(downloadFile.url));

                    mConnection = (HttpURLConnection) url.openConnection();
                    mConnection.setDoInput(true);
                    mConnection.setAllowUserInteraction(true);
                    mConnection.setRequestMethod("GET");
                    mConnection.setReadTimeout(5000);
                    mConnection.setRequestProperty("User-Agent", "NetFox");
                    mConnection.setRequestProperty("Range", "bytes=" + downloadFile.downloadSize + "-");
                    mConnection.connect();

                    inputStream = mConnection.getInputStream();
                    downloadFile.totalSize = mConnection.getContentLength() + downloadFile.downloadSize;

                    File outFile = downloadFile.getFile();
                    if (!outFile.exists()) {
                        outFile.getParentFile().mkdir();
                        outFile.createNewFile();
                    }

                    // 使用java中的RandomAccessFile 对文件进行随机读写操作
                    outputStream = new RandomAccessFile(outFile, "rw");

                    while (isWorking && downloadFile.downloadSize<downloadFile.totalSize) {
                        outputStream.seek(downloadFile.downloadSize);

                        byte[] buf = new byte[1024];
                        int len;

                        while( isWorking && (len=inputStream.read(buf))>0 ){
                            outputStream.write(buf, 0, len);
                            downloadFile.downloadSize += len;
                            Log.i("passBuyer", downloadFile.downloadSize + "");
                            updateUI(downloadFile);
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream!=null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream!=null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mConnection!=null) {
                        mConnection.disconnect();
                    }
                }
            }
        }

        void updateUI(PassFile file) {
            Message msg = mHandler.obtainMessage();
            if(file.totalSize == file.downloadSize) {
                file.downloadState = DOWNLOAD_STATE_FINISH;
            }
            msg.arg1 = file.id;
            msg.arg2 = file.getDownloadProgress();
            msg.sendToTarget();
        }

        public void restart() {
            this.isWorking = true;
        }

        public void stopTask() {
            this.isWorking = false;
        }
    }

}
