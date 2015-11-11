package com.barbara.passbuyer.Model;

import java.util.Arrays;

/**
 * Created by barbara on 6/23/15.
 */
public class LoginResponse {
    private int resultCode;
    private String resultInfo;
    private String[] resultContent;

    public LoginResponse(String[] resultContent, String resultInfo, int resultCode) {
        this.resultContent = resultContent;
        this.resultInfo = resultInfo;
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultInfo() {
        return resultInfo;
    }

    public String[] getResultContent() {
        return resultContent;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "resultCode=" + resultCode +
                ", resultInfo='" + resultInfo + '\'' +
                ", resultContent=" + Arrays.toString(resultContent) +
                '}';
    }
}
