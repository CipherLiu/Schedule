package com.example.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;


public class ContactsFragment extends Fragment{

	public ContactsFragment() {
		// TODO Auto-generated constructor stub
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {       	
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }
}
