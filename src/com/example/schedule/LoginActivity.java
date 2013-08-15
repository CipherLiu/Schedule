package com.example.schedule;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static String eventCheckUrl = Global.BASICURL+"EventCheck";
	private static String groupCheckUrl = Global.BASICURL+"GroupCheck";
	private static String socialCheckUrl = Global.BASICURL+"SocialCheck";
	private ProgressDialog progressDialog;
	private boolean hasEventArray[] = new boolean[42];
	private EventInfo socialEventArray[] = new EventInfo[10];
	private ArrayList<GroupInfo> groupList = new ArrayList();
	private String groupListString;
	private Button btnLogin,btnRegister;
	private EditText etEmail,etPassword;
	private String email,password,userId;
	private static String loginUrl = Global.BASICURL+"Login";

	public LoginActivity() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		btnLogin = (Button)findViewById(R.id.btn_login);
		btnRegister = (Button)findViewById(R.id.btn_register);
		etEmail = (EditText)findViewById(R.id.et_login_email);
		etPassword = (EditText)findViewById(R.id.et_login_password);
		progressDialog = new ProgressDialog(this);
		btnRegister.setOnClickListener(new Button.OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				LoginActivity.this.startActivityForResult(intent,1);
			}
			
		});
		btnLogin.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				if(email.isEmpty()){
					Toast noUsername = Toast.makeText(LoginActivity.this,
						     "Please input your username", Toast.LENGTH_LONG);
					noUsername.setGravity(Gravity.CENTER, 0, 0);
					noUsername.show();
				}else if(password.isEmpty()){
					Toast noPassword = Toast.makeText(LoginActivity.this,
						     "Please input your password", Toast.LENGTH_LONG);
					noPassword.setGravity(Gravity.CENTER, 0, 0);
					noPassword.show();
//				}else if(email.contentEquals("liu")&&password.contentEquals("liu")){
//					Intent intent = new Intent();
//					intent.putExtra("email", "admin");
//					intent.putExtra("userId", "001");
//					boolean hasEventArray[] = new boolean[42];
//					intent.putExtra("hasEventArray", hasEventArray);
//					intent.setClass(LoginActivity.this, MainActivity.class);
//					LoginActivity.this.startActivity(intent);
				}else{
					new LoginAT().execute(email,password);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//etEmail.setText("");
		etPassword.setText("");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == 1){
			Bundle extras = data.getExtras();
			String emailAfterRegister= extras.getString("email");
			etEmail.setText(emailAfterRegister);
		}
	}

	public Calendar getStartDate(Calendar calStartDate , int iFirstDayOfWeek) {
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
	
	class LoginAT extends AsyncTask<String,Integer,JSONObject>{
		 
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}
		
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(loginUrl);
				JSONObject jsonEntity = new JSONObject();
				if (params.length > 1) {
					jsonEntity.put("username", params[0]);
					jsonEntity.put("password", params[1]);
				} else {
					jsonEntity.put("err", "error");
				}
				
				httpPost.setEntity(new StringEntity(jsonEntity.toString(),"utf-8"));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				JSONObject resultJSON = new JSONObject();
				if(httpResponse.getStatusLine().getStatusCode() == 200){
					String retSrc = EntityUtils.toString(httpResponse.getEntity()); 
					resultJSON = new JSONObject(retSrc);
					//result = resultJSON.getInt("result");
				}
				else{
					resultJSON.put("result", Primitive.CONNECTIONREFUSED);
					
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
				return resultJSON;
			}catch(HttpHostConnectException e){
				e.printStackTrace();
				JSONObject resultJSON = new JSONObject();
				try {
					resultJSON.put("result", Primitive.CONNECTIONREFUSED);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return resultJSON;
			}catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			int resultCode;
			try {
				resultCode = result.getInt("result");
			
				switch(resultCode){
				case Primitive.CONNECTIONREFUSED:
					progressDialog.cancel();
					Toast connectError = Toast.makeText(LoginActivity.this,
					     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					userId = result.getString("userId");
					Calendar dateToCheck = getStartDate(Calendar.getInstance(),Calendar.SUNDAY);
					String calString = dateToCheck.getTimeInMillis()+"";
					new EventCheckAT().execute(calString,result.getString("userId"));
					
					break;
				case Primitive.DBCONNECTIONERROR:
					progressDialog.cancel();
					Toast DBError = Toast.makeText(LoginActivity.this,
					     "Server database error", Toast.LENGTH_LONG);
					DBError.setGravity(Gravity.CENTER, 0, 0);
					DBError.show();
					break;
				case Primitive.USERUNREGISTERED:
					progressDialog.cancel();
					Toast userUnregistered = Toast.makeText(LoginActivity.this,
							"Unregistered user , please register first", Toast.LENGTH_LONG);
					userUnregistered.setGravity(Gravity.CENTER, 0, 0);
					userUnregistered.show();
					break;
				case Primitive.WRONGPASSWORD:
					progressDialog.cancel();
					Toast wrongPwd = Toast.makeText(LoginActivity.this,
					     "Wrong password , please input the correct password", Toast.LENGTH_LONG);
					wrongPwd.setGravity(Gravity.CENTER, 0, 0);
					wrongPwd.show();
					break;
				default:
					progressDialog.cancel();
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class EventCheckAT extends AsyncTask<String,Integer,JSONObject>{

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
					Toast connectError = Toast.makeText(LoginActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("hasEventArray");
					for(int i = 0 ; i < jArray.length() ; i++){
					hasEventArray[i] = jArray.getBoolean(i);
					}
					new GroupCheckAT().execute(userId);
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(LoginActivity.this,
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
	
	class GroupCheckAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(groupCheckUrl+"?userId="+params[0]);
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
					Toast connectError = Toast.makeText(LoginActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("groupList");
					groupListString = jArray.toString();
					for(int i = 0 ; i < jArray.length() ; i++){
						JSONObject jsonObject = (JSONObject)jArray.get(i);
						GroupInfo group = new GroupInfo();
						group.setId(jsonObject.getString("_id"));
						group.setName(jsonObject.getString("groupName"));
						groupList.add(group);
						
					}
					Calendar dateToCheck = getStartDate(Calendar.getInstance(),Calendar.SUNDAY);
					String calString = dateToCheck.getTimeInMillis()+"";
					new SocialCheckAT().execute(calString , userId);
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(LoginActivity.this,
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
	
	class SocialCheckAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(socialCheckUrl+"?dateTimeInMillis="+params[0] 
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
					Toast connectError = Toast.makeText(LoginActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("socialEventArray");
					for(int i = 0 ; i < jArray.length() ; i++){
						JSONObject eventObject = (JSONObject)jArray.get(i);
						socialEventArray[i] = getEventInfoFromJSON(eventObject);
					}
					Intent intent = new Intent();
					intent.putExtra("email",email);
					intent.putExtra("userId",userId);
					intent.putExtra("hasEventArray", hasEventArray);
					intent.putExtra("socialEventArrayString", jArray.toString());
					intent.putExtra("groupListString", groupListString);
					intent.setClass(LoginActivity.this, MainActivity.class);
					LoginActivity.this.startActivity(intent);
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(LoginActivity.this,
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
	
    public String getGroupListString(){
    	JSONArray jsonArray = new JSONArray();
    	for(int i = 0; i < groupList.size(); i++){
    		JSONObject jObject = new JSONObject();
    		try {
				jObject.put("id", groupList.get(i).getId());
				jObject.put("name", groupList.get(i).getName());
				jsonArray.put(jObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return jsonArray.toString();
    }
	
	private EventInfo getEventInfoFromJSON(JSONObject eventObject){
		EventInfo event = new EventInfo();
		try {
			long fromTimeMillis = eventObject.getLong("calFrom");
			long toTimeMillis = eventObject.getLong("calTo");
			Calendar calFrom = Calendar.getInstance();
			calFrom.setTimeInMillis(fromTimeMillis);
			Calendar calTo = Calendar.getInstance();
			calTo.setTimeInMillis(toTimeMillis);
			String eventId = eventObject.getString("_id");
			String eventName = eventObject.getString("eventName");
			String eventContent = eventObject.getString("decription");
			String locationName = eventObject.getString("locationName");
			String photo = eventObject.getString("photo");
			String record = eventObject.getString("record");
			event.setCalFrom(calFrom);
			event.setCalTo(calTo);
			event.setEventName(eventName);
			event.setEventId(eventId);
			event.setDescription(eventContent);
			event.setPhoto(photo);
			event.setRecord(record);
			event.setLocationName(locationName);
			return event;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
