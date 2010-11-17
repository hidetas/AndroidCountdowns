/**
 * 
 */
package com.confuzed.android.countdowns;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.confuzed.android.countdowns.CountdownsProviderMetaData.CountdownTableMetaData;

/**
 * @author rjf
 *
 */
public class CountdownProvider extends ContentProvider {
	
	private static HashMap<String, String> sProjectionMap;
	
	static {
		sProjectionMap = new HashMap<String, String>();
		
		sProjectionMap.put(
				CountdownTableMetaData._ID,
				CountdownTableMetaData._ID
				);
		
		sProjectionMap.put(
				CountdownTableMetaData.COUNTDOWN_NAME,
				CountdownTableMetaData.COUNTDOWN_NAME
				);
		sProjectionMap.put(
				CountdownTableMetaData.COUNTDOWN_DUE,
				CountdownTableMetaData.COUNTDOWN_DUE
				);
		sProjectionMap.put(
				CountdownTableMetaData.COUNTDOWN_CREATED,
				CountdownTableMetaData.COUNTDOWN_CREATED
				);
		sProjectionMap.put(
				CountdownTableMetaData.COUNTDOWN_MODIFIED,
				CountdownTableMetaData.COUNTDOWN_MODIFIED
				);
		
	}
	
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

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		public DatabaseHelper(Context context) {
			super(
					context,
					CountdownsProviderMetaData.DATABASE_NAME,
					null,
					CountdownsProviderMetaData.DATABASE_VERSION
				);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(Countdowns.TAG, "Creating database");
			db.execSQL(
					"CREATE table " + CountdownsProviderMetaData.COUNTDOWNS_TABLE_NAME + " ( "
					+ CountdownTableMetaData._ID
					+ " INTEGER PRIMARY KEY NOT NULL, "
					+ CountdownTableMetaData.COUNTDOWN_NAME
					+ " TEXT, "
					+ CountdownTableMetaData.COUNTDOWN_DUE
					+ " INTEGER, "
					+ CountdownTableMetaData.COUNTDOWN_CREATED
					+ " INTEGER, "
					+ CountdownTableMetaData.COUNTDOWN_MODIFIED
					+ " INTEGER"
					+ ");"
				);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(Countdowns.TAG, "Upgrading database from version" 
					+ oldVersion + " to version " + newVersion );
		}	
	}
	
	private DatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return false;
	}

	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		
		switch(sUriMatcher.match(uri))
		{
		case CountdownUriMatches.COUNTDOWN_LIST:
			count = db.delete(CountdownTableMetaData.TABLE_NAME,
						selection, selectionArgs);
			break;
		case CountdownUriMatches.COUNTDOWN_ITEM:
			String id = uri.getPathSegments().get(1);
			count = db.delete(CountdownTableMetaData.TABLE_NAME,
					CountdownTableMetaData._ID + "=" + id +
					(TextUtils.isEmpty(selection) ? "" : " AND ( "
						+ selection + " )"),
					selectionArgs
					);
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri " + uri);
		}
		
		if( count > 0 )
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		switch(match)
		{
		case CountdownUriMatches.COUNTDOWN_ITEM:
			return CountdownTableMetaData.CONTENT_ITEM_TYPE;
		case CountdownUriMatches.COUNTDOWN_LIST:
			return CountdownTableMetaData.CONTENT_TYPE;
		default:
			Log.e(Countdowns.TAG, "Bad Uri " + uri + " in getType");
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if( sUriMatcher.match(uri) != CountdownUriMatches.COUNTDOWN_ITEM )
		{
			Log.e(Countdowns.TAG, "Bad Uri " + uri + " in INSERT");
			throw new IllegalArgumentException("Unknown Uri " + uri);
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		
		if( values.containsKey(CountdownTableMetaData.COUNTDOWN_CREATED) == false )
		{
			values.put(CountdownTableMetaData.COUNTDOWN_CREATED, now);
		}
		
		if( values.containsKey(CountdownTableMetaData.COUNTDOWN_MODIFIED) == false )
		{
			values.put(CountdownTableMetaData.COUNTDOWN_MODIFIED, now);
		}
		
		if( values.containsKey(CountdownTableMetaData.COUNTDOWN_NAME) == false )
		{
			throw new SQLException("Required name value is missing");
		}
		
		if( values.containsKey(CountdownTableMetaData.COUNTDOWN_DUE) == false )
		{
			throw new SQLException("Required due value is missing");
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(
				CountdownTableMetaData.TABLE_NAME,
				CountdownTableMetaData.COUNTDOWN_NAME,
				values
				);
		
		if( rowId > 0 )
		{
			Uri newRowUri = ContentUris.withAppendedId(
					CountdownTableMetaData.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(newRowUri, null);
			return newRowUri;
		}
		
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch(sUriMatcher.match(uri))
		{
		case CountdownUriMatches.COUNTDOWN_LIST:
			qb.setTables(CountdownsProviderMetaData.COUNTDOWNS_TABLE_NAME);
			qb.setProjectionMap(sProjectionMap);
			
			break;
		case CountdownUriMatches.COUNTDOWN_ITEM:
			qb.setTables(CountdownsProviderMetaData.COUNTDOWNS_TABLE_NAME);
			qb.setProjectionMap(sProjectionMap);
			qb.appendWhere(
					CountdownTableMetaData._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			Log.e(Countdowns.TAG, "Bad Uri " + uri + " in QUERY");
			throw new IllegalArgumentException("Unknown Uri " + uri);
		}
		
		String order;
		if(TextUtils.isEmpty(sortOrder))
			order = CountdownTableMetaData.DEFAULT_SORT_ORDER;
		else
			order = sortOrder;
		
		SQLiteDatabase db =	mOpenHelper.getReadableDatabase();
		Cursor curs = qb.query(
				db,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				order);
		curs.setNotificationUri(getContext().getContentResolver(), uri);
		return curs;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		
		switch(sUriMatcher.match(uri))
		{
		case CountdownUriMatches.COUNTDOWN_LIST:
			count = db.update(CountdownTableMetaData.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case CountdownUriMatches.COUNTDOWN_ITEM:
			String id = uri.getPathSegments().get(1);
			count = db.update(CountdownTableMetaData.TABLE_NAME,
					values,
					CountdownTableMetaData._ID + "=" + id +
					(TextUtils.isEmpty(selection) ? "" : " AND ( "
						+ selection + " )"),
					selectionArgs
					);
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri " + uri);
		}
		
		if( count > 0 )
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		
		return count;
	}

}
