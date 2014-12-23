package com.chc.tipapi;

import android.net.Uri;
import android.provider.BaseColumns;

public class TipTableAndUri{

	public TipTableAndUri() {
	}

	public static final class TipsTableAndUri implements BaseColumns {
		private TipsTableAndUri() {
		}
		
		public static Uri CONTENT_URI = Uri.parse("content://com.chc.chctipfor7inchcontentprovider/tips");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.chc.tips";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.chc.tips";

		private static final String[] columnNames = { "_id", "tipId",  "type", "category", "gender",  "title", "key", "image", "content"};

		public static final String DEFAULT_SORT_ORDER = "_ID Asc";
		
		public enum ColumnNames {
			
			_ID (0, " INTEGER PRIMARY KEY AUTOINCREMENT"),
			TIP_ID (1, " TEXT UNIQUE"),
			TYPE (2, " TEXT"), 
			CATEGORY (3, " TEXT"), 
			GENDER (4, " TEXT"), 
			TITLE (5, " TEXT"), 
			KEY (6, " TEXT"), 
			IMAGE (7, " TEXT"), 
			CONTENT(8, " TEXT");

			private int i;
			private String createTableString;
			
			
			private ColumnNames(int index, String str) {
				this.i = index;
				this.createTableString = str;
			}

			
			
			public int getIndex() {
				return this.i;
			}
			
			
			
			public String getCreateTableString() {
				return this.createTableString;
			}
			
		}
		
		public static String getColumnString(ColumnNames cn) {
			
			return getColumnString(cn.getIndex());
		}
		
		public static String getColumnString(int i) {
			return columnNames[i];
		}
	}

}


