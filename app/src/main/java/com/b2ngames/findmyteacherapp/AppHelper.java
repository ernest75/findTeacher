package com.b2ngames.findmyteacherapp;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by xavi on 09/12/2016.
 */

public class AppHelper {
    public static byte[] getFileDataFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
