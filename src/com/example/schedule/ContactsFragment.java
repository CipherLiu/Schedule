package com.example.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class ContactsFragment extends Fragment{

	private String userId;
	private String groupListString;
	
	//DrawView drawView = new DrawView(this.getActivity());
	ListView lv;
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
		View view=inflater.inflate(R.layout.fragment_contacts, container, false);
		lv=(ListView) view.findViewById(R.id.fc_group_list);
		init();
		return view;
		//return inflater.inflate(R.layout.fragment_contacts, container, false);
	    //DrawView drawView = new DrawView(this.getActivity());
	    //return drawView;
    }
	/*
	 * Using SimpleAdapter to generates the ListView item
	 */
	private void init() {
		// TODO Auto-generated method stub
		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(),getData(),R.layout.group_listview_item,
                new String[]{"title","info","img"},
                new int[]{R.id.item_title,R.id.item_info,R.id.item_img});
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {  
	      	  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                if(true){
                	/*
                	 * arg2 denotes the ListView index
                	 */
                	//System.out.println(arg2);
                	//To string argument
                	String strArg=new String();
                	strArg=Integer.toString(arg2);
                	Intent intent=new Intent();
                	intent.setClass(ContactsFragment.this.getActivity(),EventDetailActivity.class );
                	intent.putExtra("whichGroup", strArg);
                	ContactsFragment.this.getActivity().startActivity(intent);
                }
            }  
        }); 
	}	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        /*
         * Getting group data here
         * format goes with {title,info,img}
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "Group1");
        map.put("info", "group 1");
        map.put("img", R.drawable.no_photo_small);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "Group2");
        map.put("info", "group 2");
        map.put("img", R.drawable.no_photo_small);
        list.add(map);
         
        return list;
	}
}
