package com.example.schedule;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schedule.NewGroupActivity.AddGroupRequestAT;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailActivity extends Activity {
	private ImageView []profile=new ImageView[8];
	private ImageButton previous;
	private ImageButton next;
	SyncImageLoader syncImageLoader; 
	private String jsonStringFromFragment;
	private ArrayList<String> profileUrl = new ArrayList();
	private String userId;
	private String groupId;
	private static String url = Global.BASICURL+"MemberAddCheck";
	private String paraToNewFriendsToGroupActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		//Debug info
		//System.out.println(getIntent().getStringExtra("whichGroup"));
		//Change title to display the group info
		setTitle(getIntent().getStringExtra("groupNameToEventDetailActivity"));
		/*
		 * For use of add new members to this group
		 */
		userId = getIntent().getStringExtra("userIdToEventDetailActivity");
		groupId = getIntent().getStringExtra("groupIdToEventDetailActivity");
		//Get JSON data
		jsonStringFromFragment = getIntent().getStringExtra("paraToEventDetailActivity");
		//Find ImageView
		profile[1]=(ImageView) findViewById(R.id.profile1_event_detail);
		profile[2]=(ImageView) findViewById(R.id.profile2_event_detail);
		profile[3]=(ImageView) findViewById(R.id.profile3_event_detail);
		profile[4]=(ImageView) findViewById(R.id.profile4_event_detail);
		profile[5]=(ImageView) findViewById(R.id.profile5_event_detail);
		profile[6]=(ImageView) findViewById(R.id.profile6_event_detail);
		/*
		 * Set default profile
		 */
		profile[1].setBackgroundResource(R.drawable.no_photo_small);
		profile[2].setBackgroundResource(R.drawable.no_photo_small);
		profile[3].setBackgroundResource(R.drawable.no_photo_small);
		profile[4].setBackgroundResource(R.drawable.no_photo_small);
		profile[5].setBackgroundResource(R.drawable.no_photo_small);
		profile[6].setBackgroundResource(R.drawable.no_photo_small);
		/*
		 * Parse member image url
		 */
		try {
			JSONObject joOrigionString = new JSONObject(jsonStringFromFragment);
			JSONObject joMember = new JSONObject();
			JSONArray jaSocialArray = new JSONArray();
			jaSocialArray = joOrigionString.getJSONArray("socialArray");
			for(int i=0 ; i < jaSocialArray.length() ; i++){
				joMember = (JSONObject) jaSocialArray.get(i);
				profileUrl.add(joMember.getString("memberImage"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		/*
		 * Async task to load profile
		 */
		if(profileUrl.size() >= 6){
			for(int i=0 ; i < 6 ; i++){
				/*
				 * Attention please
				 * At here,activity's ImageView index starts with 1,manually
				 * so,profile[i+1]
				 */
				new FillUserProfileAT(profile[i+1]).execute(Global.USERIMGURL+profileUrl.get(i).toString());
			}
		}else{
			for(int i=0 ; i < profileUrl.size() ; i++){
				new FillUserProfileAT(profile[i+1]).execute(Global.USERIMGURL+profileUrl.get(i).toString());
			}
		}
		//For display the previous and next button
		previous = (ImageButton)findViewById(R.id.previous_event_detail);
		next = (ImageButton)findViewById(R.id.next_event_detail);
		//View
		DrawView dr = (DrawView)this.findViewById(R.id.drawView_event_detail);
		//Send JSON data to DrawView
		dr.setJSONData(jsonStringFromFragment);
		previous.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println("Previous touched");
				
			}
			
		});
		next.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println("Next touched");
			}
			
		});
	}
	/*
	 * Async task
	 */
	public class FillUserProfileAT extends AsyncTask<String ,Integer, Bitmap>{  
	      
	    private ImageView imageView;   
	      
	    public FillUserProfileAT( ImageView imageView) {  
	        super();  
	        this.imageView = imageView; 
	    }  
	    protected void onPreExecute() {  
	        // TODO Auto-generated method stub   
	    }  
	    protected Bitmap doInBackground(String... params) {
	        // TODO Auto-generated method stub   
	        Bitmap bitmap=null;  
	        try {  
	             URL url=new URL(params[0]);  
	             HttpURLConnection connection=(HttpURLConnection) url.openConnection();  
	             connection.setDoInput(true);  
	             connection.connect();  
	              
	             InputStream inputStream=connection.getInputStream();  
	             bitmap=BitmapFactory.decodeStream(inputStream);  
	             inputStream.close();  
	              
	        } catch (Exception e) {  
	            // TODO: handle exception   
	        }  
	        return bitmap;  
	    }  
	  
	    protected void onPostExecute(Bitmap result) {  
	        
	        super.onPostExecute(result);  
	        imageView.setImageBitmap(result);  
	    }  
	  
	} 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_event_detail, menu);
		MenuItem miNewFriendsToGroup = (MenuItem)menu.findItem(R.id.btn_new_friends_to_group);
		miNewFriendsToGroup.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				public boolean onMenuItemClick(MenuItem item) {
					// TODO Auto-generated method stub
					new MemberAddCheckAT().execute(userId,groupId);
					return true;
				}
		    });
		return true;
	}
	class MemberAddCheckAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
				if(!params[0].isEmpty()){
					
					try{
						HttpClient httpClient = new DefaultHttpClient();
						String query = URLEncoder.encode("userId", "utf-8");
						query += "=";
						query += URLEncoder.encode(params[0], "utf-8");
						query += "&";
						query += URLEncoder.encode("groupId", "utf-8");
						query += "=";
						query += URLEncoder.encode(params[1], "utf-8");	
						
						String urlParams = "?"+query;
						HttpGet httpget = new HttpGet(url+urlParams);
						HttpResponse httpResponse = httpClient.execute(httpget);
						int result;
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							paraToNewFriendsToGroupActivity = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8");  
							JSONObject resultJSON = new JSONObject(paraToNewFriendsToGroupActivity);
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
					Toast connectError = Toast.makeText(EventDetailActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					/*
					 * Go to NewFriendsActivity and pass the params
					 */
					Intent intent=new Intent();
		        	intent.setClass(EventDetailActivity.this,NewFriendsToGroupActivity.class );
		        	intent.putExtra("userIdToAddFriendsToGroup", userId);
		        	intent.putExtra("groupIdToAddFriendsToGroup", groupId);
		        	intent.putExtra("paraToNewFriendsToGroupActivity", paraToNewFriendsToGroupActivity);
		        	startActivity(intent);
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

}
