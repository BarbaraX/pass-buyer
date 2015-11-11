package com.barbara.passbuyer.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by barbara on 7/27/15.
 */
public class ConnectivityUtil {

    public static boolean isNetAvailable(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiAvailable = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isMobileNetAvailable = mConnectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();

        if ( !isWifiAvailable && !isMobileNetAvailable ) {
            Toast.makeText(context, "网络连接不可用，请连接网络！", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
