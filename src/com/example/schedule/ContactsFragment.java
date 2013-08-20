package com.example.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;


public class ContactsFragment extends Fragment{

	private String userId;
	private String groupListString;
	public ContactsFragment() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userId = this.getArguments().getString("userId");
		groupListString = this.getArguments().getString("groupListString");
		System.out.println(groupListString);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {       	
        return inflater.inflate(R.layout.fragment_social, container, false);
    }
}
