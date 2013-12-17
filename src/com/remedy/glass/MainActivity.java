package com.remedy.glass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.glass.app.Card;

public class MainActivity extends LogoutOnStopActivity {
	public static final String LOGIN_INTENT_EXTRA_CREDENTIALS = "login_credentials";
	protected static final String TAG = "Remedy.main";
	private User currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Keep the screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		String loginCredentials = getIntent().getStringExtra(LOGIN_INTENT_EXTRA_CREDENTIALS);
		currentUser = UserDB.getUser(loginCredentials);

		if (currentUser == null) {
			System.out.println("No user, forcing login.");
			
			//Starting service without username will create login activity
            Intent service = new Intent(this, RemedyService.class);
    		startService(service);
    		
    		finish();
		} else {		
			System.out.println("Logged in");
			
			//Logging in/making sure service is running
			Intent service = new Intent(this, RemedyService.class);
    		service.putExtra(RemedyService.EXTRA_USERNAME, currentUser.username);
    		startService(service);
    		
			Card card = createUserCard(currentUser);
			setContentView(card.toView());
		}
	}
	
	private Card createUserCard(User user) {
		Card card = new Card(this);
		card.setText(user.fullName);
		card.setInfo(user.title);
		card.addImage(user.image);
		
		return card;
	}
    
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	Log.i(TAG, "Asked to be destroyed");
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.remedy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.call_menu_item:
            	closeOptionsMenu();
                startActivity(new Intent(this, CallExpertActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
              openOptionsMenu();
              return true;
          }
          return false;
    }
}
