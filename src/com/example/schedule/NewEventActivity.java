package com.example.schedule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schedule.RecordButton.OnFinishedRecordListener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewEventActivity extends Activity{
	
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static String url = Global.BASICURL+"EventUpdate";
    private static String eventCheckUrl = Global.BASICURL+"EventCheck";
    private static String oneDatEventQueryUrl = Global.BASICURL+"OneDayEventQuery";
    private File eventImagePath = new File(Environment.getExternalStorageDirectory().getPath()+
			"/Schedule/EventImage");
    private File eventRecordPath = new File(Environment.getExternalStorageDirectory().getPath()+
			"/Schedule/EventRecord");
    private File tempImageFile = new File(eventImagePath, getImageFileName());
    private File tempRecordFile = new File(eventRecordPath, getRecordFileName());
	private Uri photoUri;
	private TextView dateTextView;
	private EditText etEventName,etDescription,etLocationName;
	private Button btnFromDate,btnToDate,btnFromTime,btnToTime;
	private ImageButton imgBtnLocation,imgBtnRecordPlay,imgBtnGallery,imgBtnPhoto;
	private RecordButton imgBtnRecord = null;
	private ImageView imgView;
	private MediaPlayer mediaPlayer = new MediaPlayer();;
	private EventInfo eventInfo = new EventInfo();
	private Calendar calFrom = Calendar.getInstance();
	private Calendar calTo = Calendar.getInstance();
//	private Calendar dateToCheck = Calendar.getInstance();
	private DatePickerDialog.OnDateSetListener fromDateListener,toDateListener;
	private TimePickerDialog.OnTimeSetListener fromTimeListener,toTimeListener;
	private SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");
	private int pausePosition = 0;
	private ProgressDialog progressDialog;
	private String userId;

	private Spinner groupSpinner;     
    private ArrayAdapter<String> spinnerAdapter;
    private String fromAvtivityName;
    private ArrayList<GroupInfo> groupList = new ArrayList();
	public NewEventActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_new_event);  
		dateTextView = (TextView)findViewById(R.id.tv_new_event_date);
		Intent gainIntent = getIntent();
		progressDialog = new ProgressDialog(this);
		int gainYear = gainIntent.getIntExtra("year",0);
		int gainMonth = gainIntent.getIntExtra("month",0);
		int gainDayOfMonth = gainIntent.getIntExtra("dayOfMonth",0);
		int gainHourOfDay = gainIntent.getIntExtra("hourOfDay",0);
		int gainMinute = gainIntent.getIntExtra("minute", 0);
		fromAvtivityName = gainIntent.getStringExtra("from");
//		userEmail = gainIntent.getStringExtra("userEmail");
		userId = gainIntent.getStringExtra("userId");
		String groupListString = gainIntent.getStringExtra("groupListString");
		
		try {
			JSONArray jsonArray = new JSONArray(groupListString);
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				GroupInfo group = new GroupInfo();
				group.setId(jsonObject.getString("id"));
				group.setName(jsonObject.getString("name"));
				groupList.add(group);
			}
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		eventInfo.setUserId(userId);
		etEventName = (EditText)findViewById(R.id.et_event_name);
		etDescription= (EditText)findViewById(R.id.et_description);
		etLocationName = (EditText)findViewById(R.id.et_location);
		if(!eventInfo.CalIsSet()){
			if(gainMinute > 29)
			{
				calFrom.set(gainYear, gainMonth, gainDayOfMonth, gainHourOfDay, 0);
				calFrom.roll(Calendar.HOUR_OF_DAY, true);
				calTo.set(gainYear, gainMonth, gainDayOfMonth, gainHourOfDay, 0);
				calTo.roll(Calendar.HOUR_OF_DAY, true);
				calTo.roll(Calendar.HOUR_OF_DAY, true);
			}
			else
			{
				calFrom.set(gainYear, gainMonth, gainDayOfMonth, gainHourOfDay, 30);
				calTo.set(gainYear, gainMonth, gainDayOfMonth, gainHourOfDay, 30);
				calTo.roll(Calendar.HOUR_OF_DAY, true);
			}
			calFrom.set(Calendar.SECOND, 0);
			calFrom.set(Calendar.MILLISECOND, 0);
			calTo.set(Calendar.SECOND, 0);
			calTo.set(Calendar.MILLISECOND, 0);
			eventInfo.setCalFrom(calFrom);
			eventInfo.setCalTo(calTo);		
		}
		dateTextView.setText(dfDate.format(calFrom.getTime()));
		   
		List<String> groupNameList = new ArrayList<String>(); 
		for(int i = 0; i < groupList.size(); i++){
			groupNameList.add(groupList.get(i).getName());
		}
        groupSpinner = (Spinner)findViewById(R.id.sp_group);     
        spinnerAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_spinner_item, groupNameList);    
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);          
        groupSpinner.setAdapter(spinnerAdapter);
        for(int i = 0; i < groupList.size(); i++){
			if(groupNameList.get(i).contentEquals("default")){
				groupSpinner.setSelection(i);}
			
		} 
        groupSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){      
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {     
                // TODO Auto-generated method stub        
                arg0.setVisibility(View.VISIBLE); 
                eventInfo.setTargetGroup(groupList.get(arg2).getId());
            }      
            public void onNothingSelected(AdapterView arg0) {     
                // TODO Auto-generated method stub        
                arg0.setVisibility(View.VISIBLE);     
            }     
        });     
        groupSpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            v.setVisibility(View.VISIBLE);     
        }     
        });  
		
		btnFromDate = (Button)findViewById(R.id.btn_from_date);
		btnToDate = (Button)findViewById(R.id.btn_to_date);
		btnFromTime = (Button)findViewById(R.id.btn_from_time);
		btnToTime = (Button)findViewById(R.id.btn_to_time);
		btnFromDate.setText(dfDate.format(calFrom.getTime()));
		btnToDate.setText(dfDate.format(calTo.getTime()));
		btnFromTime.setText(dfTime.format(calFrom.getTime()));
		btnToTime.setText(dfTime.format(calTo.getTime()));
		try{
			eventImagePath.mkdirs();
			eventRecordPath.mkdirs();
	       }catch(Exception e){
	    	   Toast buildPathError = Toast.makeText(NewEventActivity.this,
					     "Error occured when build file path", Toast.LENGTH_LONG);
	    	   buildPathError.setGravity(Gravity.CENTER, 0, 0);
	    	   buildPathError.show();
	       }  
		fromDateListener= new DatePickerDialog.OnDateSetListener() {       
		       
		       public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		    	   long delta = calTo.getTimeInMillis() - calFrom.getTimeInMillis();
		    	   
		    	   calFrom.set(Calendar.YEAR, year);
		           calFrom.set(Calendar.MONTH, monthOfYear);
		           calFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		           calTo.setTimeInMillis(calFrom.getTimeInMillis() + delta);
		           btnFromDate.setText(dfDate.format(calFrom.getTime()));
		           btnToDate.setText(dfDate.format(calTo.getTime()));
		           //btnToTime.setText(dfTime.format(calTo.getTime())); 
		           calFrom.set(Calendar.SECOND, 0);
		           calFrom.set(Calendar.MILLISECOND, 0);
		           calTo.set(Calendar.SECOND, 0);
		           calTo.set(Calendar.MILLISECOND, 0);
		           eventInfo.setCalFrom(calFrom);
		           eventInfo.setCalTo(calTo);
		       }
		   };
		toDateListener= new DatePickerDialog.OnDateSetListener() {
		       
		       public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		           calTo.set(Calendar.YEAR, year);
		           calTo.set(Calendar.MONTH, monthOfYear);
		           calTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		           if(calTo.before(calFrom))
		           {
		        	   calTo.setTimeInMillis(calFrom.getTimeInMillis());
		           }
		           //btnFromDate.setText(dfDate.format(calFrom.getTime()));
		           btnToDate.setText(dfDate.format(calTo.getTime()));
		           //btnFromTime.setText(dfTime.format(calFrom.getTime()));
		           btnToTime.setText(dfTime.format(calTo.getTime()));
		           calFrom.set(Calendar.SECOND, 0);
		           calFrom.set(Calendar.MILLISECOND, 0);
		           calTo.set(Calendar.SECOND, 0);
		           calTo.set(Calendar.MILLISECOND, 0);
		           eventInfo.setCalFrom(calFrom);
		           eventInfo.setCalTo(calTo);
		       }
		   };
		fromTimeListener = new TimePickerDialog.OnTimeSetListener(){
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				long delta = calTo.getTimeInMillis() - calFrom.getTimeInMillis();
				calFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
		        calFrom.set(Calendar.MINUTE, minute);
		        calTo.setTimeInMillis(calFrom.getTimeInMillis() + delta);
		        btnFromTime.setText(dfTime.format(calFrom.getTime()));
		        btnToDate.setText(dfDate.format(calTo.getTime()));
		        btnToTime.setText(dfTime.format(calTo.getTime()));
		        calFrom.set(Calendar.SECOND, 0);
				calFrom.set(Calendar.MILLISECOND, 0);
				calTo.set(Calendar.SECOND, 0);
				calTo.set(Calendar.MILLISECOND, 0);
		        eventInfo.setCalFrom(calFrom);
		        eventInfo.setCalTo(calTo);
		        }
   		   };
		toTimeListener = new TimePickerDialog.OnTimeSetListener(){
				
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					// TODO Auto-generated method stub
					calTo.set(Calendar.HOUR_OF_DAY, hourOfDay);
			        calTo.set(Calendar.MINUTE, minute);
			        if(calTo.before(calFrom))
			        {
			        	calTo.setTimeInMillis(calFrom.getTimeInMillis());
			        }
			        btnToDate.setText(dfDate.format(calTo.getTime()));
			        btnToTime.setText(dfTime.format(calTo.getTime()));
			        calFrom.set(Calendar.SECOND, 0);
					calFrom.set(Calendar.MILLISECOND, 0);
					calTo.set(Calendar.SECOND, 0);
					calTo.set(Calendar.MILLISECOND, 0);
			        eventInfo.setCalFrom(calFrom);
			        eventInfo.setCalTo(calTo);
				}  
			   };
		btnFromDate.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(NewEventActivity.this,fromDateListener,
		                calFrom.get(Calendar.YEAR),
		                calFrom.get(Calendar.MONTH),
		                calFrom.get(Calendar.DAY_OF_MONTH)
		                ).show();
			}
		});
		btnToDate.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new DatePickerDialog(NewEventActivity.this,toDateListener,
		                calTo.get(Calendar.YEAR),
		                calTo.get(Calendar.MONTH),
		                calTo.get(Calendar.DAY_OF_MONTH)
		                ).show();
			}
		});
		btnFromTime.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(NewEventActivity.this,fromTimeListener,
						calFrom.get(Calendar.HOUR_OF_DAY),
						calFrom.get(Calendar.MINUTE), true).show();
			}
		});
		btnToTime.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(NewEventActivity.this,toTimeListener,
						calTo.get(Calendar.HOUR_OF_DAY),
						calTo.get(Calendar.MINUTE), true).show();
			}
			
		});
		imgBtnPhoto = (ImageButton)findViewById(R.id.btn_camera);
		imgBtnPhoto.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempImageFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
			}
			
		});
		imgBtnGallery = (ImageButton)findViewById(R.id.btn_gallery);
		imgBtnGallery.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK, null); 
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
                		IMAGE_UNSPECIFIED);  
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                
			}
			
		});
		imgView = (ImageView)findViewById(R.id.iv_photo);
		imgView.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!eventInfo.getPhoto().isEmpty()){
			    	Intent intent = new Intent(Intent.ACTION_VIEW); 
			    	intent.setDataAndType(photoUri, IMAGE_UNSPECIFIED); 
			    	startActivity(intent);
				}else{
					Intent intent = new Intent(Intent.ACTION_PICK, null);  
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
							IMAGE_UNSPECIFIED);  
					startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
				}
			}
			
		});
		
		imgBtnRecord = (RecordButton)findViewById(R.id.btn_record);
		imgBtnRecord.setSavePath(tempRecordFile.toString());
		imgBtnRecord.setOnFinishedRecordListener(new OnFinishedRecordListener() {

			public void onFinishedRecord(String audioPath) {
				if(tempRecordFile.exists()){
					imgBtnRecordPlay.setEnabled(true);
					eventInfo.setRecord(tempRecordFile.getName());
				}else{
					imgBtnRecordPlay.setEnabled(false);
				}
			}
		});
		imgBtnRecordPlay = (ImageButton)findViewById(R.id.btn_record_play);
		imgBtnRecordPlay.setEnabled(false);
		imgBtnRecordPlay.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mediaPlayer.isPlaying()){
					mediaPlayer.pause();
					pausePosition = mediaPlayer.getCurrentPosition();
					imgBtnRecord.setEnabled(true);
					imgBtnRecordPlay.setImageResource(R.drawable.record_play);
				}else{
					imgBtnRecord.setEnabled(false);
					if(pausePosition == 0){
						try {
							mediaPlayer = new MediaPlayer();
							mediaPlayer.setDataSource(tempRecordFile.getAbsolutePath());
							mediaPlayer.prepare();
							mediaPlayer.start();
							mediaPlayer.setOnCompletionListener(new OnCompletionListener(){

								public void onCompletion(MediaPlayer mp) {
									// TODO Auto-generated method stub
									imgBtnRecord.setEnabled(true);
									imgBtnRecordPlay.setImageResource(R.drawable.record_play);
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						try {
							mediaPlayer = new MediaPlayer();
							mediaPlayer.setDataSource(tempRecordFile.getAbsolutePath());
							mediaPlayer.prepare();
							mediaPlayer.seekTo(pausePosition);
							mediaPlayer.start();
							mediaPlayer.setOnCompletionListener(new OnCompletionListener(){

								public void onCompletion(MediaPlayer mp) {
									// TODO Auto-generated method stub
									imgBtnRecord.setEnabled(true);
									imgBtnRecordPlay.setImageResource(R.drawable.record_play);
									pausePosition = 0;
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					imgBtnRecordPlay.setImageResource(R.drawable.pause);
				}
			}
		});

	}
	
	public Calendar getStartDate(Calendar calFrom , int iFirstDayOfWeek) {
		Calendar calStartDate = Calendar.getInstance();
		calStartDate.setTimeInMillis(calFrom.getTimeInMillis());
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		calStartDate.set(Calendar.MILLISECOND, 0);
		return calStartDate;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();     
	    inflater.inflate(R.menu.activity_new_event, menu);
	    MenuItem miSendNewEvent = (MenuItem)menu.findItem(R.id.menu_send_new_event);
	    miSendNewEvent.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				eventInfo.setEventName(etEventName.getText().toString());
				eventInfo.setDescription(etDescription.getText().toString());
				eventInfo.setLocationName(etLocationName.getText().toString());
				if(eventInfo.getEventName().isEmpty()){
					Toast noEventName = Toast.makeText(NewEventActivity.this,
						     "Please input event name", Toast.LENGTH_LONG);
					noEventName.setGravity(Gravity.CENTER, 0, 0);
					noEventName.show();
				}else if(eventInfo.getDescription().isEmpty()){
					eventInfo.setDescription(eventInfo.getEventName());
				}else if(eventInfo.getLocationName().isEmpty()){
					Toast noLocationName = Toast.makeText(NewEventActivity.this,
						     "Please input the location name", Toast.LENGTH_LONG);
					noLocationName.setGravity(Gravity.CENTER, 0, 0);
					noLocationName.show();
				}else{
					
					//------------Remember to Modify------------
					eventInfo.setLocationCoordinate(eventInfo.getLocationName());
					//------------Remember to Modify------------
					
					calFrom.set(Calendar.SECOND, 0);
					calFrom.set(Calendar.MILLISECOND, 0);
					calTo.set(Calendar.SECOND, 0);
					calTo.set(Calendar.MILLISECOND, 0);
					eventInfo.setCalFrom(calFrom);
					eventInfo.setCalTo(calTo);
					eventInfo.setUpdateTime(Calendar.getInstance().getTimeInMillis()+"");
					new EventUpdateAT().execute(eventInfo.getUserId(),eventInfo.getEventName(),
							eventInfo.getCalFrom().getTimeInMillis()+"",
							eventInfo.getCalTo().getTimeInMillis()+"",
							eventInfo.getLocationName(),eventInfo.getLocationCoordinate(),
							eventInfo.getDescription(),eventInfo.getUpdateTime(),
							eventInfo.getTargetGroup(),
							eventInfo.getPhoto(),eventInfo.getRecord());
				}
				return true;
			}
	    	
	    });
	    return true;
	}

	class EventUpdateAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				HttpClient httpClient = new DefaultHttpClient();
				if(!params[9].isEmpty() || !params[10].isEmpty()){
					HttpPost httpPost = new HttpPost(url);
					JSONObject jsonEntity = new JSONObject();
					if (params.length > 1) {
						jsonEntity.put("userId", params[0]);
						jsonEntity.put("eventName", params[1]);
						jsonEntity.put("calFrom", params[2]);
						jsonEntity.put("calTo", params[3]);
						jsonEntity.put("locationName", params[4]);
						jsonEntity.put("locationCoordinate", params[5]);
						jsonEntity.put("decription", params[6]);
						jsonEntity.put("updateTime", params[7]);
						jsonEntity.put("targetGroup", params[8]);
						jsonEntity.put("photo", params[9]);
						jsonEntity.put("record", params[10]);
						
					}else{
						jsonEntity.put("err", "error");
					}
					
					MultipartEntity multipartEntity  = new MultipartEntity( );
					if(!params[9].isEmpty()){
						ContentBody imageFile;
						imageFile = new FileBody(tempImageFile);
						multipartEntity.addPart("imageFile", imageFile);
					}
					if(!params[10].isEmpty()){
						ContentBody recordFile;
						recordFile = new FileBody(tempRecordFile);
						multipartEntity.addPart("recordFile", recordFile);
					}
				    ContentBody cbMessage = 
				    		new StringBody(jsonEntity.toString(),Charset.forName("UTF-8")); ;
				    multipartEntity.addPart("jsonString", cbMessage);
				    httpPost.setEntity(multipartEntity);
				    HttpResponse httpResponse = httpClient.execute(httpPost);
					int result;
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						String retSrc = EntityUtils.toString(httpResponse.getEntity()); 
						JSONObject resultJSON = new JSONObject(retSrc);
						result = resultJSON.getInt("result");
					}else{
						return Primitive.CONNECTIONREFUSED;
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					return result;
				}else{
					String query = URLEncoder.encode("userId", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[0], "utf-8");
					query += "&";
					query += URLEncoder.encode("eventName", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[1], "utf-8");
					query += "&";
					query += URLEncoder.encode("calFrom", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[2], "utf-8");
					query += "&";
					query += URLEncoder.encode("calTo", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[3], "utf-8");
					query += "&";
					query += URLEncoder.encode("locationName", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[4], "utf-8");
					query += "&";
					query += URLEncoder.encode("locationCoordinate", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[5], "utf-8");
					query += "&";
					query += URLEncoder.encode("decription", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[6], "utf-8");
					query += "&";
					query += URLEncoder.encode("updateTime", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[7], "utf-8");
					query += "&";
					query += URLEncoder.encode("targetGroup", "utf-8");
					query += "=";
					query += URLEncoder.encode(params[8], "utf-8");
					
					String urlParams = "?"+query;
					HttpGet httpget = new HttpGet(url+urlParams);
					HttpResponse httpResponse = httpClient.execute(httpget);
					int result;
					if(httpResponse.getStatusLine().getStatusCode() == 200){
						String retSrc = EntityUtils.toString(httpResponse.getEntity()); 
						JSONObject resultJSON = new JSONObject(retSrc);
						result = resultJSON.getInt("result");
					}else{
						return Primitive.CONNECTIONREFUSED;
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					return result;
				}
			}catch(HttpHostConnectException e){
				e.printStackTrace();
				return Primitive.CONNECTIONREFUSED;
			}catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.cancel();
			switch(result){
			case Primitive.CONNECTIONREFUSED:
				Toast connectError = Toast.makeText(NewEventActivity.this,
					     "Cannot connect to the server", Toast.LENGTH_LONG);
				connectError.setGravity(Gravity.CENTER, 0, 0);
				connectError.show();
				break;
			case Primitive.ACCEPT:
				
				if(fromAvtivityName.contentEquals("MainActivity")){
					Calendar dateToCheck = getStartDate(calFrom,Calendar.SUNDAY);
					String calString = dateToCheck.getTimeInMillis()+"";
					new eventCheckAT().execute(calString,userId);
				}else if(fromAvtivityName.contentEquals("DateActivity")){
					Calendar calToQuery = Calendar.getInstance();
					calToQuery.setTimeInMillis(getIntent().getLongExtra("calendar", 0));
					calToQuery.set(Calendar.HOUR_OF_DAY, 0);
					calToQuery.set(Calendar.MINUTE, 0);
					calToQuery.set(Calendar.SECOND, 0);
					calToQuery.set(Calendar.MILLISECOND,0);
					String calString = calToQuery.getTimeInMillis()+"";
					new OneDayEventQueryAT().execute(calString,userId);
				}
				break;
			case Primitive.DBCONNECTIONERROR:
				Toast DBError = Toast.makeText(NewEventActivity.this,
					     "Server database error", Toast.LENGTH_LONG);
				DBError.setGravity(Gravity.CENTER, 0, 0);
				DBError.show();
				break;
			case Primitive.FILEPARSEERROR:
				Toast fileParseError = Toast.makeText(NewEventActivity.this,
					     " Media file upload error", Toast.LENGTH_LONG);
				fileParseError.setGravity(Gravity.CENTER, 0, 0);
				fileParseError.show();
				break;	
			default:
				 
				break;
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog.show();
		}
	}
	
	class eventCheckAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(eventCheckUrl+"?dateTimeInMillis="+params[0] 
					+"&userId="+params[1]);
			try {
				HttpResponse httpResponse = httpClient.execute(httpget);
				JSONObject resultJSON = new JSONObject();
				if(httpResponse.getStatusLine().getStatusCode() == 200){
					String retSrc = EntityUtils.toString(httpResponse.getEntity()); 
					resultJSON = new JSONObject(retSrc);
				}else{
					resultJSON.put("result", Primitive.CONNECTIONREFUSED);
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
				return resultJSON;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.cancel();
			int resultCode;
			try {
				resultCode = result.getInt("result");
				
				switch(resultCode){
				case Primitive.CONNECTIONREFUSED:
					Toast connectError = Toast.makeText(NewEventActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("hasEventArray");
					boolean hasEventArray[] = new boolean[42];
					for(int i = 0 ; i < jArray.length() ; i++){
					hasEventArray[i] = jArray.getBoolean(i);
					}
					Intent data=new Intent();  
		            data.putExtra("hasEventArray",hasEventArray);
		            data.putExtra("calSelected", calFrom);
		            setResult(1, data);   
		            finish(); 
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(NewEventActivity.this,
						     "Server database error", Toast.LENGTH_LONG);
					DBError.setGravity(Gravity.CENTER, 0, 0);
					DBError.show();
					break;	
				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressDialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();
		}
		
	}
	
	class OneDayEventQueryAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(oneDatEventQueryUrl+"?dateTimeInMillis="+params[0] 
					+"&userId="+params[1]);
			try {
				HttpResponse httpResponse = httpClient.execute(httpget);
				JSONObject resultJSON = new JSONObject();
				if(httpResponse.getStatusLine().getStatusCode() == 200){
					String retSrc = new String(
							EntityUtils.toByteArray(httpResponse.getEntity()),"UTF-8");
					resultJSON = new JSONObject(retSrc);

				}else{
					resultJSON.put("result", Primitive.CONNECTIONREFUSED);
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
				return resultJSON;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.cancel();
			int resultCode;
			try {
				resultCode = result.getInt("result");
				
				switch(resultCode){
				case Primitive.CONNECTIONREFUSED:
					Toast connectError = Toast.makeText(NewEventActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("eventArray");
					Intent data=new Intent();  
		            data.putExtra("eventArray",jArray.toString());
		            setResult(1, data);   
		            finish(); 
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(NewEventActivity.this,
						     "Server database error", Toast.LENGTH_LONG);
					DBError.setGravity(Gravity.CENTER, 0, 0);
					DBError.show();
					break;	
				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			progressDialog.cancel();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
        	if(tempImageFile.exists()){
        		photoUri = Uri.fromFile(tempImageFile);
		        BitmapFactory.Options options = new BitmapFactory.Options();  
		        options.inJustDecodeBounds = true;  

		        Bitmap bitmap = BitmapFactory.decodeFile(tempImageFile.getAbsolutePath(),options);
		        options.inJustDecodeBounds =false;  
  
		        int heightRate = (int)(options.outHeight / (float)200);
		        int widthRate = (int)(options.outWidth/ (float)200); 
		        int be = heightRate > widthRate ? widthRate : heightRate; 
		        if(be <= 0)  
		            be =1;  
		        options.inSampleSize =be;  
 
		        bitmap = BitmapFactory.decodeFile(tempImageFile.getAbsolutePath(),options);   
		        imgView.setImageBitmap(bitmap);
		        imgView.invalidate();
		        try {  
		        	FileOutputStream out = new FileOutputStream(tempImageFile);  
		        	if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){  
		                out.flush();  
		                out.close(); 
		                eventInfo.setPhoto(tempImageFile.getName());
		            }
		        } catch (Exception e) {  
		            e.printStackTrace(); 
		        }  
        		
        	}
            break;
        case PHOTO_REQUEST_GALLERY:
            if (data != null){
            	photoUri = data.getData();
        		ContentResolver cr = NewEventActivity.this.getContentResolver(); 
        		Cursor cursor = cr.query(photoUri, null, null, null, null);  
		        cursor.moveToFirst();
				 
		        BitmapFactory.Options options = new BitmapFactory.Options();  
		        options.inJustDecodeBounds = true;   
		        Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(1),options);
		        options.inJustDecodeBounds =false;   
		        int heightRate = (int)(options.outHeight / (float)200);
		        int widthRate = (int)(options.outWidth/ (float)200); 
		        int be = heightRate > widthRate ? widthRate : heightRate;   
		        if(be <= 0)  
		            be =1;  
		        options.inSampleSize =be;   
		        bitmap = BitmapFactory.decodeFile(cursor.getString(1),options);   
		        imgView.setImageBitmap(bitmap);
		        imgView.invalidate();
		        try {  
		        	FileOutputStream out = new FileOutputStream(tempImageFile);  
		        	if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){  
		                out.flush();  
		                out.close();
		                eventInfo.setPhoto(tempImageFile.getName());
		            }
		        } catch (Exception e) {  
		            e.printStackTrace(); 
		        } 
            }
            break;
        }     
	}
	
	private String calendarToString(Calendar calendar){
		Date date = new Date(calendar.getTimeInMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
		return dateFormat.format(date);
	}
	
	private String getImageFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"'EventImage'yyyyMMddHHmmss");
        return dateFormat.format(date) + ".jpg";
    }
	private String getRecordFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"'EventRecord'yyyyMMddHHmmss");
        return dateFormat.format(date) + ".amr";
    }
}
