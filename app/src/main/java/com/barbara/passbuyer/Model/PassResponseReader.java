package com.barbara.passbuyer.Model;

import android.util.SparseArray;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by barbara on 7/9/15.
 */
public class PassResponseReader {
    public static List<Pass> readPasses(String resp) {

        PassResponse response = new Gson().fromJson(resp, PassResponse.class);
        if (response==null) return null;

        PassResponse.ResultContent[] contents = response.getResultContent();
        if (contents==null) return null;

        List<Pass> passes = new ArrayList<>();

        Pass pass;
        for (PassResponse.ResultContent r : contents) {
            pass = new Pass(r.passID, r.passDescrip, r.passImageUrl, r.passLocation);
            passes.add(pass);
        }


        return passes;
    }

}
