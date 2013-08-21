package com.example.schedule;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import android.app.ProgressDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schedule.NewEventActivity.OneDayEventQueryAT;
import com.example.schedule.NewEventActivity.eventCheckAT;
import com.example.schedule.RegisterActivity.RegisterAT;

import android.app.Activity;  
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;  
import android.view.ContextMenu;  
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;  
import android.view.View;  
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;  
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnCreateContextMenuListener;  
import android.widget.AbsListView;
import android.widget.AdapterView;  
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.TextView;

public class NewGroupActivity extends Activity {
	private List<Map<String, Object>> mData;
	private ListView lv_new_gp;
    private GroupItemAdapter mAdapter;
    private ArrayList<String> list;
    private ArrayList<UserInfo> friends = new ArrayList();
    private ArrayList<String> members = new ArrayList();
    private EditText et_newGroupName;
    private String userId;
    private static String url = Global.BASICURL+"GroupCreate";
    private String httpRespond;
    private String newGroupName;
    private String existGroupList;
    public static Map<Integer, Boolean> isSelected;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Activity layout
		setContentView(R.layout.activity_new_group);
		//ListView element
        lv_new_gp = (ListView) findViewById(R.id.lv_new_group);
        mAdapter = new GroupItemAdapter(this, lv_new_gp);
        et_newGroupName = (EditText) findViewById(R.id.et_new_group_name);
        
        userId = getIntent().getStringExtra("userIdToCreateGp");
        //For checking if can create new group
        existGroupList = getIntent().getStringExtra("existGroupList");
    	
        /*
         * Parse JSON data here
         */
        if(!getIntent().getStringExtra("userIdToSelectInGroup").isEmpty()){
	        try {
	        	/*
	        	 * Load JSON string from intent,
	        	 * parse the JSONArray to fill the UserInfo,
	        	 * after these,do add
	        	 */
					JSONObject resultJSON = new JSONObject(getIntent().getStringExtra("userIdToSelectInGroup"));
					JSONObject retrieveArray = new JSONObject();
					JSONArray resultJSONArray = new JSONArray();
					resultJSONArray = resultJSON.getJSONArray("friendsArray");
					for(int i=0 ; i< resultJSONArray.length();i++){
						UserInfo user = new UserInfo();
						retrieveArray = (JSONObject) resultJSONArray.get(i);
						
						user.setUsername(retrieveArray.getString("friendName"));												
						user.setImage(retrieveArray.getString("friendImage"));
				        user.setUserId(retrieveArray.getString("friendId"));
				        friends.add(user); 
					}
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        isSelected = new HashMap<Integer, Boolean>();  
	        for(int i=0 ; i < friends.size();i++){
	        	mAdapter.addUser(friends.get(i));
	        	isSelected.put(i, false); 
	        }
	        lv_new_gp.setAdapter(mAdapter);
	        lv_new_gp.setOnItemClickListener(new OnItemClickListener() {
	
	            @Override
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	                    long arg3) {
	            	CheckBox cb = (CheckBox)arg1.findViewById(R.id.cb_new_gp_is_add);
	            	//CheckBox toggle
	            	cb.toggle();
	            	isSelected.put(arg2, cb.isChecked());
	            	if(cb.isChecked()){
	            		members.add(friends.get(arg2).getUserId());
	            	}else{
	            		
	            		for(int i = 0; i<members.size(); i++){
	            			if(members.get(i).contentEquals(friends.get(arg2).getUserId())){
	            				members.remove(i);
	            			}
	            		}
	            	}
	                
	            }
	        });
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();     
	    inflater.inflate(R.menu.activity_new_group, menu);
	    MenuItem miNewGPOk = (MenuItem)menu.findItem(R.id.btn_new_group_ok);
	    MenuItem miNewGPCancel = (MenuItem)menu.findItem(R.id.btn_new_group_cancel);
	    miNewGPOk.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				newGroupName = et_newGroupName.getText().toString();
				boolean canCreateNewGroup = true;
				/*
				 * construct the request string,as JSON format
				 */
				try {
					JSONArray parseExistGroupList = new JSONArray(existGroupList);
					JSONObject parseExistGroupItem = new JSONObject();
					for(int i=0 ; i < parseExistGroupList.length() ; i++){
						parseExistGroupItem = (JSONObject) parseExistGroupList.get(i);
						if(newGroupName.equals(parseExistGroupItem.getString("groupName"))){
							canCreateNewGroup = false;
						}
					}
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(canCreateNewGroup)
				{
					if(members.size()!=0){
						if(!newGroupName.isEmpty()){
							JSONObject sendObject = new JSONObject();
							try {
								
								JSONArray friends = new JSONArray();
								for(int i=0 ; i < members.size() ; i++){
									friends.put(members.get(i));
									sendObject.put("friends", friends);
								}
								sendObject.put("userId", userId);
								sendObject.put("groupName", newGroupName);
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//System.out.println("In New group at,send string is:"+sendObject.toString());
							new AddGroupRequestAT().execute(sendObject.toString());
						}else{
							Toast inputInvalid = Toast.makeText(NewGroupActivity.this,
								     "Input new group name", Toast.LENGTH_LONG);
							inputInvalid.setGravity(Gravity.CENTER, 0, 0);
							inputInvalid.show();
						}
					}else{
						Toast inputInvalid = Toast.makeText(NewGroupActivity.this,
							     "Please select at least one", Toast.LENGTH_LONG);
						inputInvalid.setGravity(Gravity.CENTER, 0, 0);
						inputInvalid.show();
					}
				}
				/*
				 * Group existed!
				 */
				else{
					Toast inputInvalid = Toast.makeText(NewGroupActivity.this,
						     "Group already existed!", Toast.LENGTH_LONG);
					inputInvalid.setGravity(Gravity.CENTER, 0, 0);
					inputInvalid.show();
					et_newGroupName.setText("");
				}
				return true;
			}
	    	
	    });
	    
	    miNewGPCancel.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub 
	            finish(); 
				return true;
			}
	    	
	    });
	    return true;
	}
	
	class AddGroupRequestAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(!params[0].isEmpty()){
				try {
					/*
					 * construct a http post request
					 */
					HttpPost httpPost = new HttpPost(url);
					HttpClient httpClient = new DefaultHttpClient();
					HttpEntity hEntity;
					
					hEntity = new StringEntity(params[0],"utf-8");
					//System.out.println("Test sending string(in new group async task):"+params[0]);
					httpPost.setEntity(hEntity);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					int result;
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						httpRespond = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8"); 
						JSONObject resultJSON = new JSONObject(httpRespond);
						result = resultJSON.getInt("result");
					}else{
						return Primitive.CONNECTIONREFUSED;
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					return result;
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
				Toast connectError = Toast.makeText(NewGroupActivity.this,
					     "Cannot connect to the server", Toast.LENGTH_LONG);
				connectError.setGravity(Gravity.CENTER, 0, 0);
				connectError.show();
				break;
			case Primitive.ACCEPT:
				Intent data=new Intent();  
				try {
					JSONObject resultJSON = new JSONObject(httpRespond);
					/*
					 * Use as a return value to fragment to refresh the listview
					 */
					data.putExtra("newCreateGroupId", resultJSON.getString("groupId"));
		            data.putExtra("newCreateGroupName", newGroupName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            setResult(1, data);   
	            finish(); 
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
    private void dataChanged() {
        // Notify listView
        mAdapter.notifyDataSetChanged();
    }

    public class GroupItemAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;  
        private Vector<UserInfo> mUsers = new Vector<UserInfo>();  
        private ListView mListView;  
        SyncImageLoader syncImageLoader;  
  
        public GroupItemAdapter(Context context, ListView listView) {  
            mInflater = LayoutInflater.from(context);  
            syncImageLoader = new SyncImageLoader();  
            mListView = listView;  
        }  
  
        public void addGroup(String userName, String image) {  
            UserInfo user = new UserInfo();  
            user.setUsername(userName);
            user.setImage(image);
            mUsers.add(user);  
        }  
  
        public void addUser(UserInfo user){
        	mUsers.add(user);
        }
        
        public void addUsers(ArrayList<UserInfo> users){
        	mUsers.clear();
        	for(int i = 0; i < users.size(); i++)
        		mUsers.add(users.get(i));
        }
        public void clean() {  
        	mUsers.clear();  
        }  
  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mUsers.size();  
        }  
  
        public Object getItem(int position) {  
            if (position >= getCount()) {  
                return null;  
            }  
            return mUsers.get(position);  
        }  
  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                convertView = mInflater.inflate(R.layout.new_group_listview_item,  
                        null);  
            }  
            UserInfo user = mUsers.get(position);  
            convertView.setTag(position);  
            ImageView ivUserProfile = (ImageView) convertView.findViewById(
            		R.id.iv_new_gp_user_profile);
            TextView tvUserName = (TextView)convertView.findViewById(
            		R.id.tv_new_gp_username);
            CheckBox cbIsChecked = (CheckBox)convertView.findViewById(
            		R.id.cb_new_gp_is_add);
            cbIsChecked.setChecked(isSelected.get(position));
            tvUserName.setText(user.getUsername());  
            if(!user.getImage().contentEquals("null")){
            	ivUserProfile.setBackgroundResource(R.drawable.no_photo_small);
            	syncImageLoader.loadImage(position,
            			Global.USERIMGURL+user.getImage(),  
                        imageLoadListener, user.getImage()); 
            	}else
            	{
            		ivUserProfile.setBackgroundResource(R.drawable.no_photo_small);
            	}
             
            return convertView;  
        }  
  
        SyncImageLoader.OnImageLoadListener imageLoadListener = 
        		new SyncImageLoader.OnImageLoadListener() {  
  
			public void onImageLoad(Integer t, Drawable drawable) {  
                // UserInfo model = (UserInfo) getItem(t);  
                View view = mListView.findViewWithTag(t);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_new_gp_user_profile);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
  
            public void onError(Integer t) {  
                UserInfo model = (UserInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_new_gp_user_profile);  
                    iv.setBackgroundResource(R.drawable.no_photo_small);  
                }  
            }  
  
        };  
  
        public void loadImage() {  
            int start = mListView.getFirstVisiblePosition();  
            int end = mListView.getLastVisiblePosition();  
            if (end >= getCount()) {  
                end = getCount() - 1;  
            }  
            syncImageLoader.setLoadLimit(start, end);  
            syncImageLoader.unlock();  
        }  
  
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {  
  
            public void onScrollStateChanged(AbsListView view, int scrollState) {  
                switch (scrollState) {  
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:  
                    syncImageLoader.lock();  
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
                    loadImage();  
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
                    syncImageLoader.lock();  
                    break;  
  
                default:  
                    break;  
                }  
  
            }  
  
            public void onScroll(AbsListView view, int firstVisibleItem,  
                    int visibleItemCount, int totalItemCount) {  
                // TODO Auto-generated method stub  
  
            }  
        };  
    }
    
}
