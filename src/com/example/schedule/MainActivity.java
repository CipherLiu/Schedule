package com.example.schedule;


import java.util.ArrayList;
import java.util.Calendar;


import com.example.schedule.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;



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
    private boolean hasEventArray[] = new boolean[42];
    private String email,userId;
    private Calendar calSelected = Calendar.getInstance();
    private Calendar calToday = Calendar.getInstance();
    private MeFragment myScheduleFragment = new MeFragment();
    private SocialFragment whatsNewFragment = new SocialFragment();
	private ContactsFragment groupFragment = new ContactsFragment();
    
    public String getEmail() {
		return email;
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
        
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
				newEventIntent.putExtra("hourOfDay", calSelected.get(Calendar.HOUR_OF_DAY));
				newEventIntent.putExtra("minute", calSelected.get(Calendar.MINUTE));
				newEventIntent.putExtra("userEmail", email);
				newEventIntent.putExtra("userId", userId);
				newEventIntent.setClass(MainActivity.this, NewEventActivity.class);
				startActivity(newEventIntent);
				return true;
			}
        	
        });
        MenuItem miToday = menu.findItem(R.id.menu_today_main);
        miToday.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				calSelected.setTimeInMillis(calToday.getTimeInMillis());
				myScheduleFragment.update(calSelected);
				return true;
			}
        	
        });
        return true;
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
            	return whatsNewFragment;
            case 2:
            	//Fragment groupFragment = new GroupFragment();
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
}