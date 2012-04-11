package br.unicamp.yamba;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;


public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener {
	
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	Twitter twitter;
	TextView textCount;
    SharedPreferences prefs;


    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);


        
     // Find views
        editText = (EditText) findViewById(R.id.editText);
        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);
        
        
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        editText.addTextChangedListener(this);

        
        prefs = PreferenceManager.getDefaultSharedPreferences(this); 
        prefs.registerOnSharedPreferenceChangeListener(this);
        

    }
	
	
	
	private Twitter getTwitter() {
	    if (twitter == null) {
	      String username, password, apiRoot;
	      username = prefs.getString("user", "");
	      password = prefs.getString("pass", "");
	      apiRoot = prefs.getString("apiroot", "http://yamba.marakana.com/api");

	      // Connect to twitter service
	      twitter = new Twitter(username, password);
	      twitter.setAPIRootUrl(apiRoot);
	    }
	    return twitter;
	  }
	
	// Called first time user clicks on the menu button
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemPrefs:
				startActivity(new Intent(this, PrefsActivity.class)); 
				break;
			}
		return true;
	}


	// TextWatcher methods
	public void afterTextChanged(Editable statusText) {
		int count = 140 - statusText.length(); 
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN); 
	
		if (count < 20)	textCount.setTextColor(Color.YELLOW);
		if (count < 0)textCount.setTextColor(Color.RED);
	}
	

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}


	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	
	// Called when button is clicked
	public void onClick(View v) {
		String status = editText.getText().toString();
	    new PostToTwitter().execute(status);
	    Log.d(TAG, "onClicked");

	}
	
	
	
	
	
	// Asynchronously posts to twitter
	  class PostToTwitter extends AsyncTask<String, Integer, String> {
	    // Called to initiate the background activity
	    @Override
	    protected String doInBackground(String... statuses) {
	      try {
	        Twitter.Status status = getTwitter().updateStatus(statuses[0]);
	        return status.text;
	      } catch (TwitterException e) {
	        Log.e(TAG, e.toString());
	        e.printStackTrace();
	        return "Erro na Postagem!";
	      }
	    }
		
		// Called when there's a status to be updated
		protected void onProgressUpdate(Integer... values) { 
			super.onProgressUpdate(values);
			// Not used in this case
		}
		
		// Called once the background activity has completed
		protected void onPostExecute(String result) { 
		
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}


	}



	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		// invalidate twitter object
		twitter = null;

		
	}



}


