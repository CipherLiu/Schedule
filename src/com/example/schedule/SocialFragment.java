package com.example.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SocialFragment extends Fragment {
	private boolean dayViewIsSelected = true;
	
	public SocialFragment() {
		
	}
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {       	
	     if(dayViewIsSelected)  
	    	 return inflater.inflate(R.layout.fragment_social_dayview, container, false);
	     else
	    	 return inflater.inflate(R.layout.fragment_social, container, false);
	    }

	 
}
