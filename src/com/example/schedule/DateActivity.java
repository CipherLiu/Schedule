package com.example.schedule;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DateActivity extends Activity {

	private static String eventCheckUrl = Global.BASICURL+"EventCheck";
	private static String eventImageUrl = Global.EVENTIMGURL;
	private TextView dateTextView;
	private ListView dateListView;
	private JSONArray eventArray;
	private Calendar calendar =  Calendar.getInstance();
	private String userId;
	private ProgressDialog progressDialog;
	private String groupListString;
	public DateActivity(){
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_date);
		Intent gainIntent = getIntent(); 
		String date =gainIntent.getIntExtra("year",1990) + "-" 
				+ gainIntent.getIntExtra("month",5) + "-" 
				+ gainIntent.getIntExtra("dayOfMonth",10);
		calendar.setTimeInMillis(gainIntent.getLongExtra("calendar", 0));
		userId = gainIntent.getStringExtra("userId");
		groupListString = gainIntent.getStringExtra("groupListString");
		try {
			eventArray =  new JSONArray(gainIntent.getStringExtra("eventArray"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressDialog = new ProgressDialog(this);
		dateTextView = (TextView)findViewById(R.id.tv_date);
		dateTextView.setText(date);
		dateListView = (ListView)findViewById(R.id.lv_date);
		dateListView.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				JSONObject eventObject;
				try {
					eventObject = (JSONObject)eventArray.get(position);
					String eventId = eventObject.getString("_id");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		dateListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				JSONObject eventObject;
				try {
					eventObject = (JSONObject)eventArray.get(position);
					String eventId = eventObject.getString("_id");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
			
		});
		EventItemAdapter eventAdapter = new EventItemAdapter(this,dateListView);
		for(int i = 0 ; i < eventArray.length() ; i++){
    		try {
				JSONObject eventObject = (JSONObject)eventArray.get(i);
				eventAdapter.addEvent(getEventInfoFromJSON(eventObject));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		dateListView.setAdapter(eventAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();     
	    inflater.inflate(R.menu.activity_date, menu);
	    MenuItem miNewEvent = (MenuItem)menu.findItem(R.id.menu_new_event_date);
	    miNewEvent.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent newEventIntent = new Intent();
				newEventIntent.putExtra("year", calendar.get(Calendar.YEAR));
				newEventIntent.putExtra("month", calendar.get(Calendar.MONTH));
				newEventIntent.putExtra("dayOfMonth", calendar.get(Calendar.DAY_OF_MONTH));
//				newEventIntent.putExtra("hourOfDay", calSelected.get(Calendar.HOUR_OF_DAY));
//				newEventIntent.putExtra("minute", calSelected.get(Calendar.MINUTE));
				newEventIntent.putExtra("hourOfDay", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				newEventIntent.putExtra("minute", Calendar.getInstance().get(Calendar.MINUTE));
				newEventIntent.putExtra("userId", userId);
				newEventIntent.putExtra("from", "DateActivity");
				newEventIntent.putExtra("groupListString",groupListString);
				newEventIntent.putExtra("calendar",calendar.getTimeInMillis());
				newEventIntent.setClass(DateActivity.this, NewEventActivity.class);
				startActivityForResult(newEventIntent , 1);
				return true;
			}
	    });
	    return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 1 && resultCode == 1){
			Bundle extras = data.getExtras();
			try {
				eventArray = new JSONArray(extras.getString("eventArray"));
				EventItemAdapter eventAdapter = new EventItemAdapter(this,dateListView);
				for(int i = 0 ; i < eventArray.length() ; i++){
		    		try {
						JSONObject eventObject = (JSONObject)eventArray.get(i);
						eventAdapter.addEvent(getEventInfoFromJSON(eventObject));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
				dateListView.setAdapter(eventAdapter);
				eventAdapter.notifyDataSetChanged();				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Calendar dateToCheck = getStartDate(calendar,Calendar.SUNDAY);
		String calString = dateToCheck.getTimeInMillis()+"";
		new eventCheckAT().execute(calString,userId);
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
	private String dateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"yyyy.MM.dd HH:mm");
        return dateFormat.format(date);
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
					Toast connectError = Toast.makeText(DateActivity.this,
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
		            data.putExtra("calSelected", calendar);
		            setResult(1, data);   
		            finish(); 
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(DateActivity.this,
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
	
	public class EventItemAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;  
        private Vector<EventInfo> mEvents = new Vector<EventInfo>();  
        private ListView mListView;  
        SyncImageLoader syncImageLoader;  
  
        public EventItemAdapter(Context context, ListView listView) {  
            mInflater = LayoutInflater.from(context);  
            syncImageLoader = new SyncImageLoader();  
            mListView = listView;  
              
            /* 
             * 
             * 这一句话取消掉注释的话，那么能更加的节省资源，不过体验稍微有点， 
             * 你滑动的时候不会读取图片，当手放开后才开始度图片速度更快，你们可以试一试 
             * */  
              
            // mListView.setOnScrollListener(onScrollListener);  
        }  
  
        public void addEvent(String eventName, String eventContent, 
        		Calendar calFrom , Calendar calTo , String locationName,
        		String record,String photo) {  
            EventInfo event = new EventInfo();  
            event.setEventName(eventName);
            event.setDescription(eventContent);
            event.setCalFrom(calFrom);
            event.setCalTo(calTo);
            event.setLocationName(locationName);
            event.setRecord(record);
            event.setPhoto(photo);
            mEvents.add(event);  
        }  
  
        public void addEvent(EventInfo event){
        	mEvents.add(event);
        }
        
        public void addEvents(ArrayList<EventInfo> events){
        	mEvents.clear();
        	for(int i = 0; i < events.size(); i++)
        		mEvents.add(events.get(i));
        }
        public void clean() {  
            mEvents.clear();  
        }  
  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mEvents.size();  
        }  
  
        public Object getItem(int position) {  
            if (position >= getCount()) {  
                return null;  
            }  
            return mEvents.get(position);  
        }  
  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                convertView = mInflater.inflate(R.layout.list_item_date,  
                        null);  
            }  
            EventInfo event = mEvents.get(position);  
            convertView.setTag(position);  
            ImageView ivEventPhoto = (ImageView) convertView.findViewById(
            		R.id.iv_date_list_item_event_photo);
            TextView tvTimeBegin = (TextView)convertView.findViewById(
            		R.id.tv_date_list_item_time_begin);
            TextView tvTimeEnd = (TextView)convertView.findViewById(
            		R.id.tv_date_list_item_time_end);
            TextView tvEventName = (TextView) convertView.findViewById(
            		R.id.tv_date_list_item_event_name);  
            TextView tvEventContent = (TextView) convertView.findViewById(
            		R.id.tv_date_list_item_event_content);  
            tvEventName.setText(event.getEventName());  
            tvEventContent.setText(event.getDescription());
            tvTimeBegin.setText(dateFormat(event.getCalFrom().getTime()));
            tvTimeEnd.setText(dateFormat(event.getCalTo().getTime()));
            if(!event.getPhoto().contentEquals("null")){
            	ivEventPhoto.setBackgroundResource(R.drawable.no_event_photo);
            	syncImageLoader.loadImage(position, eventImageUrl + event.getPhoto(),  
                        imageLoadListener, event.getPhoto()); 
            	} 
             
            return convertView;  
        }  
  
        SyncImageLoader.OnImageLoadListener imageLoadListener = 
        		new SyncImageLoader.OnImageLoadListener() {  
  
			public void onImageLoad(Integer t, Drawable drawable) {  
                // EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(t);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_date_list_item_event_photo);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
  
            public void onError(Integer t) {  
                EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_date_list_item_event_photo);  
                    iv.setBackgroundResource(R.drawable.no_event_photo);  
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
