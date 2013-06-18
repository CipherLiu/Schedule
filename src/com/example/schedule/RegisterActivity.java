package com.example.schedule;

//import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Environment; 

public class RegisterActivity extends Activity {

	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static String url = Global.BASICURL+"Register";
    private File userImagePath = new File(Environment.getExternalStorageDirectory().getPath()+
    											"/Schedule/UserImage");
    private File tempFile = new File(userImagePath, getPhotoFileName());
	private EditText etEmail,etPassword,etPasswordConfirm,etUsername;
	//private Button btnRegisterOK;
	private ImageButton btnCamera,btnGallery;
	private ImageView ivUser;
	private ProgressDialog progressDialog;
	private String email,password,passwordConfirm,username;
	private Boolean imageChanged = false;
	private UserInfo userInfo = new UserInfo();
	public RegisterActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);
		etEmail = (EditText)findViewById(R.id.et_register_email);
		etPassword = (EditText)findViewById(R.id.et_register_password);
		etPasswordConfirm = (EditText)findViewById(R.id.et_register_password_confirm);
		etUsername = (EditText)findViewById(R.id.et_register_username);
		btnCamera = (ImageButton)findViewById(R.id.btn_register_camera);
		btnGallery = (ImageButton)findViewById(R.id.btn_register_gallery);
		//btnRegisterOK = (Button)findViewById(R.id.btn_register_ok);
		ivUser = (ImageView)findViewById(R.id.iv_register_user_image);
		if(!ivUser.isDrawingCacheEnabled())
			ivUser.setDrawingCacheEnabled(true);
		progressDialog = new ProgressDialog(this);
		try{
			userImagePath.mkdirs();
	       }catch(Exception e){
	    	   Toast buildPathError = Toast.makeText(RegisterActivity.this,
					     "Error occured when build file path", Toast.LENGTH_LONG);
	    	   buildPathError.setGravity(Gravity.CENTER, 0, 0);
	    	   buildPathError.show();
	       }  
		btnCamera.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                imageChanged = true;
			}			
		});
		btnGallery.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK, null);  
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);  
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                imageChanged = true;
			}			
		});
//		btnRegisterOK.setOnClickListener(new OnClickListener(){
//
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				email = etEmail.getText().toString();
//				password = etPassword.getText().toString();
//				passwordConfirm = etPasswordConfirm.getText().toString();
//				username = etUsername.getText().toString();
//				if(email.isEmpty()){
//					Toast noEmial = Toast.makeText(RegisterActivity.this,
//						     "Please input your e-mail", Toast.LENGTH_LONG);
//					noEmial.setGravity(Gravity.CENTER, 0, 0);
//					noEmial.show();
//				}else if(password.isEmpty()){
//					Toast noPassword = Toast.makeText(RegisterActivity.this,
//						     "Please input your password", Toast.LENGTH_LONG);
//					noPassword.setGravity(Gravity.CENTER, 0, 0);
//					noPassword.show();
//				}else if(passwordConfirm.isEmpty()){
//					Toast noPasswordConfirm = Toast.makeText(RegisterActivity.this,
//						     "Please confirm your password", Toast.LENGTH_LONG);
//					noPasswordConfirm.setGravity(Gravity.CENTER, 0, 0);
//					noPasswordConfirm.show();
//				}else{
//					if(password.compareTo(passwordConfirm) == 0){
//						userInfo.setEmail(email);
//						userInfo.setPassword(password);
//						if(username.isEmpty())
//							username = email;
//						userInfo.setUsername(username);
//						if(imageChanged){
//				            userInfo.setImage(tempFile.getName());
//						}else{
//							userInfo.setImage("none");
//						}
//						new RegisterAT().execute(userInfo.getEmail(),userInfo.getPassword(),
//								userInfo.getUsername(),userInfo.getImage());
//						 	
//					}else{
//						Toast PasswordConfirmError = Toast.makeText(RegisterActivity.this,
//							     "Password confirm failure", Toast.LENGTH_LONG);
//						PasswordConfirmError.setGravity(Gravity.CENTER, 0, 0);
//						PasswordConfirmError.show();
//					}
//				}
//			}		
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();     
	    inflater.inflate(R.menu.activity_register, menu);
	    MenuItem miRegOk = (MenuItem)menu.findItem(R.id.btn_register_ok);
	    miRegOk.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				passwordConfirm = etPasswordConfirm.getText().toString();
				username = etUsername.getText().toString();
				if(email.isEmpty()){
					Toast noEmial = Toast.makeText(RegisterActivity.this,
						     "Please input your e-mail", Toast.LENGTH_LONG);
					noEmial.setGravity(Gravity.CENTER, 0, 0);
					noEmial.show();
				}else if(password.isEmpty()){
					Toast noPassword = Toast.makeText(RegisterActivity.this,
						     "Please input your password", Toast.LENGTH_LONG);
					noPassword.setGravity(Gravity.CENTER, 0, 0);
					noPassword.show();
				}else if(passwordConfirm.isEmpty()){
					Toast noPasswordConfirm = Toast.makeText(RegisterActivity.this,
						     "Please confirm your password", Toast.LENGTH_LONG);
					noPasswordConfirm.setGravity(Gravity.CENTER, 0, 0);
					noPasswordConfirm.show();
				}else{
					if(password.compareTo(passwordConfirm) == 0){
						userInfo.setEmail(email);
						userInfo.setPassword(password);
						if(username.isEmpty())
							username = email;
						userInfo.setUsername(username);
						if(imageChanged){
				            userInfo.setImage(tempFile.getName());
						}else{
							userInfo.setImage("none");
						}
						new RegisterAT().execute(userInfo.getEmail(),userInfo.getPassword(),
								userInfo.getUsername(),userInfo.getImage());
						 	
					}else{
						Toast PasswordConfirmError = Toast.makeText(RegisterActivity.this,
							     "Password confirm failure", Toast.LENGTH_LONG);
						PasswordConfirmError.setGravity(Gravity.CENTER, 0, 0);
						PasswordConfirmError.show();
					}
				}
				return true;
			}
	    	
	    });
	    MenuItem miRegCancel = (MenuItem)menu.findItem(R.id.register_cancel);
	    miRegCancel.setOnMenuItemClickListener(new OnMenuItemClickListener(){
	    	
	    	public boolean onMenuItemClick(MenuItem item) {
	    		RegisterActivity.this.finish();
	    		return true;
	    	}
	    });
	    return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
            startPhotoZoom(Uri.fromFile(tempFile));
            break;
        case PHOTO_REQUEST_GALLERY:
            if (data != null)
                startPhotoZoom(data.getData());
            break;
        case PHOTO_REQUEST_CUT:
            if (data != null){
            	Bundle bundle = data.getExtras();
            	if (bundle != null) {
            		Bitmap photo = bundle.getParcelable("data");
            		if (photo==null) {
            			ivUser.setImageResource(R.drawable.no_photo_large);
            		}else {  
            			ivUser.setImageBitmap(photo);
            			BufferedOutputStream stream;
						try {
							stream = new BufferedOutputStream(
							        new FileOutputStream(tempFile));
							photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	                        stream.flush();
	                        stream.close();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            	}
            }
            break;
        }     
	}
	
	public void startPhotoZoom(Uri uri) {  
		Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);    
        intent.putExtra("outputX", 128);  
        intent.putExtra("outputY", 128);  
        intent.putExtra("return-data", true);  
        startActivityForResult(intent, PHOTO_REQUEST_CUT); 
    }  

	public static String stringToMD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
	
	private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'UserImage'yyyyMMddHHmmss");
        return dateFormat.format(date) + ".jpg";
//		if(email == null ||email.isEmpty())
//			return "";
//		else
//			return stringToMD5(email)+".jpg";
    }
	
	class RegisterAT extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				HttpClient httpClient = new DefaultHttpClient();
				if(imageChanged){
					HttpPost httpPost = new HttpPost(url);
					JSONObject jsonEntity = new JSONObject();
					if (params.length > 1) {
						jsonEntity.put("email", params[0]);
						jsonEntity.put("password", params[1]);
						jsonEntity.put("username", params[2]);
						jsonEntity.put("image", params[3]);
					} else {
						jsonEntity.put("err", "error");
					}
					MultipartEntity multipartEntity  = new MultipartEntity( );
					ContentBody cbFile;
					cbFile = new FileBody(tempFile, "image/jpg");
					multipartEntity.addPart("imgFile", cbFile);
				    ContentBody cbMessage = new StringBody(jsonEntity.toString(),Charset.forName("UTF-8")); ;
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
					String urlParams = "?email="+params[0]+"&password="+params[1]+"&username="+params[2];
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
			System.out.println(result);
			switch(result){
			case Primitive.CONNECTIONREFUSED:
				Toast connectError = Toast.makeText(RegisterActivity.this,
					     "Cannot connect to the server", Toast.LENGTH_LONG);
				connectError.setGravity(Gravity.CENTER, 0, 0);
				connectError.show();
				break;
			case Primitive.ACCEPT:
				Intent data=new Intent();  
	            data.putExtra("email", userInfo.getEmail());  
	            setResult(1, data);   
	            finish(); 
				break;
			case Primitive.DBCONNECTIONERROR:
				Toast DBError = Toast.makeText(RegisterActivity.this,
					     "Server database error", Toast.LENGTH_LONG);
				DBError.setGravity(Gravity.CENTER, 0, 0);
				DBError.show();
				break;
			case Primitive.USERREGISTERED:
				Toast userRegistered = Toast.makeText(RegisterActivity.this,
					     "This E-mail address has registered", Toast.LENGTH_LONG);
				userRegistered.setGravity(Gravity.CENTER, 0, 0);
				userRegistered.show();
				break;
			case Primitive.FILEPARSEERROR:
				Toast fileParseError = Toast.makeText(RegisterActivity.this,
					     "Image upload error", Toast.LENGTH_LONG);
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
}
