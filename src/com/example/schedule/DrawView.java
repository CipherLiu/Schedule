package com.example.schedule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	//For calculating event rect area
	private int[] eventPointOnAxisX={80,140,200,260,320,380,440}; 
	//Denotes the 0:00 of point on axis Y
	private int startOfYAxis; 
	//Offset per a o'clock
	private int axisyOffsetPerClock;
	//Event rect area width
	private int axisxOffsetPerEvent;
	private String JSONData;
	//Calculating if a all-day event
	private int currentDayOfYear;
	private Calendar currentTime;
	//Store parsed JSON data
	private ArrayList<MemberEvent> memberEventList = new ArrayList();
	/*
	 * Paint point,just as you see
	 */
	private int paintTopLeftX;
	private int paintTopLeftY;
	private int paintTopRightX;
	private int paintTopRightY;
	private int paintBottomLeftX;
	private int paintBottomLeftY;
	private int paintBottomRightX;
	private int paintBottomRightY;
	
	public class MemberEvent{
		public String member;
		public ArrayList<EventInterval> eventInterval;
		public String getMember() {
			return member;
		}
		public void setMember(String member) {
			this.member = member;
		}
		public ArrayList<EventInterval> getEventInterval() {
			return eventInterval;
		}
		public void setEventInterval(ArrayList<EventInterval> eventInterval) {
			this.eventInterval = eventInterval;
		}
	}
	public class EventInterval{
		public String calFrom;
		public String calTo;
		public String getCalFrom() {
			return calFrom;
		}
		public void setCalFrom(String calFrom) {
			this.calFrom = calFrom;
		}
		public String getCalTo() {
			return calTo;
		}
		public void setCalTo(String calTo) {
			this.calTo = calTo;
		}
	}
	
	public String getJSONData() {
		return JSONData;
	}
	public void setJSONData(String JSONData) {
		this.JSONData = JSONData;
	}
	/*
	 * Constructor for using DrawView successfully in layout XML file
	 */
	public DrawView(Context context) {
		super(context);
	}
	public DrawView(Context context, AttributeSet attrs) {

		super( context, attrs );
	}
	public DrawView(Context context, AttributeSet attrs, int defStyle) {

		super( context, attrs, defStyle );
	}
	private void init() {
		// TODO Auto-generated method stub
		startOfYAxis = 30;
		axisyOffsetPerClock=20;
		axisxOffsetPerEvent=40;
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		init();
		System.out.println("In onDraw:"+getJSONData());
		/*
		 * 	Parse JSON data
		 */
		JSONData = getJSONData();
		try {
			JSONObject joOrigionString = new JSONObject(JSONData);
			
			JSONObject joMember = new JSONObject();
			JSONObject joEvent = new JSONObject();
			
			JSONArray jaSocialArray = new JSONArray();
			JSONArray jaEventArray = new JSONArray();
			//Get socialArray array
			jaSocialArray = joOrigionString.getJSONArray("socialArray");
			for(int i=0 ; i < jaSocialArray.length() ; i++){
				MemberEvent memberEvent = new MemberEvent();
				ArrayList<EventInterval> eventIntervalList = new ArrayList();
				//Get i th element of jaSocialArray
				joMember = (JSONObject) jaSocialArray.get(i);
				//Get memberId of jaSocialArray(i)
				memberEvent.setMember(joMember.getString("memberId"));
				//Get eventArray array
				jaEventArray = joMember.getJSONArray("eventArray");
				
				for(int j=0 ; j < jaEventArray.length() ; j++){
					joEvent = (JSONObject) jaEventArray.get(j);
					//As follows
					EventInterval eventInterval = new EventInterval();
					eventInterval.setCalFrom(joEvent.getString("calFrom"));
					eventInterval.setCalTo(joEvent.getString("calTo"));
					eventIntervalList.add(eventInterval);
				}
				memberEvent.setEventInterval(eventIntervalList);
				//Add one to memberEventList
				memberEventList.add(memberEvent);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// Create Paint Object and draw for a pair of axis
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
		for(int i=startOfYAxis;i<=510;)
		{
		     	pathGrid.moveTo(50, i);
		       	pathGrid.lineTo(460,i);
		       	p.setPathEffect(effects);      
		       	canvas.drawPath(pathGrid, p); 
		       	i+=40;
		}
		/*
		 * Reset paint
		 */
		p.reset();
        p.setStyle(Paint.Style.FILL);
        /*
         * Current params
         */
        currentTime = Calendar.getInstance();
        currentDayOfYear = currentTime.get(Calendar.DAY_OF_YEAR);
        Calendar calFrom = Calendar.getInstance();
        Calendar calTo = Calendar.getInstance();
        /*
         * Draw interval grid
         */
		for(int i=0 ; i < Math.min(6,memberEventList.size()) ; i++){
			switch(i){
			case 0:
				p.setColor(Color.BLUE);
				for(int j=0 ; j<memberEventList.get(i).eventInterval.size() ; j++){
					calFrom.setTimeInMillis(Long.parseLong(memberEventList.get(i).eventInterval.get(j).getCalFrom()));
					calTo.setTimeInMillis(Long.parseLong(memberEventList.get(i).eventInterval.get(j).getCalTo()));
					if(calFrom.get(Calendar.DAY_OF_YEAR) == currentDayOfYear && calTo.get(Calendar.DAY_OF_YEAR)==currentDayOfYear){
						/*
						 * Get 4 important points of draw area
						 */
						paintTopLeftX = eventPointOnAxisX[0];
						paintTopLeftY = startOfYAxis+(calFrom.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calFrom.get(Calendar.MINUTE))/3;
						
						paintTopRightX = eventPointOnAxisX[0]+axisxOffsetPerEvent;
						paintTopRightY = startOfYAxis+(calFrom.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calFrom.get(Calendar.MINUTE))/3;
						
						paintBottomLeftX = eventPointOnAxisX[0];
						paintBottomLeftY = startOfYAxis+(calTo.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calTo.get(Calendar.MINUTE))/3;
						
						paintBottomRightX = eventPointOnAxisX[0]+axisxOffsetPerEvent;
						paintBottomRightY = startOfYAxis+(calTo.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calTo.get(Calendar.MINUTE))/3;
						//Begin draw this calculated area
						canvas.drawRect(paintTopLeftX, paintTopLeftY, paintBottomRightX, paintBottomRightY, p);
					}
				}
				break;
			case 1:
				p.setColor(Color.GREEN);
				break;
			case 2:
				 p.setColor(Color.YELLOW);
				break;
			case 3:
				 p.setColor(Color.MAGENTA);
				break;
			case 4:
				 p.setColor(Color.CYAN);
				break;
			case 5:
				 p.setColor(Color.LTGRAY);
				break;
			default:
				break;
			}
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
}
