package com.example.schedule;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EventDetailActivity extends Activity {
	private ImageView []profile=new ImageView[8];
	private ImageButton previous;
	private ImageButton next;
	SyncImageLoader syncImageLoader; 
	private String jsonStringFromFragment;
	private ArrayList<String> profileUrl = new ArrayList();
	private String userId;
	private String groupId;
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
		return true;
	}

}
