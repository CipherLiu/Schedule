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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
    //Number of selected
    private int checkNum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
        lv_new_gp = (ListView) findViewById(R.id.lv_new_group);
        mAdapter = new GroupItemAdapter(this, lv_new_gp);
        et_newGroupName = (EditText) findViewById(R.id.et_new_group_name);
        /*
         * Parse data here
         */
        UserInfo user = new UserInfo();
        user.setUsername("erbi");
        user.setImage("null");
        user.setUserId("daerbi");
        friends.add(user); 
        
        for(int i=0 ; i < friends.size();i++){
        	mAdapter.addUser(friends.get(i));
        }
        lv_new_gp.setAdapter(mAdapter);
        lv_new_gp.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
            	CheckBox cb = (CheckBox)arg0.findViewById(R.id.cb_new_gp_is_add);
            	
            	cb.toggle();
            	if(cb.isChecked()){
            		members.add(friends.get(arg2).getUserId());
            	}else{
            		
            		for(int i = 0; i<members.size(); i++){
            			if(members.get(i).contentEquals(friends.get(i).getUserId())){
            				members.remove(i);
            			}
            		}
            	}
                
            }
        });
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
				Intent data=new Intent();  
	            data.putExtra("newCreateGroupId", "Test ID");
	            data.putExtra("newCreateGroupName", "New Added Group");
	            setResult(1, data);   
	            finish(); 
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
            tvUserName.setText(user.getUsername());  
            if(user.getImage().contentEquals("null")){
            	ivUserProfile.setBackgroundResource(R.drawable.no_photo_small);
            	syncImageLoader.loadImage(position,
            			"http://192.168.1.103/userimg/UserImage_20130614_110059.jpg",  
                        imageLoadListener, user.getImage()); 
            	//syncImageLoader.loadImage(position, eventImageUrl + event.getPhoto(),  
                //        imageLoadListener, event.getPhoto()); 
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
