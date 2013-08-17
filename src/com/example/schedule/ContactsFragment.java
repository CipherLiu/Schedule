package com.example.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class ContactsFragment extends Fragment{

	private String userId;
	private String groupListString;
	private Button newGroup;
	private Button newFriends;
	private ArrayList<GroupInfo> groupList = new ArrayList();
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ProgressDialog progressDialog;
     /*
      * Getting group data here
      * format goes with {title,info,img}
      */
    private Map<String, Object> map = new HashMap<String, Object>();
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
		//System.out.println(groupListString);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {     
		View view=inflater.inflate(R.layout.fragment_contacts, container, false);
		
		newGroup=(Button) view.findViewById(R.id.fc_group_new);
		newFriends = (Button) view.findViewById(R.id.fc_friends_new);
		
		lv=(ListView) view.findViewById(R.id.fc_group_list);
		
		newGroup.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NewGroupAT().execute(userId);
			}
			
		});
		newFriends.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
            	intent.setClass(ContactsFragment.this.getActivity(),NewFriendsActivity.class );
            	intent.putExtra("userIdToAddNewFriends", userId);
            	ContactsFragment.this.startActivityForResult(intent, 11);
			}
			
		});
		init();
		return view;
		//return inflater.inflate(R.layout.fragment_contacts, container, false);
	    //DrawView drawView = new DrawView(this.getActivity());
	    //return drawView;
    }
	class NewGroupAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Intent intent=new Intent();
        	intent.setClass(ContactsFragment.this.getActivity(),NewGroupActivity.class );
        	intent.putExtra("userIdToCreateGp", userId);
			ContactsFragment.this.startActivityForResult(intent, 10);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 10 && resultCode == 1){
			//Get data from new group
			Bundle extras = data.getExtras();
			String newCreateGroupId= extras.getString("newCreateGroupId");
			String newCreateGroupName= extras.getString("newCreateGroupName");
			//Store into GroupInfo
			GroupInfo group = new GroupInfo();
			group.setId(newCreateGroupId);
			group.setName(newCreateGroupName);
			groupList.add(group);
			//Change ListView
			map = new HashMap<String, Object>();
			map.put("title", newCreateGroupName);
			map.put("info", "");
			map.put("img", R.drawable.no_photo_small);
			list.add(map);
			//Notify
			SimpleAdapter sAdapter = new SimpleAdapter(this.getActivity(),list,R.layout.group_listview_item,
	                new String[]{"title","info","img"},
	                new int[]{R.id.item_title,R.id.item_info,R.id.item_img});
			sAdapter.notifyDataSetChanged();
			//Debug info
			//System.out.println("From new group");
		}
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
        //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        /*
         * Getting group data here
         * format goes with {title,info,img}
         */
        //Map<String, Object> map = new HashMap<String, Object>();
		
        try {
			JSONArray jsonArray = new JSONArray(groupListString);
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				GroupInfo group = new GroupInfo();
				group.setId(jsonObject.getString("_id"));
				group.setName(jsonObject.getString("groupName"));
				groupList.add(group);
			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        for(int i=0 ; i< groupList.size();i++){
        	map = new HashMap<String, Object>();
        	map.put("title", groupList.get(i).getName());
        	map.put("info", "");
        	map.put("img", R.drawable.no_photo_small);
        	list.add(map);
        }
        return list;
	}
}
