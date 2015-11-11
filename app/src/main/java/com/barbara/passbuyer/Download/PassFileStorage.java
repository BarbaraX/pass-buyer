package com.barbara.passbuyer.Download;

import android.content.SharedPreferences;

import com.barbara.passbuyer.MyApplication;
import com.google.gson.Gson;

/**
 * Created by barbara on 7/30/15.
 */
public class PassFileStorage {

    private static SharedPreferences getStorageFile() {
        return MyApplication.getContext().getSharedPreferences("passFile", 0);

    }

    /**
     * 查看文件中是否包含相应url对应的PassFile
     * @param url
     * @return
     */
    public static boolean hasPassFileOfUrl(String url) {
        return getStorageFile().contains(url);
    }

    /**
     * 从shared preferences中得到某个url对应的passFile对象
     * @param url
     * @return
     */
    public static PassFile getPassFileOfUrl(String url) {
        PassFile passFile = null;

        String storeJson = getStorageFile().getString(url, null);
        StorageHelper store = new Gson().fromJson(storeJson, StorageHelper.class);

        passFile = new PassFile(store.downloadSize, store.state, store.totalSize, url);

        return passFile;
    }

    /**
     * 保存passFile对象状态到shared preferences中
     * @param passFile
     * @return
     */
    public static boolean savePassFile(PassFile passFile) {
        StorageHelper store = new StorageHelper(passFile.totalSize, passFile.downloadSize, passFile.downloadState);
        String fileJsonStr = new Gson().toJson(store, StorageHelper.class);

        return getStorageFile().edit().putString(passFile.url, fileJsonStr).commit();
    }

    private static class StorageHelper {
        int state;
        int totalSize;
        int downloadSize;

        public StorageHelper(int totalSize, int downloadSize, int state) {
            this.totalSize = totalSize;
            this.downloadSize = downloadSize;
            this.state = state;
        }
    }

}
