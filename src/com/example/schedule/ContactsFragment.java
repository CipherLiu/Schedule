package com.example.schedule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    private String paraToNewFriendsActivity;
    private String paraToNewGroupActivity;
    private String paraToEventDetailActivity;
    private String groupIdToEventDetailActivity;
    private String groupNameToEventDetailActivity;
    private static String stranger_check_url = Global.BASICURL+"StrangerCheck";
    private static String friends_check_url = Global.BASICURL+"FriendsCheck";
    private static String group_social_url = Global.BASICURL+"GroupSocial";
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
				new NewFriendsAT().execute(userId);
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
			if(!params[0].isEmpty()){
				try{
					HttpClient httpClient = new DefaultHttpClient();
					//Compose a http get url
					String query = URLEncoder.encode("userId", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[0], "utf-8");
					String urlParams = "?"+query;
					HttpGet httpget = new HttpGet(friends_check_url+urlParams);
					//http response
					HttpResponse httpResponse = httpClient.execute(httpget);
					int result;
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						paraToNewGroupActivity = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8");  
						JSONObject resultJSON = new JSONObject(paraToNewFriendsActivity);
						result = resultJSON.getInt("result");
					}else{
						return Primitive.CONNECTIONREFUSED;
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					return result;
				}catch(HttpHostConnectException e){
					e.printStackTrace();
					return Primitive.CONNECTIONREFUSED;
				}catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
		}else{
			return -1;
		}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			 * Go to NewGroupActivity and pass the params
			 */
			Intent intent=new Intent();
        	intent.setClass(ContactsFragment.this.getActivity(),NewGroupActivity.class );
        	intent.putExtra("userIdToCreateGp", userId);
        	intent.putExtra("userIdToSelectInGroup", paraToNewGroupActivity);
        	intent.putExtra("existGroupList", groupListString);
			ContactsFragment.this.startActivityForResult(intent, 10);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}
	}
	class NewFriendsAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
				if(!params[0].isEmpty()){
					
					try{
						HttpClient httpClient = new DefaultHttpClient();
						String query = URLEncoder.encode("userId", "utf-8");
						query += "=";
						query += URLEncoder.encode(params[0], "utf-8");
						String urlParams = "?"+query;
						HttpGet httpget = new HttpGet(stranger_check_url+urlParams);
						HttpResponse httpResponse = httpClient.execute(httpget);
						int result;
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							paraToNewFriendsActivity = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8");  
							JSONObject resultJSON = new JSONObject(paraToNewFriendsActivity);
							result = resultJSON.getInt("result");
						}else{
							return Primitive.CONNECTIONREFUSED;
						}
						if (httpClient != null) {
							httpClient.getConnectionManager().shutdown();
						}
						return result;
					}catch(HttpHostConnectException e){
						e.printStackTrace();
						return Primitive.CONNECTIONREFUSED;
					}catch (Exception e) {
						e.printStackTrace();
						return -1;
					}
			}else{
				return -1;
			}
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			 * Go to NewFriendsActivity and pass the params
			 */
			Intent intent=new Intent();
        	intent.setClass(ContactsFragment.this.getActivity(),NewFriendsActivity.class );
        	intent.putExtra("userIdToAddNewFriends", userId);
        	intent.putExtra("userIdNewlyReg", paraToNewFriendsActivity);
        	ContactsFragment.this.startActivityForResult(intent, 11);
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
                	TextView ev = (TextView)arg0.getChildAt(arg2).findViewById(R.id.item_title);
                	for(int i=0 ; i < groupList.size() ; i++){
                		if(groupList.get(i).getName().equals(ev.getText().toString())){
                			groupIdToEventDetailActivity = groupList.get(i).getId();
                			groupNameToEventDetailActivity = groupList.get(i).getName();
                		}
                	}
                	new GetDrawDataAT().execute(userId,groupIdToEventDetailActivity);
                }
            }  
        }); 
	}	
	class GetDrawDataAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
				if(!params[0].isEmpty()){
					
					try{
						HttpClient httpClient = new DefaultHttpClient();
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND,0);
						String query = URLEncoder.encode("userId", "utf-8");
						query += "=";
						query += URLEncoder.encode(params[0], "utf-8");
						query += "&";
						query += URLEncoder.encode("groupId","utf-8");
						query += "=";
						query += URLEncoder.encode(params[1],"utf-8");
						query += "&";
						query += URLEncoder.encode("dateTimeInMillis","utf-8");
						query += "=";
						query += URLEncoder.encode(String.valueOf(cal.getTimeInMillis()),"utf-8");
						
						String urlParams = "?"+query;
						
						HttpGet httpget = new HttpGet(group_social_url+urlParams);
						System.out.println("Get url"+group_social_url+urlParams);
						HttpResponse httpResponse = httpClient.execute(httpget);
						int result;
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							paraToEventDetailActivity = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8");  
							JSONObject resultJSON = new JSONObject(paraToEventDetailActivity);
							result = resultJSON.getInt("result");
						}else{
							return Primitive.CONNECTIONREFUSED;
						}
						if (httpClient != null) {
							httpClient.getConnectionManager().shutdown();
						}
						return result;
					}catch(HttpHostConnectException e){
						e.printStackTrace();
						return Primitive.CONNECTIONREFUSED;
					}catch (Exception e) {
						e.printStackTrace();
						return -1;
					}
			}else{
				return -1;
			}
			
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch(result){
			case Primitive.CONNECTIONREFUSED:
				Toast connectError = Toast.makeText(ContactsFragment.this.getActivity(),
					     "Cannot connect to the server", Toast.LENGTH_LONG);
				connectError.setGravity(Gravity.CENTER, 0, 0);
				connectError.show();
				break;
			case Primitive.ACCEPT:
			/*
			 * Go to EventDetailActivity and pass the params
			 */
			Intent intent=new Intent();
        	intent.setClass(ContactsFragment.this.getActivity(),EventDetailActivity.class );
        	intent.putExtra("userIdToEventDetailActivity", userId);
        	intent.putExtra("groupIdToEventDetailActivity",groupIdToEventDetailActivity);
        	intent.putExtra("groupNameToEventDetailActivity", groupNameToEventDetailActivity);
        	intent.putExtra("paraToEventDetailActivity", paraToEventDetailActivity);
        	ContactsFragment.this.startActivity(intent);
				break;
			default:
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
		}
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
