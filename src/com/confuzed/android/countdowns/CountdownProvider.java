/**
 * 
 */
package com.confuzed.android.countdowns;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author rjf
 *
 */
public class CountdownProvider extends ContentProvider {
	
	// Possible return states from the UriMatcher...
	private static class CountdownUriMatches {
		public static final int COUNTDOWN_LIST = 1;
		public static final int COUNTDOWN_ITEM = 2;
		
	}
	
	private static UriMatcher sUriMatcher;
	
	// Initialise the static Uri Matcher
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(
				CountdownsProviderMetaData.AUTHORITY,
				"countdowns",
				CountdownUriMatches.COUNTDOWN_LIST);
		sUriMatcher.addURI(
				CountdownsProviderMetaData.AUTHORITY,
				"countdowns/#",
				CountdownUriMatches.COUNTDOWN_ITEM);	
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		switch(match)
		{
		case CountdownUriMatches.COUNTDOWN_ITEM:
			return CountdownsProviderMetaData.CountdownTableMetaData.CONTENT_ITEM_TYPE;
		case CountdownUriMatches.COUNTDOWN_LIST:
			return CountdownsProviderMetaData.CountdownTableMetaData.CONTENT_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
