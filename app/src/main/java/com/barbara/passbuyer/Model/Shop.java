package com.barbara.passbuyer.Model;

import android.graphics.Bitmap;

import com.barbara.passbuyer.Utils.ImageUtil;

import java.io.Serializable;

/**
 * Created by barbara on 6/26/15.
 */
public class Shop implements Serializable{

    private String mId;
    private String mName;
    private String mInfo;
    private String mPhone;
    private String mAddress;
    private String mType;
    private String mLogoUrl;//商户缩略图url


    public Shop(String mId, String mName, String mInfo, String mPhone, String mAddress, String mType, String mLogoUrl) {
        this.mId = mId;
        this.mName = mName;
        this.mInfo = mInfo;
        this.mPhone = mPhone;
        this.mAddress = mAddress;
        this.mType = mType;
        this.mLogoUrl = mLogoUrl;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getInfo() {
        return mInfo;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getType() {
        return mType;
    }

    public String getLogoUrl() {
        return mLogoUrl;
    }
}
