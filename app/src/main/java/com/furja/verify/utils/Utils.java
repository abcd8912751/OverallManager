package com.furja.verify.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.furja.verify.utils.Constants.LOG_TAG;

/**
 * 显示Log或Toast
 */

public class Utils {



    /**
     * 截取二维码所示地址的后12位作为条码
     */
    public static String formatBarCode(String barCodeUrl)
    {
        String url="http://www.registeryourshark.com/reg/?m=IR101&s=U11F2626Z2A4";

        try {
            barCodeUrl=URLEncoder.encode(barCodeUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(barCodeUrl==null||barCodeUrl.length()<1)
            return "空";
        else
            return barCodeUrl;
    }


}
