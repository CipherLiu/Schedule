package com.example.activity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.fragment.ContactsFragment;
import com.example.fragment.MeFragment;
import com.example.fragment.SocialFragment;
import com.example.net.Global;
import com.example.net.Primitive;
import com.example.schedule.R;
import com.example.schedule.R.id;
import com.example.schedule.R.layout;
import com.example.schedule.R.menu;
import com.example.schedule.R.string;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;



public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    ViewPager mViewPager;
    private static String eventCheckUrl = Global.BASICURL+"EventCheck";
    private boolean hasEventArray[] = new boolean[42];
    private String socialEventArrayString; 
//    private EventInfo socialEventArray[] = new EventInfo[10];
//    private ArrayList<GroupInfo> groupList = new ArrayList();
    private String groupListString;
    private String email,userId;
    private Calendar calSelected = Calendar.getInstance();
    private Calendar calToday = Calendar.getInstance();
    private MeFragment myScheduleFragment = new MeFragment();
    private SocialFragment whatsNewFragment = new SocialFragment();
	private ContactsFragment groupFragment = new ContactsFragment();
	private ProgressDialog progressDialog;
	private long exitTime = 0;
	public String getEmail() {
		return email;
	}

	public String getGroupListString() {
		return groupListString;
	}

	public void setGroupListString(String groupListString) {
		this.groupListString = groupListString;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getCalSelected() {
		return calSelected;
	}

	public void setCalSelected(Calendar calSelected) {
		this.calSelected = calSelected;
	}

	public Calendar getCalToday() {
		return calToday;
	}

	public void setCalToday(Calendar calToday) {
		this.calToday = calToday;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");
        userId = extras.getString("userId");
        hasEventArray = extras.getBooleanArray("hasEventArray");
        groupListString = extras.getString("groupListString");
        socialEventArrayString = extras.getString("socialEventArrayString");
        progressDialog = new ProgressDialog(MainActivity.this); 
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the
        // Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if ((System.currentTimeMillis() - exitTime) > 2000) {  
			 
	        Toast.makeText(this, "再次按返回键退出", Toast.LENGTH_SHORT).show();  
	 
	        exitTime = System.currentTimeMillis();  
	 
	    } else {  
	 
	        super.onBackPressed();  
	 
	    } 
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        MenuItem miNewEvent = menu.findItem(R.id.menu_new_main);
        miNewEvent.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent newEventIntent = new Intent();
				calSelected.setTimeInMillis(myScheduleFragment.getCalSelected().getTimeInMillis());
				newEventIntent.putExtra("year", calSelected.get(Calendar.YEAR));
				newEventIntent.putExtra("month", calSelected.get(Calendar.MONTH));
				newEventIntent.putExtra("dayOfMonth", calSelected.get(Calendar.DAY_OF_MONTH));
				newEventIntent.putExtra("hourOfDay", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				newEventIntent.putExtra("minute", Calendar.getInstance().get(Calendar.MINUTE));
				Calendar cal = Calendar.getInstance();
				calSelected.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
				calSelected.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
				newEventIntent.putExtra("calendar", calSelected.getTimeInMillis());
				newEventIntent.putExtra("userEmail", email);
				newEventIntent.putExtra("userId", userId);
				newEventIntent.putExtra("groupListString",groupListString);
				newEventIntent.putExtra("from", "MainActivity");
				newEventIntent.setClass(MainActivity.this, NewEventActivity.class);
				startActivityForResult(newEventIntent , 1);
				return true;
			}
        	
        });
        MenuItem miToday = menu.findItem(R.id.menu_today_main);
        miToday.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Calendar dateToCheck = getStartDate(Calendar.getInstance(),Calendar.SUNDAY);
				String calString = dateToCheck.getTimeInMillis()+"";
				new eventCheckAT().execute(calString,userId);
				return true;
			}
        	
        });
        return true;
    }
	class eventCheckAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(eventCheckUrl+"?dateTimeInMillis="+params[0] 
					+"&userId="+params[1]);
			try {
				HttpResponse httpResponse = httpClient.execute(httpget);
				JSONObject resultJSON = new JSONObject();
				if(httpResponse.getStatusLine().getStatusCode() == 200){
					String retSrc = EntityUtils.toString(httpResponse.getEntity()); 
					resultJSON = new JSONObject(retSrc);
				}else{
					resultJSON.put("result", Primitive.CONNECTIONREFUSED);
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
				return resultJSON;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.cancel();
			int resultCode;
			try {
				resultCode = result.getInt("result");
				
				switch(resultCode){
				case Primitive.CONNECTIONREFUSED:
					Toast connectError = Toast.makeText(MainActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("hasEventArray");
					for(int i = 0 ; i < jArray.length() ; i++){
					hasEventArray[i] = jArray.getBoolean(i);
					}
					calSelected.setTimeInMillis(calToday.getTimeInMillis());
					myScheduleFragment.update(calSelected , hasEventArray);
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(MainActivity.this,
						     "Server database error", Toast.LENGTH_LONG);
					DBError.setGravity(Gravity.CENTER, 0, 0);
					DBError.show();
					break;	
				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressDialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();
		}
		
	}
    
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

 
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

   
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

	/**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public SectionsPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments){
        	super(fm);
        }
        
		@Override
        public Fragment getItem(int i) {
			switch (i) {
            case 0:
            	//Fragment myScheduleFragment = new MyScheduleFragment();
            	Bundle args = new Bundle();
            	args.putString("userId", userId);
            	args.putBooleanArray("hasEventArray", hasEventArray);
            	myScheduleFragment.setArguments(args);
            	return myScheduleFragment;
            case 1:
            	//Fragment whatsNewFragment = new WhatsNewFragment();
            	Bundle bubdleToSocial = new Bundle();
            	bubdleToSocial.putString("socialEventArrayString", socialEventArrayString);
            	bubdleToSocial.putString("userId", userId);
            	whatsNewFragment.setArguments(bubdleToSocial);
            	return whatsNewFragment;
            case 2:
            	//Fragment groupFragment = new GroupFragment();
            	Bundle bundleToGroup = new Bundle();
            	bundleToGroup.putString("userId", userId);
            	bundleToGroup.putString("groupListString", groupListString);
            	groupFragment.setArguments(bundleToGroup);
            	return groupFragment;
            }
			return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1);
                case 1: return getString(R.string.title_section2);
                case 2: return getString(R.string.title_section3);
            }
            return null;
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == 1){
			Bundle extras = data.getExtras();
			hasEventArray = extras.getBooleanArray("hasEventArray");
			Calendar calSeclected = (Calendar)extras.get("calSelected");
			myScheduleFragment.update(calSeclected ,hasEventArray);
		}else if(requestCode == 2 && resultCode == 1){
			if(data != null){
				Bundle extras = data.getExtras();
				hasEventArray = extras.getBooleanArray("hasEventArray");
				Calendar calSeclected = (Calendar)extras.get("calSelected");
				myScheduleFragment.update(calSeclected ,hasEventArray);
			}
		}
	}
    
	public Calendar getStartDate(Calendar calStartDate , int iFirstDayOfWeek) {
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		calStartDate.set(Calendar.MILLISECOND, 0);
		return calStartDate;
	}
	
}