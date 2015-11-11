package com.barbara.passbuyer.Model;

import android.graphics.Bitmap;

import com.barbara.passbuyer.Utils.HttpUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barbara on 6/27/15.
 */
public class ShopResponseReader {

    public static List<Shop> readShops(String resp) {

        ShopResponse response = new Gson().fromJson(resp, ShopResponse.class);
        if (response==null) return null;

        ShopResponse.ResultContent[] contents = response.getResultContent();
        if (contents==null) return null;

        List<Shop> shops = new ArrayList<>();

        Shop shop;
        for (ShopResponse.ResultContent r : contents) {
            shop = new Shop(r.retailerID,r.retailerName,r.retailerInfo,r.retailerPhone,r.retailerAddress,r.retailerType,r.retailerLogoUrl);
            shops.add(shop);
        }

        return shops;
    }
}
