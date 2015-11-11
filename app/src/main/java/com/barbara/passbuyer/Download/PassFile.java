package com.barbara.passbuyer.Download;

import android.util.Log;

import com.barbara.passbuyer.Utils.DebugConstant;
import com.barbara.passbuyer.Utils.FileUtil;

import java.io.File;

/**
 * pass对应的下载文件类
 * Created by barbara on 7/22/15.
 */
public class PassFile {

    public static final String PASS_SUFFIX = "pkpass";

    public int id;
    public String url;          //文件对应的url,将用url的最后一个segment作为文件名称
    public int downloadSize;    //文件已经下载的大小
    public int totalSize;       //文件总大小
    public int downloadState;   //文件下载状态

    private File file;          //用于操作PassFile对应的下载文件

    public PassFile(int id, String url) {
        this.id = id;
        this.url = url;
        this.totalSize = 0;
        this.downloadState = FileDownloadManager.DOWNLOAD_STATE_INIT;
        file = makeFile(this.url);
    }

    public PassFile(int downloadSize, int downloadState, int totalSize, String url) {
        this.downloadSize = downloadSize;
        this.downloadState = downloadState;
        this.totalSize = totalSize;
        this.url = url;
        file = makeFile(url);
    }

    public int getDownloadProgress(){
        return downloadSize*100/totalSize;
    }

    public File getFile() {
        return file;
    }

    /**
     * 在磁盘上创建url最后一个segment作为文件名称的文件
     */
    private File makeFile(String url) {
        String fileName = url.substring(url.lastIndexOf("/")+1);

        if (FileUtil.isExternalStorageWritable()) {
            return FileUtil.makeFileOnExternalStorage(fileName);
        }

        return null;
    }

    /**
     * 删除磁盘上未下载完的PassFile对应的文件
     */
    public void deleteFile() {
        if (this.file==null || !this.file.exists()) {
            Log.e("passBuyer","delete File failed, because file is null or not exist");
            return;
        }

        this.file.delete();
    }

    @Override
    public String toString() {
        return "PassFile{" +
                "downloadSize=" + downloadSize +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", totalSize=" + totalSize +
                ", downloadState=" + downloadState +
                ", file=" + file +
                '}';
    }
}
