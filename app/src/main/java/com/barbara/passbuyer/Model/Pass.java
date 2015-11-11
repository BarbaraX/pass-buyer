package com.barbara.passbuyer.Model;

/**
 * Created by barbara on 7/9/15.
 */
public class Pass {

    private String mId;
    private String mDescription;
    private String mImgUrl;
    private String mDownloadUrl;

    public Pass(String mId, String mDescription, String mImgUrl, String mDownloadUrl) {
        this.mId = mId;
        this.mDescription = mDescription;
        this.mImgUrl = mImgUrl;
        this.mDownloadUrl = mDownloadUrl;
    }

    /**
     * 获得优惠券Pass的id
     */
    public String getId() {
        return mId;
    }

    /**
     * 获得优惠券的简单描述
     * @return
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * 获得优惠券图片的Url
     * @return
     */
    public String getImgUrl() {
        return mImgUrl;
    }

    /**
     * 获得优惠券对应PKPass文件的下载地址
     * @return
     */
    public String getDownloadUrl() {
        return mDownloadUrl;
    }
}
