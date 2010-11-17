package com.confuzed.android.countdowns;

import com.confuzed.android.countdowns.CountdownsProviderMetaData.CountdownTableMetaData;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class CountdownsActivity extends ListActivity {
	private static final String[] PROJECTION = {
		CountdownTableMetaData.COUNTDOWN_NAME,
		CountdownTableMetaData.COUNTDOWN_DUE
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = getIntent();
        if( intent.getData() == null )
        {
        	intent.setData(CountdownTableMetaData.CONTENT_URI);
        }
        
        Cursor c = managedQuery(
        		intent.getData(),
        		PROJECTION,
        		null, null,
        		CountdownTableMetaData.DEFAULT_SORT_ORDER
        		);
        
        SimpleCursorAdapter adapter =
        	new SimpleCursorAdapter(
        			this,
        			R.layout.countdown_item,
        			c,
        			new String[] {
        				CountdownTableMetaData.COUNTDOWN_NAME,
        				CountdownTableMetaData.COUNTDOWN_DUE
        			},
        			new int[] {
        				R.id.TvItemTitle,
        				R.id.TvItemCountdown
        			}
        	);
        
        this.setListAdapter(adapter);
        
    }
}