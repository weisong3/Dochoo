package com.chc.tipapi;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.chc.tipapi.TipTableAndUri.TipsTableAndUri;
import com.chc.tipapi.TipTableAndUri.TipsTableAndUri.ColumnNames;

public class TipsDataProvider {

	private final static String TAG = "TipsDataProvider";
	
	private static Context context;
	
	private static void checkAndSet(Context context) {
		if (TipsDataProvider.context == null) {
			synchronized (TipsDataProvider.class) {
				if (TipsDataProvider.context == null) {
					TipsDataProvider.context = context;
				}
			}
		}
	}
	
	private static String getStringData(Cursor cursor, String strIndex) {		
		if (cursor == null || strIndex == null) {
			return null;
		}	
		int index = cursor.getColumnIndex(strIndex);
		if (index < 0) {
			return "";
		}	
		return cursor.getString(index);	
	}
	
	public static ArrayList<Tip> getTipList(Context context){
		checkAndSet(context);
		Cursor cursor = null;
		ArrayList<Tip> listBack = new ArrayList<Tip>();
		try {

			ContentResolver cr = context.getContentResolver();
			cursor = cr.query(TipsTableAndUri.CONTENT_URI, null, null, null, TipsTableAndUri.getColumnString(ColumnNames._ID));
			if (cursor == null || !cursor.moveToFirst()) {
				closeCursor(cursor);
				return listBack;
			}
			if (cursor.moveToFirst()) {
				do {
					TipsTableAndUri.ColumnNames[] columnNamesArray = TipsTableAndUri.ColumnNames.values();
					HashMap<ColumnNames, String> tipData = new HashMap<ColumnNames, String>();
					for (int i = 0; i < columnNamesArray.length; i++) {
						tipData.put(columnNamesArray[i], getStringData(cursor, TipsTableAndUri.getColumnString(i)));
					}
					Tip tip = new Tip(tipData);
					listBack.add(tip);
				} while (cursor.moveToNext());
			}
			closeCursor(cursor);
		} catch (Exception ex) {
			closeCursor(cursor);
			throw new SQLException(ex.toString());
		}
		return listBack;
	}
	
	public static ArrayList<Tip> getTipListWithCategory(Context context, String categoryString){
		checkAndSet(context);
		Cursor cursor = null;
		ArrayList<Tip> listBack = new ArrayList<Tip>();
		try {
			StringBuilder sb = new StringBuilder();
			String[] categoryStringArray = categoryString.split(";");
			for(int i = 0; i < categoryStringArray.length; ++i) {
				if(i == 0) {
					sb.append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.CATEGORY)).append("=\'").append(categoryStringArray[i]).append("\'");
				} else {
					sb.append(" or ").append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames.CATEGORY)).append("=\'").append(categoryStringArray[i]).append("\'");
				}
			}
			ContentResolver cr = context.getContentResolver();
			cursor = cr.query(TipsTableAndUri.CONTENT_URI, null, sb.toString(), null, TipsTableAndUri.getColumnString(ColumnNames._ID));
			if (cursor == null || !cursor.moveToFirst()) {
				closeCursor(cursor);
				return listBack;
			}
			if (cursor.moveToFirst()) {
				do {
					TipsTableAndUri.ColumnNames[] columnNamesArray = TipsTableAndUri.ColumnNames.values();
					HashMap<ColumnNames, String> tipData = new HashMap<ColumnNames, String>();
					for (int i = 0; i < columnNamesArray.length; i++) {
						tipData.put(columnNamesArray[i], getStringData(cursor, TipsTableAndUri.getColumnString(i)));
					}
					Tip tip = new Tip(tipData);
					listBack.add(tip);
				} while (cursor.moveToNext());
			}
			closeCursor(cursor);
		} catch (Exception ex) {
			closeCursor(cursor);
			throw new SQLException(ex.toString());
		}
		return listBack;
	}
	
	public static Tip getTip(String index , Context context){
		Tip tip = null;
		if(index == null){
			Log.e(TAG, "currentUserId is Null");
		}else{
			checkAndSet(context);
			StringBuilder sb = new StringBuilder();
			sb.append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames._ID)).append("=\'").append(index).append("\'");
			Cursor cursor = null;
			try{				
				ContentResolver cr = context.getContentResolver();
				cursor = cr.query(TipsTableAndUri.CONTENT_URI, null, sb.toString(), null, null);
				
				if (cursor == null || !cursor.moveToFirst()) {
					closeCursor(cursor);
					return null;
				}
				
				if (cursor.moveToFirst()) {
					TipsTableAndUri.ColumnNames[] columnNamesArray = TipsTableAndUri.ColumnNames.values();
					HashMap<ColumnNames, String> tipData = new HashMap<ColumnNames, String>();
					for (int i = 0; i < columnNamesArray.length; i++) {
						tipData.put(columnNamesArray[i], getStringData(cursor, TipsTableAndUri.getColumnString(i)));
					}
					tip = new Tip(tipData);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				closeCursor(cursor);
			}
		}
		return tip;
	}
	
	public static boolean addTip(Tip tip, Context context) throws SQLiteConstraintException, SQLException {
		checkAndSet(context);
		ContentValues contentValues = new ContentValues();
		tip.setIndex(getTipList(context).size());
		HashMap<String, String> tipData = tip.getTipData();
		for (int i = 0; i < tipData.size(); i++) {
			contentValues.put(TipsTableAndUri.getColumnString(i), tipData.get(TipsTableAndUri.getColumnString(i)));
		}
		context.getContentResolver().insert(TipsTableAndUri.CONTENT_URI, contentValues);
		return true;	
	}
	
	public static boolean addBatchTip(ArrayList<Tip> tipList, Context context) throws SQLiteConstraintException, SQLException {
		checkAndSet(context);
		ContentValues[] contentValues = new ContentValues[tipList.size()];
		int index = getTipList(context).size();
		for(int i = 0; i < tipList.size(); ++i) {
			tipList.get(i).setIndex(index);
			ContentValues tempContentValue = new ContentValues();
			HashMap<String, String> tipData = tipList.get(i).getTipData();
			for (int j = 0; j < tipData.size(); j++) {
				tempContentValue.put(TipsTableAndUri.getColumnString(j), tipData.get(TipsTableAndUri.getColumnString(j)));
			}
			contentValues[i] = tempContentValue;
			index++;
		}
		context.getContentResolver().bulkInsert(TipsTableAndUri.CONTENT_URI, contentValues);
		return true;	
	}
	
	public static boolean updateTip(Tip tip, Context context) throws SQLException {
		checkAndSet(context);
		HashMap<String, String> tipData = tip.getTipData();
		ContentValues contentValues = new ContentValues();
		for (int i = 0; i < tipData.size(); i++) {
			if(tipData.get(TipsTableAndUri.getColumnString(i)) != null){
				contentValues.put(TipsTableAndUri.getColumnString(i), tipData.get(TipsTableAndUri.getColumnString(i)));
			}		
		}
		StringBuilder sb = new StringBuilder();
		sb.append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames._ID)).append("=\'").append(tip.getIndex()).append("\'");
		try {
			if(getTip(Integer.toString(tip.getIndex()), context) != null) {
				context.getContentResolver().update(TipsTableAndUri.CONTENT_URI, contentValues, sb.toString(), null);
			}else{
				context.getContentResolver().insert(TipsTableAndUri.CONTENT_URI, contentValues);
			}
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean deleteAll(Context context) throws SQLException {	
		checkAndSet(context);
		int i = context.getContentResolver().delete(TipsTableAndUri.CONTENT_URI, null, null);	
		if (i < 0){
			return false;
		}
		return true;
	}
	
	public static boolean deleteTip(String index, Context context) throws SQLException {	
		checkAndSet(context);
		StringBuilder sb = new StringBuilder();
		sb.append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames._ID)).append("=\'").append(index).append("\'");
		StringBuilder sb1 = new StringBuilder();
		sb1.append(TipsTableAndUri.getColumnString(TipsTableAndUri.ColumnNames._ID)).append(">\'").append(index).append("\'");
		Cursor cursor = null;
		try {
			int nRes = context.getContentResolver().delete(TipsTableAndUri.CONTENT_URI, sb.toString(), null);
			if (nRes == 1) {
				cursor = context.getContentResolver().query(TipsTableAndUri.CONTENT_URI, null, sb1.toString(), null, TipsTableAndUri.getColumnString(ColumnNames._ID));
				if (cursor == null || !cursor.moveToFirst()) {
					closeCursor(cursor);
					return false;
				}
				
				if (cursor.moveToFirst()) {
					TipsTableAndUri.ColumnNames[] columnNamesArray = TipsTableAndUri.ColumnNames.values();
					HashMap<ColumnNames, String> tipData = new HashMap<ColumnNames, String>();
					for (int i = 0; i < columnNamesArray.length; i++) {
						tipData.put(columnNamesArray[i], getStringData(cursor, TipsTableAndUri.getColumnString(i)));
					}
					Tip tip = new Tip(tipData);
					tip.setIndex(tip.getIndex() - 1);
					if(updateTip(tip, context)) {
						return true;
					}else {
						return false;
					}
				}else {
					return false;
				}
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isEmpty(Context context) throws SQLException {
		checkAndSet(context);
		Cursor c = context.getContentResolver().query(TipsTableAndUri.CONTENT_URI, null, null, null, null);
		if(c.getCount() == 0) {
			closeCursor(c);
			return true;
		}
		closeCursor(c);
		return false;
	}
	
	private static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}
}
