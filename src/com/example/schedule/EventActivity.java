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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends Activity {

	private static String userImageUrl = Global.USERIMGURL;
	private static String commentUpdateUrl = Global.BASICURL+"CommentUpdate";
	private int positionInEventArray;
	private String userId;
	private String publisherId;
	private String eventId;
	private String eventName;
	private String eventContent;
	private String commentsString;
	private int commentCount;
	private JSONArray commentsArray;
	private Calendar calFrom = Calendar.getInstance();
	private Calendar calTo = Calendar.getInstance();
	private String photo;
	private String record;
	private TextView tvFrom,tvTo,tvEventName,tvEventContent,tvCommentCount;
	private ImageButton imgBtnRecordPlay;
	private ImageView ivPhoto;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private int pausePosition;
	private File eventRecordPath;
	private File tempRecordFile;
	private File eventImagePath;
	private AlertDialog alertDialog;
	private ProgressDialog progressDialog;
	private CommentListView lvComments;
	private Button btnComment;
	private CommentItemAdapter ciAdapter;
	public EventActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent gainIntent = getIntent();
		userId = gainIntent.getStringExtra("userId");
		publisherId = gainIntent.getStringExtra("publisherId");
		eventId = gainIntent.getStringExtra("eventId");
		eventName = gainIntent.getStringExtra("eventName");
		eventContent = gainIntent.getStringExtra("eventContent");
		photo = gainIntent.getStringExtra("photo");
		record = gainIntent.getStringExtra("record");
		commentsString = gainIntent.getStringExtra("commentsString");
		positionInEventArray = gainIntent.getIntExtra("position", -1);
		try {
			commentsArray = new JSONArray(commentsString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		commentCount = gainIntent.getIntExtra("commentCount", 0);
		long calFromInMillis = gainIntent.getLongExtra("calFrom", 0);
		calFrom.setTimeInMillis(calFromInMillis);
		long calToInMillis = gainIntent.getLongExtra("calTo", 0);
		calTo.setTimeInMillis(calToInMillis);
		eventRecordPath = new File(Environment.getExternalStorageDirectory().getPath()+
				"/Schedule/tempEventRecord");
		this.tempRecordFile = new File(eventRecordPath, record);
		this.progressDialog = new ProgressDialog(this);
		this.setContentView(R.layout.activity_event);
		tvEventName = (TextView)findViewById(R.id.tv_event_event_name);
		tvEventContent = (TextView)findViewById(R.id.tv_event_event_content);
		tvFrom = (TextView)findViewById(R.id.tv_event_event_time_begin);
		tvTo = (TextView)findViewById(R.id.tv_event_event_time_end);
		imgBtnRecordPlay = (ImageButton)findViewById(R.id.btn_event_event_record_play);
		ivPhoto = (ImageView)findViewById(R.id.iv_event_event_photo);
		tvCommentCount = (TextView)findViewById(R.id.tv_event_event_commet_count);
		lvComments = (CommentListView)findViewById(R.id.lv_event_event_comment);
		ciAdapter = new CommentItemAdapter(this,lvComments);
		for(int i = 0; i < commentsArray.length(); i++){
			
			try {
				ciAdapter.addComment(getCommentInfoFromJSON(commentsArray.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		lvComments.setAdapter(ciAdapter);
		lvComments.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> parent, View itemView,
					int position, long id) {
				// TODO Auto-generated method stub
				
				final int positionInCommentArray = position;
				AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this); 
				// use a custom View defined in xml 
				View view = LayoutInflater.from(
						EventActivity.this).inflate(R.layout.alert_dialog_comment_handle, null); 
				final TextView editText = (TextView)view.findViewById(
						R.id.tv_alert_dialog_comment_handle); 
				editText.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						try {
							JSONObject comment = commentsArray.getJSONObject(positionInCommentArray);
							String commentId = comment.getString("_id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						alertDialog.cancel();
					}
					
				});
				builder.setView(view); 
				alertDialog = builder.create();
				alertDialog.show();
				return true;
			}			
		});
		btnComment = (Button)findViewById(R.id.btn_event_event_comment);
		btnComment.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this); 
				// use a custom View defined in xml 
				View view = LayoutInflater.from(
						EventActivity.this).inflate(R.layout.alert_dialog_comment, null); 
				final EditText editText = (EditText)view.findViewById(
						R.id.et_alert_dialog_comment_input); 
				Button btnSend = (Button)view.findViewById(R.id.btn_alert_dialog_comment_send);
				btnSend.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						// TODO Auto-generated method stub
						 String commentContent = editText.getText().toString(); 
						 if(commentContent.isEmpty()){
							 Toast ipPutEmpty = Toast.makeText(EventActivity.this,
								     "Please input your comment", Toast.LENGTH_LONG);
							 ipPutEmpty.setGravity(Gravity.CENTER, 0, 0);
							 ipPutEmpty.show();
						 }else{
							 Calendar publishTime = Calendar.getInstance();
							 alertDialog.cancel();
							 new CommentUpdateAT().execute(userId,eventId,
									 commentContent,publishTime.getTimeInMillis()+"",publisherId);
						 }
					}
					
				});
				builder.setView(view); 
				alertDialog = builder.create();
				Window window = alertDialog.getWindow();     
				window.setGravity(Gravity.BOTTOM); 
				window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				alertDialog.show();
			}
			
		});
		if(!record.contentEquals("null")){
			imgBtnRecordPlay.setVisibility(View.VISIBLE);
			imgBtnRecordPlay.setOnClickListener(new OnClickListener(){

				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String recordURL = Global.EVENTRECORDURL + record;
		    		File recordFile =new File(recordURL);
		    		if(recordFile.exists()){
		    			recordPlay();
		    		}else{
		    			new GetRecordAT().execute(recordURL);
		    		}
				}
				
			});
		}
		if(!photo.contentEquals("null")){
			eventImagePath = new File(Environment.getExternalStorageDirectory().getPath()+
					"/Schedule/images");
			File tempRecordFile = new File(eventImagePath, photo);
			Bitmap bitmap = BitmapFactory.decodeFile(tempRecordFile.toString());  
			ivPhoto.setImageBitmap(bitmap);
			
		}
		tvEventName.setText(eventName);
		tvEventContent.setText(eventContent);
		tvFrom.setText(dateFormat(calFrom.getTime()));
		tvTo.setText(dateFormat(calTo.getTime()));
		tvCommentCount.setText(
				getResources().getString(R.string.comments)+"("+commentCount+")");
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent data=new Intent();  
        data.putExtra("commentsArrayString",commentsArray.toString());
        data.putExtra("position", positionInEventArray);
        data.putExtra("commentCount",commentsArray.length());
        setResult(1, data);   
        finish(); 
	}
	
	private CommentInfo getCommentInfoFromJSON(JSONObject commentObject){
		CommentInfo comment = new CommentInfo();
		try {
			String commentId = commentObject.getString("_id");
			String commentContent = commentObject.getString("commentContent");
			String publisherImage = commentObject.getString("publisherImage");
			String publisherId = commentObject.getString("publisherId");
			String publishTimeString = commentObject.getString("publishTime");
			String publisherName = commentObject.getString("publisherName");
			Calendar publishTime = Calendar.getInstance();
			publishTime.setTimeInMillis(Long.parseLong(publishTimeString));
			comment.setCommentContent(commentContent);
			comment.setCommentId(commentId);
			comment.setPublisherId(publisherId);
			comment.setPublisherImage(publisherImage);
			comment.setPublisherName(publisherName);
			comment.setPublishTime(publishTime);
			return comment;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private String dateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"yyyy.MM.dd HH:mm");
        return dateFormat.format(date);
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
	
	public class CommentItemAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;  
        private Vector<CommentInfo> mComments = new Vector<CommentInfo>();  
        private ListView mListView;  
        SyncImageLoader syncImageLoader;  
  
        public CommentItemAdapter(Context context, ListView listView) {  
            mInflater = LayoutInflater.from(context);  
            syncImageLoader = new SyncImageLoader();  
            mListView = listView;            
            /* 
             * download while not scrolling
             * */  
            // mListView.setOnScrollListener(onScrollListener);  
        }  
  
  
        public void addComment(CommentInfo comment){
        	mComments.add(comment);
        }
        
        public void addComments(ArrayList<CommentInfo> comments){
        	mComments.clear();
        	for(int i = 0; i < comments.size(); i++)
        		mComments.add(comments.get(i));
        }
        public void clean() {  
            mComments.clear();  
        }  
  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return mComments.size();  
        }  
  
        public Object getItem(int position) {  
            if (position >= getCount()) {  
                return null;  
            }  
            return mComments.get(position);  
        }  
  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                convertView = mInflater.inflate(R.layout.list_item_event_comment,  
                        null);  
            }  
            final CommentInfo comment = mComments.get(position);  
            convertView.setTag(position);  
            ImageView ivPublisherImage = (ImageView) convertView.findViewById(
            		R.id.iv_comment_list_item_publisher_image);
            TextView tvPubliserName = (TextView)convertView.findViewById(
            		R.id.tv_comment_list_item_publisher_name);
            TextView tvPublishTime = (TextView)convertView.findViewById(
            		R.id.tv_comment_list_item_publish_time);
            TextView tvCommentContent = (TextView) convertView.findViewById(
            		R.id.tv_comment_list_item_content);  
            ImageButton iBtnCommentReply = (ImageButton)convertView.findViewById(
            		R.id.btn_comment_list_item_reply);
            if(!comment.getPublisherImage().contentEquals("null")){
            	ivPublisherImage.setBackgroundResource(R.drawable.no_photo_small);
            	syncImageLoader.loadImage(position, userImageUrl + comment.getPublisherImage(),  
                        imageLoadListener, comment.getPublisherImage()); 
            }else{
            	ivPublisherImage.setBackgroundResource(R.drawable.no_photo_small);
            }
            tvPubliserName.setText(comment.getPublisherName());
            tvPublishTime.setText(dateFormat(comment.getPublishTime().getTime()));
            tvCommentContent.setText(comment.getCommentContent());
            iBtnCommentReply.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this); 
					// use a custom View defined in xml 
					View view = LayoutInflater.from(
							EventActivity.this).inflate(R.layout.alert_dialog_comment, null); 
					final EditText editText = (EditText)view.findViewById(
							R.id.et_alert_dialog_comment_input); 
					Button btnSend = (Button)view.findViewById(R.id.btn_alert_dialog_comment_send);
					btnSend.setOnClickListener(new OnClickListener(){

						public void onClick(View v) {
							// TODO Auto-generated method stub
							 String commentContent = editText.getText().toString(); 
							 if(commentContent.isEmpty()){
								 Toast ipPutEmpty = Toast.makeText(EventActivity.this,
									     "Please input your comment", Toast.LENGTH_LONG);
								 ipPutEmpty.setGravity(Gravity.CENTER, 0, 0);
								 ipPutEmpty.show();
							 }else{
								 Calendar publishTime = Calendar.getInstance();
								 alertDialog.cancel();
								 new CommentUpdateAT().execute(userId,eventId,
										 commentContent,publishTime.getTimeInMillis()+"",
										 publisherId);
							 }
						}
						
					});
					builder.setView(view); 
					alertDialog = builder.create();
					Window window = alertDialog.getWindow();     
					window.setGravity(Gravity.BOTTOM); 
					window.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					alertDialog.show();
				}
            	
            });
            return convertView;  
        }  
  
        SyncImageLoader.OnImageLoadListener imageLoadListener = 
        		new SyncImageLoader.OnImageLoadListener() {  
  
			public void onImageLoad(Integer t, Drawable drawable) {  
                // EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(t);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_comment_list_item_publisher_image);  
                    iv.setBackgroundDrawable(drawable);  
                }  
            }  
  
            public void onError(Integer t) {  
                EventInfo model = (EventInfo) getItem(t);  
                View view = mListView.findViewWithTag(model);  
                if (view != null) {  
                    ImageView iv = (ImageView) view  
                            .findViewById(R.id.iv_comment_list_item_publisher_image);  
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
	
	class CommentUpdateAT extends AsyncTask<String,Integer,JSONObject>{

		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				HttpClient httpClient = new DefaultHttpClient();
				
					HttpPost httpPost = new HttpPost(commentUpdateUrl);
					JSONObject jsonEntity = new JSONObject();
					if (params.length > 1) {
						jsonEntity.put("userId", params[0]);
						jsonEntity.put("eventId",params[1]);
						jsonEntity.put("commentContent", params[2]);
						jsonEntity.put("publishTime", params[3]);
						jsonEntity.put("publisherId", params[4]);
						
					}else{
						jsonEntity.put("err", "error");
					}
					HttpEntity jsonStringEntity = 
							new StringEntity(jsonEntity.toString(),"UTF-8");				
				    httpPost.setEntity(jsonStringEntity);
				    HttpResponse httpResponse = httpClient.execute(httpPost);
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
			progressDialog.cancel();
			int resultCode;
			try {
				resultCode = result.getInt("result");
				switch(resultCode){
				case Primitive.CONNECTIONREFUSED:
					Toast connectError = Toast.makeText(EventActivity.this,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONObject commentObject = result.getJSONObject("comment");
					commentsArray.put(commentObject);
					commentCount++;
					tvCommentCount.setText(
							getResources().getString(R.string.comments)+"("+commentCount+")");
					ciAdapter.addComment(getCommentInfoFromJSON(commentObject));
					ciAdapter.notifyDataSetChanged();
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(EventActivity.this,
						     "Server database error", Toast.LENGTH_LONG);
					DBError.setGravity(Gravity.CENTER, 0, 0);
					DBError.show();
					break;
				case Primitive.FILEPARSEERROR:
					Toast fileParseError = Toast.makeText(EventActivity.this,
						     " Media file upload error", Toast.LENGTH_LONG);
					fileParseError.setGravity(Gravity.CENTER, 0, 0);
					fileParseError.show();
					break;	
				default:
					 
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			progressDialog.show();
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
			return downloadHelper.downFile(Global.EVENTRECORDURL+record, 
					"Schedule/tempEventRecord/",record);
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == -1){
				Toast Error = Toast.makeText(EventActivity.this,
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
