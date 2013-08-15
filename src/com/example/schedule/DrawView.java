package com.example.schedule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;




public class DrawView extends View {
	//Region for clip
	//private Region region;
	private Bitmap[] profile=new Bitmap[8];
	//For calculating touch event area
	private int[] eventPointOnAxisX=new int[7]; 
	private int[] eventPointOnAxisY=new int[25]; 
	private int profileRectLeft;
	private int profileRectTop;
	private int axisxOffsetPerClock;
	private int axisyOffsetPerEvent;
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/*
	 * Constructor for using DrawView successfully in layout XML file
	 */
	public DrawView(Context context) {
		super(context);
		init();
	}
	public DrawView(Context context, AttributeSet attrs) {

		super( context, attrs );
	}

	public DrawView(Context context, AttributeSet attrs, int defStyle) {

		super( context, attrs, defStyle );
	}
	private void init() {
		// TODO Auto-generated method stub
		int[] eventPointOnAxisX={80,140,200,260,320,380,440};
		/*
		 * Loop for draw horizontal line of the grid
		 */
		for(int i=0;i<=24;i++)
		{
			eventPointOnAxisY[i]=30+i*20;
		}
		/*
		 * Rectangle area for profile
		 */
		profileRectLeft=80;
		profileRectTop=520;
		axisxOffsetPerClock=20;
		axisyOffsetPerEvent=60;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Create Paint Object
		Paint p = new Paint();
		p.setColor(Color.RED);
		//Create Graduation Axis
		canvas.drawLine(55, 20, 55, 520, p);
		//Draw Graduation Text
		canvas.drawText("0:00", 10, 30, p);
		canvas.drawText("2:00", 10, 70, p);
		canvas.drawText("4:00", 10, 110, p);
		canvas.drawText("6:00", 10, 150, p);
		canvas.drawText("8:00", 10, 190, p);
		canvas.drawText("10:00", 10, 230, p);
		canvas.drawText("12:00", 10, 270, p);
		canvas.drawText("14:00", 10, 310, p);
		canvas.drawText("16:00", 10, 350, p);
		canvas.drawText("18:00", 10, 390, p);
		canvas.drawText("20:00", 10, 430, p);
		canvas.drawText("22:00", 10, 470, p);
		canvas.drawText("24:00", 10, 510, p);
		/*
		 * Dashed line
		 */
		p.reset();
		p.setStyle(Paint.Style.STROKE);      
        p.setColor(Color.DKGRAY);      
        Path pathGrid = new Path();   
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);   
        for(int i=30;i<=510;)
        {
        	pathGrid.moveTo(50, i);
        	pathGrid.lineTo(460,i);
        	p.setPathEffect(effects);      
            canvas.drawPath(pathGrid, p); 
        	i+=40;
        }
        
        /*
         * A operation for clip the bitmap object to circle frame
         * 
         */
        /*
        profile[0] = BitmapFactory.decodeResource(getResources(), R.drawable.beijing);  
        Paint paint=new Paint();
        canvas.save();   
        Path newPath=new Path();
        canvas.clipPath(newPath);
        newPath.addCircle(profileRectLeft+20, profileRectTop+20, 20, Path.Direction.CCW);  
        canvas.clipPath(newPath, Region.Op.REPLACE);  
        canvas.drawBitmap(profile[0],null,new Rect(profileRectLeft,profileRectTop,profileRectLeft+40,profileRectTop+40),paint);
        canvas.restore(); 
        */
        //Test info 
        p.reset();
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(80, 190, 120, 350, p);
        p.setColor(Color.GREEN);
        canvas.drawRect(140, 210, 180, 350, p);
        p.setColor(Color.YELLOW);
        canvas.drawRect(200, 30, 240, 510, p);
        p.setColor(Color.MAGENTA);
        canvas.drawRect(260, 390, 300, 430, p);
        p.setColor(Color.CYAN);
        canvas.drawRect(320, 50, 360, 90, p);
        p.setColor(Color.LTGRAY);
        canvas.drawRect(380, 190, 420, 350, p);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getX();
		float y=event.getY();
		/*Debug info of touch event
		StringBuilder tip = new StringBuilder();   
		tip.append(x);
		tip.append("+");
		tip.append(y);
		Toast msg = Toast.makeText(this.getContext(),
			     tip, Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, 0, 0);
		msg.show();
		*/
		touchedWhichEvent(x,y);
		return super.onTouchEvent(event);
	}
	//Parse touch event
	private void touchedWhichEvent(float x, float y) {
		// TODO Auto-generated method stub
		if((x>=80 && x<=440) && (y>=30 && y<=510))
		{
			//User1
			if(x>=80 && x<=120)
			{
				if(y>=190 && y<=350)
				{
					//Debug info
					Toast msg = Toast.makeText(this.getContext(),
						     "Touched event", Toast.LENGTH_LONG);
					msg.setGravity(Gravity.CENTER, 0, 0);
					msg.show();
				}
			}
		}
	}
	/*
	 * AsyncTask for getting picture from the server
	 */
	/*
	class UserImageAT extends AsyncTask<String,Integer,Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap bitmap = getBitmapFromUrl(params[0]);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			profile[1]=result;
			}	
	};
	private Bitmap getBitmapFromUrl(String imgUrl) {
		    URL url;
		    Bitmap bitmap = null;
		    try {
		            url = new URL(imgUrl);
		            InputStream is = url.openConnection().getInputStream();
		            BufferedInputStream bis = new BufferedInputStream(is);
		            bitmap = BitmapFactory.decodeStream(bis);
		            bis.close();
		    } catch (MalformedURLException e) {
		            e.printStackTrace();
		    } catch (IOException e) {
		            e.printStackTrace();
		    }
		    return bitmap;
		}
		*/
	
}
