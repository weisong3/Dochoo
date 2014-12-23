package com.chc.dochoo.userlogin;

import android.content.res.Resources;

import com.chc.found.FoundSettings;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by HenryW on 1/21/14.
 */
public abstract class RawStringUtil {
    public static String getFromRawFileAsString(Resources res, int rawResourceId) {
        try {
            InputStream inputStream = res.openRawResource(rawResourceId);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[FoundSettings.BUFFERSIZE];
            StringBuilder sb = new StringBuilder();
            int count;
            while ((count = bis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, count, Charset.forName("UTF-8")));
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
