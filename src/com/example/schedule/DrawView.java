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

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;




public class DrawView extends View {
	//Region for clip
	//private Region region;
	private Bitmap[] profile = new Bitmap[8];
	//For calculating event rect area
	private int[] eventPointOnAxisX = {80,140,200,260,320,380,440}; 
	private int[] isIdle = new int[1441];
	//Denotes the 0:00 of point on axis Y
	private int startOfYAxis = 30;; 
	//Offset per o'clock
	private int axisyOffsetPerClock = 20;
	//Event rect area width
	private int axisxOffsetPerEvent = 40;
	
	private String JSONData;
	private int baseDay;
	private int sixTupleIndex;
	private int sixTupleNumber;
	//Calculating if a all-day event
	private int currentDayOfYear;
	private Calendar currentTime;
	private Calendar todayMinStart;
	//Store parsed JSON data
	private ArrayList<MemberEvent> memberEventList = new ArrayList();
	/*
	 * Paint point,just as you see
	 */
	private int paintTopLeftX;
	private int paintTopLeftY;
	private int paintBottomRightX;
	private int paintBottomRightY;
	/*
	 * memberEventList store a list of MemberEvent,
	 * MemberEvent contains member and eventInterval,
	 * each eventInterval is a ArrayList,
	 * EventInterval contains From and To time tag
	 */
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
	/*
	 * Time interval as long 
	 */
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
	/*
	 * Getter and setter
	 * Use of getting data from EventDeatilActivity
	 */
	public String getJSONData() {
		return JSONData;
	}
	public void setJSONData(String JSONData) {
		this.JSONData = JSONData;
	}
	public int getBaseDay() {
		return baseDay;
	}
	public void setBaseDay(int baseDay) {
		this.baseDay = baseDay;
	}
	public int getSixTupleIndex() {
		return sixTupleIndex;
	}
	public void setSixTupleIndex(int sixTupleIndex) {
		this.sixTupleIndex = sixTupleIndex;
	}
	/*
	 * Attention please!!!
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
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		JSONData = getJSONData();
		baseDay = getBaseDay();
		sixTupleIndex = getSixTupleIndex();
		/*
		 * Clear dirt data
		 */
		if(memberEventList.size()!=0){
			memberEventList.clear();
		}
		for(int i=0 ; i<=1440 ; i++){
			isIdle[i]=0;
		}
		/*
		 * 	Parse JSON data
		 */
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
		//Draw Graduation Text,just as you see
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
        currentDayOfYear = baseDay;
        
        Calendar calFrom = Calendar.getInstance();
        Calendar calTo = Calendar.getInstance();
        //System.out.println("Minutes diff:"+minutesDiff(currentTime));
        /*
         * Draw interval grid
         */
        sixTupleNumber = memberEventList.size()/6;
        int realIndex=0;
		for(int i=sixTupleIndex*6 ; i < ((sixTupleIndex < sixTupleNumber)?(sixTupleIndex*6+6): (sixTupleIndex*6 +memberEventList.size()%6)); i++){
			realIndex = i;
			switch(i%6){
			case 0:
				p.setColor(Color.RED);
				doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			case 1:
				p.setColor(Color.GREEN);
				doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			case 2:
				 p.setColor(Color.YELLOW);
				 doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			case 3:
				 p.setColor(Color.MAGENTA);
				 doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			case 4:
				 p.setColor(Color.CYAN);
				 doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			case 5:
				 p.setColor(Color.DKGRAY);
				 doDrawRect(canvas , p , i%6 , calFrom , calTo,realIndex);
				break;
			default:
				break;
			}
		}
		//Calculate common idle interval
		for(int i=0;i<memberEventList.size();i++){
			for(int j=0 ; j<memberEventList.get(i).eventInterval.size() ; j++){
				calFrom.setTimeInMillis(Long.parseLong(memberEventList.get(i).eventInterval.get(j).getCalFrom()));
				calTo.setTimeInMillis(Long.parseLong(memberEventList.get(i).eventInterval.get(j).getCalTo()));
				if(calFrom.get(Calendar.DAY_OF_YEAR) < currentDayOfYear){
					if(calTo.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
						for(int k= 0; k < minutesDiff(calTo);k++){
							isIdle[k] = 1;
						}
					}
					else if(calTo.get(Calendar.DAY_OF_YEAR) > currentDayOfYear){
						for(int k= 0; k < 1440;k++){
							isIdle[k] = 1;
						}
					}
					else{
					}
					
				}
				//Event interval starts at today
				else if(calFrom.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
					//Event interval ends at today
					if(calTo.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
						for(int k= (int) minutesDiff(calFrom); k < minutesDiff(calTo);k++){
							isIdle[k] = 1;
						}
					}
					//Event interval ends after today
					else if(calTo.get(Calendar.DAY_OF_YEAR) > currentDayOfYear){
						for(int k= (int) minutesDiff(calFrom); k < 1440 ;k++){
							isIdle[k] = 1;
						}
					}
					else{
					}
				}
				else{
				}
			}
		}
		p.reset();
		p.setColor(Color.BLUE);
		p.setAlpha(155);
        /*
         * Calculating common-idle time
         */
		int drawIdleIntervalStart;
		int drawIdleIntervalend;
		isIdle[1439]=1;
		for(int i=0 ; i<1440 ;i++){
			if(isIdle[i]==0){
				drawIdleIntervalStart = i;
				for(int k=i+1 ; k<1440 ;k++){
					if(isIdle[k]==1){
						drawIdleIntervalend = k;
						//Draw
						canvas.drawRect(50, startOfYAxis+drawIdleIntervalStart/3, 460, startOfYAxis+drawIdleIntervalend/3, p);
						i = k+1;
						break;
					}
				}
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
	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getX();
		float y=event.getY();
		touchedWhichEvent(x,y);
		return super.onTouchEvent(event);
	}
	*/
	private void doDrawRect(Canvas canvas,Paint p,int i,Calendar calFrom,Calendar calTo,int realIndex){
		for(int j=0 ; j<memberEventList.get(realIndex).eventInterval.size() ; j++){
			calFrom.setTimeInMillis(Long.parseLong(memberEventList.get(realIndex).eventInterval.get(j).getCalFrom()));
			calTo.setTimeInMillis(Long.parseLong(memberEventList.get(realIndex).eventInterval.get(j).getCalTo()));
			//Event interval starts before today
			if(calFrom.get(Calendar.DAY_OF_YEAR) < currentDayOfYear){
				//Event interval ends at today
				if(calTo.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
					/*
					 * Get 4 important points of draw area
					 */
					paintTopLeftX = eventPointOnAxisX[i];
					paintTopLeftY = startOfYAxis;

					paintBottomRightX = eventPointOnAxisX[i]+axisxOffsetPerEvent;
					paintBottomRightY = startOfYAxis+(calTo.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calTo.get(Calendar.MINUTE))/3;
					//Begin draw this calculated area
					canvas.drawRect(paintTopLeftX, paintTopLeftY, paintBottomRightX, paintBottomRightY, p);
					//Color interval
					for(int k= 0; k < minutesDiff(calTo);k++){
						isIdle[k] = 1;
					}
				}
				/*
				 * Event interval ends after today,
				 * meaning the event is a all-day event,
				 * draw the whole rectangle area
				 */
				else if(calTo.get(Calendar.DAY_OF_YEAR) > currentDayOfYear){
					paintTopLeftX = eventPointOnAxisX[i];
					paintTopLeftY = startOfYAxis;

					paintBottomRightX = eventPointOnAxisX[i]+axisxOffsetPerEvent;
					paintBottomRightY = startOfYAxis+24*axisyOffsetPerClock;
					//Begin draw this calculated area
					canvas.drawRect(paintTopLeftX, paintTopLeftY, paintBottomRightX, paintBottomRightY, p);
					for(int k= 0; k < 1440;k++){
						isIdle[k] = 1;
					}
				}
				else{
				/*
				 * A event starts before today,and it ends before today
				 * So,it will not show here,
				 * just do nothing
				 */
				}
				
			}
			//Event interval starts at today
			else if(calFrom.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
				//Event interval ends at today
				if(calTo.get(Calendar.DAY_OF_YEAR) == currentDayOfYear){
					paintTopLeftX = eventPointOnAxisX[i];
					paintTopLeftY = startOfYAxis+(calFrom.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calFrom.get(Calendar.MINUTE))/3;

					paintBottomRightX = eventPointOnAxisX[i]+axisxOffsetPerEvent;
					paintBottomRightY = startOfYAxis+(calTo.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calTo.get(Calendar.MINUTE))/3;
					
					canvas.drawRect(paintTopLeftX, paintTopLeftY, paintBottomRightX, paintBottomRightY, p);
					for(int k= (int) minutesDiff(calFrom); k < minutesDiff(calTo);k++){
						isIdle[k] = 1;
					}
				}
				//Event interval ends after today
				else if(calTo.get(Calendar.DAY_OF_YEAR) > currentDayOfYear){
					paintTopLeftX = eventPointOnAxisX[i];
					paintTopLeftY = startOfYAxis+(calFrom.get(Calendar.HOUR_OF_DAY))*axisyOffsetPerClock+(calFrom.get(Calendar.MINUTE))/3;
					
					paintBottomRightX = eventPointOnAxisX[i]+axisxOffsetPerEvent;
					paintBottomRightY = startOfYAxis+24*axisyOffsetPerClock;
					
					canvas.drawRect(paintTopLeftX, paintTopLeftY, paintBottomRightX, paintBottomRightY, p);
					for(int k= (int) minutesDiff(calFrom); k < 1440 ;k++){
						isIdle[k] = 1;
					}
				}
				else{
				/*
				 * Logical error!
				 * should not be here,
				 * just do nothing
				 */
				}
			}
			else{
			/*
			 * Event interval starts in the future,
			 * will  not be here,
			 * just do nothing 
			 */
			}
		}
	}
	private long minutesDiff(Calendar minuteToCalculate){
        todayMinStart = Calendar.getInstance();
        todayMinStart.set(Calendar.DAY_OF_YEAR, baseDay);
        todayMinStart.set(Calendar.HOUR_OF_DAY, 0);
        todayMinStart.set(Calendar.MINUTE, 0);
        todayMinStart.set(Calendar.SECOND, 0);
        todayMinStart.set(Calendar.MILLISECOND,0);
        return (minuteToCalculate.getTimeInMillis()-todayMinStart.getTimeInMillis())/60000;
	}
	//Parse touch event
	private void touchedWhichEvent(float x, float y) {
		// TODO Auto-generated method stub
		//Just judge these rect
		if((x>=eventPointOnAxisX[0] && x<=eventPointOnAxisX[5]+axisxOffsetPerEvent) && (y>=startOfYAxis && y<=startOfYAxis+480)){
			//User1
			if(x>=eventPointOnAxisX[0] && x<=eventPointOnAxisX[0]+axisxOffsetPerEvent){
			}
			//User2
			if(x>=eventPointOnAxisX[1] && x<=eventPointOnAxisX[1]+axisxOffsetPerEvent){
			}
			//User3
			if(x>=eventPointOnAxisX[2] && x<=eventPointOnAxisX[2]+axisxOffsetPerEvent){
			}
			//User4
			if(x>=eventPointOnAxisX[3] && x<=eventPointOnAxisX[3]+axisxOffsetPerEvent){
			}
			//User5
			if(x>=eventPointOnAxisX[4] && x<=eventPointOnAxisX[4]+axisxOffsetPerEvent){
			}
			//User6
			if(x>=eventPointOnAxisX[5] && x<=eventPointOnAxisX[5]+axisxOffsetPerEvent){
			}
		}
	}	
}
