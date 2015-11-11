package com.barbara.passbuyer.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * Created by barbara on 6/26/15.
 */
public class ShopResponse {
    private int resultCode;
    private String resultInfo;
    private ResultType[] resultType;
    private ResultContent[] resultContent;

    class ResultType {
        String typeID;
        String type;

        @Override
        public String toString() {
            return "ResultType{" +
                    "typeID='" + typeID + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public class ResultContent {
        String retailerID;
        String retailerName;
        String retailerInfo;
        String retailerPhone;
        String retailerAddress;
        String retailerType;

        @SerializedName("retailerLogo")
        String retailerLogoUrl;

    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public ResultType[] getResultType() {
        return resultType;
    }

    public ResultContent[] getResultContent() {
        return resultContent;
    }

}
