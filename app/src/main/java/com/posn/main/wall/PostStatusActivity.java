package com.posn.main.wall;

import com.posn.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class PostStatusActivity extends Activity
	{

		// declare variables
		EditText status;

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_post_status);
				
				status = (EditText) findViewById(R.id.postStatus);
				
        getActionBar().setDisplayHomeAsUpEnabled(true);

			}


		@Override
		public boolean onCreateOptionsMenu(Menu menu)
			{
				// Inflate the menu items for use in the action bar
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.main_activity_actions, menu);
				// get mute button
				// muteButton = menu.findItem(R.id.action_mute);

				return super.onCreateOptionsMenu(menu);
			}
		
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: 
            // API 5+ solution
            onBackPressed();
            return true;
       
        case R.id.action_post:
        	
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(status.getWindowToken(), 0);
					
        	String statusMsg = status.getText().toString();
        	
        	Intent resultIntent = new Intent();
        	resultIntent.putExtra("status", statusMsg);
        	setResult(Activity.RESULT_OK, resultIntent);
       		finish();
          return true;
          

        default:
            return super.onOptionsItemSelected(item);
        }
    }
	}
