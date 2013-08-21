package com.example.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SocialFragment extends Fragment {

	private static String eventImageUrl = Global.EVENTIMGURL;
	private static String userImageUrl = Global.USERIMGURL;
	private ListView lvSocial;
	private JSONArray socialEventArray;
	private String userId;
	private EventItemAdapter socialEventItemAdapter;
	private View root;
	private LayoutInflater mInflater;
	public SocialFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userId = getArguments().getString("userId");
		String socialEventArrayString = getArguments().getString("socialEventArrayString");
		try {
			socialEventArray = new JSONArray(socialEventArrayString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {       	

//		return inflater.inflate(R.layout.fragment_social, container, false);
		LinearLayout lay = new LinearLayout(this.getActivity());
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setBackgroundResource(R.drawable.page_bg);
		lvSocial = new ListView(this.getActivity());
		lvSocial.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		socialEventItemAdapter = new EventItemAdapter(this.getActivity(),lvSocial);
		socialEventItemAdapter.addEventFromJSONArray(socialEventArray);
		lvSocial.setAdapter(socialEventItemAdapter);
		lvSocial.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent toEventActivity = new Intent();
				JSONObject eventObject;
				try {
					eventObject = (JSONObject)socialEventArray.get(position);
					EventInfo selectedEvent = getEventInfoFromJSON(eventObject);
					toEventActivity.putExtra("eventId", selectedEvent.getEventId());
					toEventActivity.putExtra("calFrom", selectedEvent.getCalFrom().getTimeInMillis());
					toEventActivity.putExtra("calTo", selectedEvent.getCalTo().getTimeInMillis());
					toEventActivity.putExtra("eventName", selectedEvent.getEventName());
					toEventActivity.putExtra("eventContent", selectedEvent.getDescription());
					toEventActivity.putExtra("photo", selectedEvent.getPhoto());
					toEventActivity.putExtra("record", selectedEvent.getRecord());
					toEventActivity.putExtra("userId", userId);
					toEventActivity.putExtra("commentCount", selectedEvent.getCommentCount());
					toEventActivity.putExtra("commentsString", selectedEvent.getCommentsString());					
					toEventActivity.putExtra("publisherId", selectedEvent.getUserId());
					toEventActivity.putExtra("position", position);
					toEventActivity.setClass(SocialFragment.this.getActivity(), EventActivity.class);
					SocialFragment.this.startActivityForResult(toEventActivity , 2);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		lay.addView(lvSocial);
		return lay;
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 2 && resultCode == 1){
			Bundle extras = data.getExtras();
			String commentsArrayString = extras.getString("commentsArrayString");
			int position = extras.getInt("position");
			int commentCount = extras.getInt("commentCount");
			try {
				JSONArray commentsArray = new JSONArray(commentsArrayString);
				JSONArray eventArrayNew = new JSONArray();
				for(int i = 0; i < socialEventArray.length(); i++){
					if(i==position){
						JSONObject eventJSONObject = new JSONObject();
						eventJSONObject.put("_id", socialEventArray.getJSONObject(i).get("_id"));
						eventJSONObject.put("eventName", 
								socialEventArray.getJSONObject(i).get("eventName"));
						long calFrom = (Long)socialEventArray.getJSONObject(i).get("calFrom");
						eventJSONObject.put("calFrom", calFrom);
						long calTo = (Long)socialEventArray.getJSONObject(i).get("calTo");
						eventJSONObject.put("calTo", calTo);
						eventJSONObject.put("locationName", 
								socialEventArray.getJSONObject(i).get("locationName"));
						eventJSONObject.put("locationCoordinate", 
								socialEventArray.getJSONObject(i).get("locationCoordinate"));
						eventJSONObject.put("decription", 
								socialEventArray.getJSONObject(i).get("decription"));
						eventJSONObject.put("photo", 
								socialEventArray.getJSONObject(i).get("photo"));
						eventJSONObject.put("record", 
								socialEventArray.getJSONObject(i).get("record"));
						//increase the commentCount
						eventJSONObject.put("commentCount", commentCount);
						eventJSONObject.put("updateTime", 
								socialEventArray.getJSONObject(i).get("updateTime"));
						eventJSONObject.put("publisherId", 
								socialEventArray.getJSONObject(i).get("publisherId"));
						eventJSONObject.put("publisherName", 
								socialEventArray.getJSONObject(i).get("publisherName"));	
						eventJSONObject.put("publisherImage", 
								socialEventArray.getJSONObject(i).get("publisherImage"));
						//update the commentArrayString 
						eventJSONObject.put("commentsString", commentsArray.toString());
						eventArrayNew.put(eventJSONObject);
					}else{
						eventArrayNew.put(socialEventArray.get(i));
					}
				}
				socialEventItemAdapter.addEventFromJSONArray(eventArrayNew);
				socialEventItemAdapter.notifyDataSetChanged();
				this.getView().invalidate();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String dateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"yyyy.MM.dd HH:mm");
        return dateFormat.format(date);
    }
	private String titleDateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"yyyy-MM-dd");
        return dateFormat.format(date);
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
 			String locationCoordinate = eventObject.getString("locationCoordinate");
 			String photo = eventObject.getString("photo");
 			String record = eventObject.getString("record");
 			int commentCount = eventObject.getInt("commentCount");
 			String commentsString = eventObject.getString("commentsString");
 			String pulisherId = eventObject.getString("publisherId");
 			String publisherImage = eventObject.getString("publisherImage");
 			String updateTime = eventObject.getString("updateTime");
 			String publisherName = eventObject.getString("publisherName");
 			event.setCalFrom(calFrom);
 			event.setCalTo(calTo);
 			event.setEventName(eventName);
 			event.setEventId(eventId);
 			event.setDescription(eventContent);
 			event.setPhoto(photo);
 			event.setRecord(record);
 			event.setLocationName(locationName);
 			event.setCommentCount(commentCount);
 			event.setCommentsString(commentsString);
 			event.setUserId(pulisherId);
 			event.setUserIamge(publisherImage);
 			event.setUpdateTime(updateTime);
 			event.setUserName(publisherName);
 			event.setLocationCoordinate(locationCoordinate);
 			return event;
 		} catch (JSONException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		return null;
 	}
	
	public class EventItemAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;  
        private Vector<EventInfo> mEvents = new Vector<EventInfo>();  
        private ListView mListView;  
        SyncImageLoader syncImageLoader;  
        SyncImageLoader syncPublisherImageLoader;
  
        public EventItemAdapter(Context context, ListView listView) {  
            mInflater = LayoutInflater.from(context);  
            syncImageLoader = new SyncImageLoader();  
            syncPublisherImageLoader = new SyncImageLoader();  
            mListView = listView;            
            /* 
             * download while not scrolling
             * */  
            // mListView.setOnScrollListener(onScrollListener);  
        }  
  
        public void addEvent(String eventName, String eventContent, 
        		Calendar calFrom , Calendar calTo , String locationName,
        		String record,String photo,int commentCount, String commentsString) {  
            EventInfo event = new EventInfo();  
            event.setEventName(eventName);
            event.setDescription(eventContent);
            event.setCalFrom(calFrom);
            event.setCalTo(calTo);
            event.setLocationName(locationName);
            event.setRecord(record);
            event.setPhoto(photo);
            event.setCommentCount(commentCount);
            event.setCommentsString(commentsString);
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
        public void addEventFromJSONArray(JSONArray eventsJSONArray){
        	mEvents.clear();
        	for(int i = 0; i < eventsJSONArray.length(); i++)
				try {
					addEvent(getEventInfoFromJSON(eventsJSONArray.getJSONObject(i)));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
    			int commentCount = eventObject.getInt("commentCount");
    			String commentsString = eventObject.getString("commentsString");
    			String pulisherId = eventObject.getString("publisherId");
    			String publisherImage = eventObject.getString("publisherImage");
    			String updateTime = eventObject.getString("updateTime");
    			String publisherName = eventObject.getString("publisherName");
    			event.setCalFrom(calFrom);
    			event.setCalTo(calTo);
    			event.setEventName(eventName);
    			event.setEventId(eventId);
    			event.setDescription(eventContent);
    			event.setPhoto(photo);
    			event.setRecord(record);
    			event.setLocationName(locationName);
    			event.setCommentCount(commentCount);
    			event.setCommentsString(commentsString);
    			event.setUserId(pulisherId);
    			event.setUserIamge(publisherImage);
    			event.setUpdateTime(updateTime);
    			event.setUserName(publisherName);
    			return event;
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return null;
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
                convertView = mInflater.inflate(R.layout.list_item_social,  
                        null);  
            } 
            final int positionInArray = position;
            final EventInfo selectedEvent = mEvents.get(position);  
            convertView.setTag(position);  
            ImageView ivEventPhoto = (ImageView) convertView.findViewById(
            		R.id.iv_social_list_item_event_photo);
            TextView tvTimeBegin = (TextView)convertView.findViewById(
            		R.id.tv_social_list_item_time_begin);
            TextView tvTimeEnd = (TextView)convertView.findViewById(
            		R.id.tv_social_list_item_time_end);
            TextView tvEventName = (TextView) convertView.findViewById(
            		R.id.tv_social_list_item_event_name);  
            TextView tvEventContent = (TextView) convertView.findViewById(
            		R.id.tv_social_list_item_event_content); 
            ImageButton iBtnRecordPaly = (ImageButton)convertView.findViewById(
            		R.id.btn_social_list_item_event_record_play);
            Button commentsButton = (Button)convertView.findViewById(
            		R.id.btn_social_list_item_event_comment);
            ImageView ivPubliserImage = (ImageView)convertView.findViewById(
            		R.id.iv_social_list_item_publisher_image);
            TextView tvPublisherName = (TextView)convertView.findViewById(
            		R.id.tv_social_list_item_publisher_name);
            TextView tvPublishTime = (TextView)convertView.findViewById(
            		R.id.tv_social_list_item_publish_time);
            commentsButton.setText(SocialFragment.this.getResources().
            		getString(R.string.comments)+"("+selectedEvent.getCommentCount()+")");
            commentsButton.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent toEventActivity = new Intent();
					toEventActivity.putExtra("eventId", selectedEvent.getEventId());
					toEventActivity.putExtra("calFrom", selectedEvent.getCalFrom().getTimeInMillis());
					toEventActivity.putExtra("calTo", selectedEvent.getCalTo().getTimeInMillis());
					toEventActivity.putExtra("eventName", selectedEvent.getEventName());
					toEventActivity.putExtra("eventContent", selectedEvent.getDescription());
					toEventActivity.putExtra("photo", selectedEvent.getPhoto());
					toEventActivity.putExtra("record", selectedEvent.getRecord());
					toEventActivity.putExtra("userId", userId);
					toEventActivity.putExtra("commentCount", selectedEvent.getCommentCount());
					toEventActivity.putExtra("commentsString", selectedEvent.getCommentsString());
					
					toEventActivity.putExtra("publisherId", selectedEvent.getUserId());
					toEventActivity.putExtra("position", positionInArray);
					toEventActivity.setClass(SocialFragment.this.getActivity(), EventActivity.class);
					SocialFragment.this.startActivityForResult(toEventActivity , 2);
				}
            	
            });
            if(!selectedEvent.getRecord().contentEquals("null")){
            	iBtnRecordPaly.setVisibility(View.VISIBLE);
            	RecordPlayClickListener listener = 
            			new RecordPlayClickListener(selectedEvent.getRecord());
            	iBtnRecordPaly.setOnClickListener(listener);
            } 
            tvEventName.setText(selectedEvent.getEventName());  
            tvEventContent.setText(selectedEvent.getDescription());
            tvTimeBegin.setText(dateFormat(selectedEvent.getCalFrom().getTime()));
            tvTimeEnd.setText(dateFormat(selectedEvent.getCalTo().getTime()));
            tvPublisherName.setText(selectedEvent.getUserName());
            String publishTimeLong = selectedEvent.getUpdateTime();
            Calendar updateTime =  Calendar.getInstance();
            updateTime.setTimeInMillis(Long.parseLong(publishTimeLong));
            tvPublishTime.setText(dateFormat(updateTime.getTime()));
            if(!selectedEvent.getPhoto().contentEquals("null")){
            	ivEventPhoto.setBackgroundResource(R.drawable.no_event_photo);
            	syncImageLoader.loadImage(position, eventImageUrl + selectedEvent.getPhoto(),  
                        imageLoadListener, selectedEvent.getPhoto()); 
            } 
            if(!selectedEvent.getUserIamge().contentEquals("null")){
            	ivPubliserImage.setBackgroundResource(R.drawable.no_photo_small);
            	syncPublisherImageLoader.loadImage(position, 
            			userImageUrl+selectedEvent.getUserIamge(), userImageLoadListener, 
            			selectedEvent.getUserIamge());
            }else{
            	ivPubliserImage.setBackgroundResource(R.drawable.no_photo_small);
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
                            .findViewById(R.id.iv_social_list_item_event_photo);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
			
  
            public void onError(Integer t) {  
                EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_social_list_item_event_photo);  
                    iv.setBackgroundResource(R.drawable.no_event_photo);  
                }  
            }  
  
        };  
        SyncImageLoader.OnImageLoadListener userImageLoadListener = 
        		new SyncImageLoader.OnImageLoadListener() {  
  
			public void onImageLoad(Integer t, Drawable drawable) {  
                // EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(t);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_social_list_item_publisher_image);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
			
  
            public void onError(Integer t) {  
                EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_social_list_item_publisher_image);  
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
            syncPublisherImageLoader.setLoadLimit(start, end);
            syncPublisherImageLoader.unlock(); 
        }  
  
        AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {  
  
            public void onScrollStateChanged(AbsListView view, int scrollState) {  
                switch (scrollState) {  
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:  
                    syncImageLoader.lock(); 
                    syncPublisherImageLoader.lock();
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
                    loadImage();  
                    break;  
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
                    syncImageLoader.lock();  
                    syncPublisherImageLoader.lock();
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
        
        class RecordPlayClickListener implements OnClickListener{

    		private String fileName;
    		private ImageButton imgBtnRecordPlay;
    		private int pausePosition;
    		private File eventRecordPath;
    		private File tempRecordFile;
    		private MediaPlayer mediaPlayer = new MediaPlayer();
    		RecordPlayClickListener(String fileName){
    			this.fileName = fileName;
    			this.eventRecordPath = new File(Environment.getExternalStorageDirectory().getPath()+
    					"/Schedule/tempEventRecord");
    			try{
    				eventRecordPath.mkdirs();
    		       }catch(Exception e){
    		    	   Toast buildPathError = Toast.makeText(SocialFragment.this.getActivity(),
    						     "Error occured when build file path", Toast.LENGTH_LONG);
    		    	   buildPathError.setGravity(Gravity.CENTER, 0, 0);
    		    	   buildPathError.show();
    		       } 
    			this.tempRecordFile = new File(eventRecordPath, fileName);
    		}
    		
    		public class DownloadHelper {
    			private URL url = null;			
    			public String download(String newUrl){
    				StringBuffer sb = new StringBuffer();
    				String line = null;
    				BufferedReader br = null;				
    				try {
    					url = new URL(newUrl);
    					HttpURLConnection urlConn  = (HttpURLConnection)url.openConnection();
    					br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
    					while((line = br.readLine())!=null){
    						sb.append(line);
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				} finally{
    					try{
    						br.close();
    					}catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				return sb.toString();
    			}			
    			//error return -1 , success return 0 , file exist return 1
    			public int downFile(String urlStr,String path,String fileName){
    				try {
    					InputStream is = null;
    					FileUtils fileUtils = new FileUtils();
    					if(fileUtils.existSDFile(path+fileName)){
    						return 1;
    					}else{
    						is = getInputStreamFromUrl(urlStr);
    						File resultFile = fileUtils.write2SDCARDFromInputSteam(path, fileName, is);
    						if(resultFile==null)return -1;
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    				return 0;
    			}			
    			public InputStream getInputStreamFromUrl(String newUrl){
    				URL url = null;
    				HttpURLConnection httpURLConnection = null;
    				InputStream is = null;
    				try {
    					url = new URL(newUrl);
    					httpURLConnection = (HttpURLConnection)url.openConnection();
    					is = httpURLConnection.getInputStream();
    					
    				} catch (MalformedURLException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    				return is;
    			}
    		}
    		
    		public class FileUtils {
    			private String SDCARD = null;
    			public FileUtils() {
    				SDCARD = Environment.getExternalStorageDirectory() + "/";
    			}
    			public File createSDDir(String dirName) {
    				File fileDir = new File(SDCARD + dirName);
    				fileDir.mkdir();
    				return fileDir;
    			}
    			public File createSDFile(String fileName) throws IOException {
    				File file = new File(SDCARD+fileName); 
    				file.createNewFile();
    				return file;
    			}
    			public boolean existSDFile(String fileName) {
    				File file = new File(SDCARD + fileName);
    				return file.exists();
    			}
    			public File write2SDCARDFromInputSteam(String path, String fileName,
    					InputStream is) {
    				File file = null;
    				OutputStream os = null;
    				try {
    					createSDDir(path);
    					file = createSDFile(path + fileName);
    					os = new FileOutputStream(file);
    					byte[] buffer = new byte[4 * 1024];
    					while (is.read(buffer) != -1) {
    						os.write(buffer);
    					}
    					os.flush();
    				} catch (FileNotFoundException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				} finally {
    					try {
    						os.close();
    						is.close();
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    				}
    				return file;
    			}
    		}
    		
    		public void onClick(View v) { 		
    			// TODO Auto-generated method stub
    			imgBtnRecordPlay = (ImageButton)v;
        		String recordURL = Global.EVENTRECORDURL + fileName;
        		File recordFile =new File(recordURL);
        		if(recordFile.exists()){
        			recordPlay();
        		}else{
        			new GetRecordAT().execute(recordURL);
        		}
    		}
    		class GetRecordAT extends AsyncTask<String,Integer,Integer>{
    			 
    			@Override
    			protected void onPreExecute() {
    			}
    			
    			@Override
    			protected Integer doInBackground(String... params) {
    				// TODO Auto-generated method stub
    				DownloadHelper downloadHelper = new DownloadHelper();
    				return downloadHelper.downFile(Global.EVENTRECORDURL+fileName, 
    						"Schedule/tempEventRecord/",fileName);
    			}

    			@Override
    			protected void onPostExecute(Integer result) {
    				// TODO Auto-generated method stub
    				super.onPostExecute(result);
    				if(result == -1){
    					Toast Error = Toast.makeText(SocialFragment.this.getActivity(),
    						     "Error occured when get the record", Toast.LENGTH_LONG);
    		    	  Error.setGravity(Gravity.CENTER, 0, 0);
    		    	  Error.show();
    				}else if(result == 0 || result == 1){
    					recordPlay();
    				}
    				
    			}
    		}
    		private void recordPlay(){
    			
        		if(mediaPlayer.isPlaying()){
    				mediaPlayer.pause();
    				pausePosition = mediaPlayer.getCurrentPosition();
    				imgBtnRecordPlay.setImageResource(R.drawable.record_play);
    			}else{
    				if(pausePosition == 0){
    					try {
    						mediaPlayer = new MediaPlayer();
    						mediaPlayer.setDataSource(tempRecordFile.getAbsolutePath());
    						mediaPlayer.prepare();
    						mediaPlayer.start();
    						mediaPlayer.setOnCompletionListener(new OnCompletionListener(){

    							public void onCompletion(MediaPlayer mp) {
    								// TODO Auto-generated method stub
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
    	}
    } 
}
