/**
 * 
 */
package com.confuzed.android.countdowns;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author rjf
 *
 */
public class CountdownsProviderMetaData {
	public static final String AUTHORITY = "com.confuzed.android.countdowns";
	
	public static final String DATABASE_NAME = "countdowns.db";
	public static final int DATABASE_VERSION = 1;
	public static final String COUNTDOWNS_TABLE_NAME = "countdowns";
	
	
	public static final class CountdownTableMetaData
		implements BaseColumns {
		private CountdownTableMetaData() {};
		public static final String TABLE_NAME = "countdowns";
		
		public static final Uri CONTENT_URI =
			Uri.parse("content://" + AUTHORITY + "/countdowns");
		
		public static final String CONTENT_TYPE =
			"vnd.android.cursor.dir/vnd.androidcountdown.countdown";
		
		public static final String CONTENT_ITEM_TYPE =
			"vnd.andoird.cursor.item/vnd.androidcountdown.countdown";
		
		public static final String DEFAULT_SORT_ORDER = "due ASC";
		
		// Database columns
		
		// id, Int
		
		// name, String
		public static final String COUNTDOWN_NAME = "name";
		
		// due, Int
		public static final String COUNTDOWN_DUE = "due";
		
		// created, Int
		public static final String COUNTDOWN_CREATED = "created";
		
		// modified, Int
		public static final String COUNTDOWN_MODIFIED = "modified";
	}
}
