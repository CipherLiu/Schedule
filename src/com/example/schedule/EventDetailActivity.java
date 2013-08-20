package com.example.schedule;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.LinearLayout;
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
	private String anotherDayJsonString;
	private ArrayList<String> paraListToDrawView = new ArrayList();
	private DrawView dr;
	private static int dayOffset = 0;
	private static String group_social_url = Global.BASICURL+"GroupSocial";
	private int currentDayOfYear;
	private Calendar currentCal;
	private int sixTupleIndex = 0;
	private int sixTupleNumber;
	private int memberNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		//Debug info
		
		//Change title to display the group info
		setTitle(getIntent().getStringExtra("groupNameToEventDetailActivity"));
		/*
		 * For use of add new members to this group
		 */
		userId = getIntent().getStringExtra("userIdToEventDetailActivity");
		groupId = getIntent().getStringExtra("groupIdToEventDetailActivity");
		//Get JSON data
		jsonStringFromFragment = getIntent().getStringExtra("paraToEventDetailActivity");
		paraListToDrawView.add(jsonStringFromFragment);
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
		memberNumber = profileUrl.size();
		sixTupleNumber = memberNumber/6;
		
		System.out.println("Member num is:"+memberNumber);
		System.out.println("sixTupleNumber num is:"+sixTupleNumber);
		System.out.println("JSON data is:"+jsonStringFromFragment);
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
			for(int i=profileUrl.size()+1 ; i <= 6 ; i++){
				profile[i].setBackgroundResource(0);
			}
		}
		//For display the previous and next button
		previous = (ImageButton)findViewById(R.id.previous_event_detail);
		next = (ImageButton)findViewById(R.id.next_event_detail);
		//Current day of year
		currentCal = Calendar.getInstance();
		currentDayOfYear = currentCal.get(Calendar.DAY_OF_YEAR);;
		//View
		dr = (DrawView)this.findViewById(R.id.drawView_event_detail);
		//Send JSON data to DrawView
		dr.setJSONData(jsonStringFromFragment);
		dr.setBaseDay(currentDayOfYear);
		previous.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				//Re-draw
				dayOffset--;
				if(dayOffset < 0){
					Toast alreadyToday = Toast.makeText(EventDetailActivity.this,
						     "咩，已经今天了啊！", Toast.LENGTH_LONG);
					alreadyToday.setGravity(Gravity.CENTER, 0, 0);
					alreadyToday.show();
					dayOffset=0;
				}else{
					dr.setSixTupleIndex(sixTupleIndex);
					dr.setJSONData(paraListToDrawView.get(dayOffset));
					dr.setBaseDay(currentDayOfYear+dayOffset);
					dr.postInvalidate();
				}
				*/
				if(memberNumber > 6){
					sixTupleIndex--;
					if(sixTupleIndex < 0){
						Toast tip = Toast.makeText(EventDetailActivity.this,
							     "咩，已经是前六个了啊！", Toast.LENGTH_LONG);
						tip.setGravity(Gravity.CENTER, 0, 0);
						tip.show();
						//Flag holder
						sixTupleIndex = 0;
					}
					else{
						//Clean up
						for(int i=1;i<=6;i++){
							profile[i].setBackgroundResource(R.drawable.no_photo_small);
							profile[i].setImageBitmap(null);
						}
						//New
						for(int i = sixTupleIndex*6 ; i<(sixTupleIndex*6+6) ; i++){
							if(!profileUrl.get(i).toString().equals("null")){
								new FillUserProfileAT(profile[(i+1)%6]).execute(Global.USERIMGURL+profileUrl.get(i).toString());
							}
						}
						dr.setSixTupleIndex(sixTupleIndex);
						dr.setJSONData(jsonStringFromFragment);
						dr.setBaseDay(currentDayOfYear);
						dr.postInvalidate();
					}
				}else{
					Toast tip = Toast.makeText(EventDetailActivity.this,
						     "咩，就这几个人！", Toast.LENGTH_LONG);
					tip.setGravity(Gravity.CENTER, 0, 0);
					tip.show();
					sixTupleIndex=0;
				}
			}
			
		});
		next.setOnClickListener(new ImageButton.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				dayOffset++;
				//Request new data
				if(dayOffset == paraListToDrawView.size()){
					Calendar requestCal = Calendar.getInstance();
					requestCal.set(Calendar.DAY_OF_YEAR, currentDayOfYear+dayOffset);
					
					requestCal.set(Calendar.HOUR_OF_DAY, 0);
					requestCal.set(Calendar.MINUTE, 0);
					requestCal.set(Calendar.SECOND, 0);
					requestCal.set(Calendar.MILLISECOND,0);
					
					System.out.println("Current day of year:"+currentDayOfYear);
					System.out.println("Days diff:"+(requestCal.get(Calendar.DAY_OF_YEAR)-currentDayOfYear));
					
					
					new GetDrawDataAT().execute(userId,groupId,String.valueOf(requestCal.getTimeInMillis()));
				}
				//Read from cached data
				else{
					dr.setSixTupleIndex(sixTupleIndex);
					dr.setJSONData(paraListToDrawView.get(dayOffset));
					dr.setBaseDay(currentDayOfYear+dayOffset);
					dr.postInvalidate();
				}
				*/
				sixTupleIndex++;
				if(memberNumber > 6 && sixTupleIndex<=sixTupleNumber){
					if(sixTupleIndex < sixTupleNumber){
						//Clean up
						for(int i=1;i<=6;i++){
							profile[i].setBackgroundResource(R.drawable.no_photo_small);
							profile[i].setImageBitmap(null);
						}
						//New profile
						for(int i = sixTupleIndex*6 ; i< (sixTupleIndex*6+6);i++){
							if(!profileUrl.get(i).toString().equals("null")){
								new FillUserProfileAT(profile[(i+1)%6]).execute(Global.USERIMGURL+profileUrl.get(i).toString());
							}
						}
						dr.setSixTupleIndex(sixTupleIndex);
						dr.postInvalidate();
					}else{
						for(int i=1;i<=6;i++){
							profile[i].setBackgroundResource(R.drawable.no_photo_small);
							profile[i].setImageBitmap(null);
						}
						for(int i = sixTupleIndex*6 ; i< (sixTupleIndex*6+memberNumber%6);i++){
							if(!profileUrl.get(i).toString().equals("null")){
								new FillUserProfileAT(profile[(i+1)%6]).execute(Global.USERIMGURL+profileUrl.get(i).toString());
							}
						}
						for(int i = memberNumber%6+1 ; i<=6;i++){
							profile[i].setBackgroundResource(0);
						}
						//String str="{\"result\":1,\"socialArray\":[{\"memberId\":\"5212c8a844b4ad93159d8223\",\"memberImage\":
						//\"UserImage201308200937545212c8a844b4ad93159d8223.jpg\",\"eventArray\":[]}]}";
						dr.setSixTupleIndex(sixTupleIndex);
						dr.setJSONData(jsonStringFromFragment);
						dr.setBaseDay(currentDayOfYear);
						dr.postInvalidate();
					}

				}else{
					sixTupleIndex--;
					Toast tip = Toast.makeText(EventDetailActivity.this,
						     "咩，就这几个人！", Toast.LENGTH_LONG);
					tip.setGravity(Gravity.CENTER, 0, 0);
					tip.show();
				}
			}
			
		});
	}
	/*
	 * Async task
	 */
	class GetDrawDataAT extends AsyncTask<String,Integer,Integer>{

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
						query += URLEncoder.encode("groupId","utf-8");
						query += "=";
						query += URLEncoder.encode(params[1],"utf-8");
						query += "&";
						query += URLEncoder.encode("dateTimeInMillis","utf-8");
						query += "=";
						query += URLEncoder.encode(params[2],"utf-8");
						
						String urlParams = "?"+query;
						
						HttpGet httpget = new HttpGet(group_social_url+urlParams);
						HttpResponse httpResponse = httpClient.execute(httpget);
						int result;
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							anotherDayJsonString = new String(EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8"); 
							JSONObject resultJSON = new JSONObject(anotherDayJsonString);
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
				//New add to paraListToDrawView
				paraListToDrawView.add(anotherDayJsonString);
				System.out.println("paraListToDrawView's size is:"+paraListToDrawView.size());
				//Re-draw
				System.out.println("Gotted new data is:"+anotherDayJsonString);
				dr.setJSONData(anotherDayJsonString);
				dr.setBaseDay(currentDayOfYear+dayOffset);
				dr.postInvalidate();
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
					 * Go to NewFriendsToGroupActivity and pass the params
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
