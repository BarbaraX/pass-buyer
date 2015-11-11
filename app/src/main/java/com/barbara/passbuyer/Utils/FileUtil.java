package com.barbara.passbuyer.Utils;

import android.os.Environment;
import android.util.Log;

import com.barbara.passbuyer.Download.PassFile;
import com.barbara.passbuyer.MyApplication;

import java.io.File;

/**
 * Created by barbara on 7/21/15.
 */
public class FileUtil {

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File makeFileOnExternalStorage(String fileName) {
        File fileDir = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        return new File(fileDir, fileName);
    }

    public static String getExternalCacheStorage() {
        String path = null;
        path = MyApplication.getContext().getExternalCacheDir().toString();
        Log.i("passBuyer", path);
        return path;
    }

    public static boolean deleteRecursive(File file2delete) {
        if (!file2delete.isDirectory()) {
            return false;
        }

        for (String child : file2delete.list()) {
            final File temp = new File(file2delete, child);
            if (temp.isDirectory()) {
                deleteRecursive(temp);
            } else {
                temp.delete();
            }
        }

        return file2delete.delete();
    }

}
