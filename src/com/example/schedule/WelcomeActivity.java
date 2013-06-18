package com.example.schedule;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

	//Define go
	private static final int Go_LogIn = 1000;  
	private static final int Go_Guide = 1001; 
	//Splash delay time
	private static final long SPLASH_DELAY_MILLIS = 3000; 
	//Preference 
	boolean isFirstIn = false;  
	private static final String SHAREDPREFERENCES_NAME = "first_pref";  
	//Handler
	private Handler mHandler = new Handler() {  		  
	        @Override  
	        public void handleMessage(Message msg) {  
	            switch (msg.what) {  
	            case Go_LogIn:  
	            	goLogin();  
	                break;  
	            case Go_Guide:  
	                goGuide();  
	                break;  
	            }  
	            super.handleMessage(msg);  
	        }  
	    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
    }
	private void init() {  
        SharedPreferences preferences = getSharedPreferences(  
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);    
        isFirstIn = preferences.getBoolean("isFirstIn", true);  
        if (isFirstIn) { 
        //Assert false
        	mHandler.sendEmptyMessageDelayed(Go_Guide, SPLASH_DELAY_MILLIS);             
        } else {  
        	//mHandler.sendEmptyMessageDelayed(Go_Guide, SPLASH_DELAY_MILLIS); 
        	mHandler.sendEmptyMessageDelayed(Go_LogIn, SPLASH_DELAY_MILLIS); 
        }  
  
    }  
    private void goLogin() {  
    	Intent intent = new Intent();
		intent.setClass(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		WelcomeActivity.this.overridePendingTransition(R.layout.alpha_in, R.layout.alpha_out);
		finish();
    }  
  
    private void goGuide() {  
        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);  
        WelcomeActivity.this.startActivity(intent);  
        WelcomeActivity.this.finish();  
    }  
	@Override  
	//hold up BACK ops
    public boolean onKeyDown(int keyCode, KeyEvent event) {   
        if(keyCode==KeyEvent.KEYCODE_BACK) {   
            return false;   
        }   
        return false;   
    } 

}
