package com.chc.dochoo.contacts;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.chc.found.config.Apis;
import com.chc.found.models.EntityUser;
import com.chc.found.models.PatientUser;
import com.chc.found.utils.PinyinUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lance Ji on 4/20/14.
 */
public class MyCursor implements Cursor{
    private int position;
    private List<EntityUser> data;

    final private String TAG = "MyCursor";
    public MyCursor(List<EntityUser> data){
        convertUserlist(data);
        this.data = data;
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public String getString(int i) {
        try{
            if(StringUtils.isNotBlank(data.get(position).getFullname()))    return PinyinUtils.getPingYin(data.get(position).getFullname()).substring(0, 1);
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        return " ";
    }
    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public boolean move(int i) {
        return false;
    }

    @Override
    public boolean moveToPosition(int i) {
        if(position<-1||position>getCount()){
            return false;
        }

        this.position = i;
        //if(position+2>getCount()){
        //    this.position = position;
        //}else{
        //   this.position = position + 2;
        //}
        return true;
    }

    @Override
    public boolean moveToFirst() {
        return false;
    }

    @Override
    public boolean moveToLast() {
        return false;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public boolean moveToPrevious() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public int getColumnIndex(String s) {
        return 0;
    }

    @Override
    public int getColumnIndexOrThrow(String s) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public String getColumnName(int i) {
        return null;
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public byte[] getBlob(int i) {
        return new byte[0];
    }



    @Override
    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {

    }

    @Override
    public short getShort(int i) {
        return 0;
    }

    @Override
    public int getInt(int i) {
        return 0;
    }

    @Override
    public long getLong(int i) {
        return 0;
    }

    @Override
    public float getFloat(int i) {
        return 0;
    }

    @Override
    public double getDouble(int i) {
        return 0;
    }

    @Override
    public int getType(int i) {
        return 0;
    }

    @Override
    public boolean isNull(int i) {
        return false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver contentObserver) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver contentObserver) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle bundle) {
        return null;
    }

    private void convertUserlist(List<EntityUser> source){
        Collections.sort(source, new Comparator<EntityUser>() {
            @Override
            public int compare(EntityUser lhs, EntityUser rhs) {
                String leftName = PinyinUtils.getPingYin(lhs.getFullname());
                String rightName = PinyinUtils.getPingYin(rhs.getFullname());
                if (leftName == null && rightName == null) return 0;
                if (leftName == null) return -1;
                if (rightName == null) return 1;
                return leftName.compareToIgnoreCase(rightName);
            }
        });
    }
}
