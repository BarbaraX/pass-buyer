package com.barbara.passbuyer.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.barbara.passbuyer.Download.PassFile;
import com.barbara.passbuyer.MyApplication;
import com.barbara.passbuyer.R;

import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by barbara on 6/21/15.
 */
public class HttpUtil {

    private static HttpURLConnection getConnection(String address) throws IOException {
        URL url = new URL(modifyUrl(address));
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(12000);
            connection.setDoOutput(false);//GET请求http报文没有body
            connection.setDoInput(true);
            return connection;

    }

    /**
     * 输入url，得到json响应数据
     * @param url
     * @return
     */
    public static String sendHttpGetRequest(String url) {
        Log.i("passBuyer", "Request URL:" + url);

        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            connection = getConnection(url);
            if (connection==null) {
                return null;
            }

            is = connection.getInputStream();
            //将输入流转换成字符串
            baos = new ByteArrayOutputStream();
            byte [] buffer=new byte[1024];
            int len = 0;
            while((len=is.read(buffer))!=-1){//从InputStream读入，存在buffer数组中
                baos.write(buffer, 0, len);//从buffer数组读出到ByteArrayOutputStream对应的缓冲中
            }
            String jsonString = baos.toString();//把ByteArrayOutputStream对应的缓冲中的数据转换成String

            return jsonString;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection!=null) {
                connection.disconnect();
            }
            try {
                if (baos!=null) {
                    baos.close();
                }
                if (is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static Bitmap getScaledImageFromUrl(String url, int width, int height) {
        Log.i("passBuyer","Image URL:"+url);
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap image = null;
        try {
            connection = getConnection(url);
            if (connection==null) {
                Log.e("passBuyer", "in connection==null block");
                return BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.img_fail);
            }

            //压缩处理网络得到的图
            is = connection.getInputStream();
            image = ImageUtil.decodeScaledBitmapFromStream(is, width, height);

            return image;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("passBuyer", "in IOException block");
            return BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.img_fail);
        } finally {
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection!=null) {
                connection.disconnect();
            }
        }

    }

    /**
     * url中的localhost代表的是手机ip，应该替换成主机ip
     * @param url
     * @return
     */
    public static String modifyUrl(String url) {
        return url.replaceAll("localhost", BaseUrls.IP);
    }
}
