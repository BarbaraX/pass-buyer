package com.barbara.passbuyer.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by barbara on 7/9/15.
 */
public class PassResponse {
    private int resultCode;
    private String resultInfo;
    private ResultContent[] resultContent;

    public class ResultContent {
        String passID;
        String passDescrip;
        String passLocation;

        @SerializedName("passImage")
        String passImageUrl;

    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public ResultContent[] getResultContent() {
        return resultContent;
    }

}
