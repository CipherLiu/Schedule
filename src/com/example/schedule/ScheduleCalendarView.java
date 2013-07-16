package com.example.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schedule.LoginActivity.eventCheckAT;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.LinearLayout.LayoutParams;

public class ScheduleCalendarView {
	private static String eventCheckUrl = Global.BASICURL+"EventCheck";
	private int iFirstDayOfWeek = Calendar.SUNDAY;
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
	private boolean hasEventArray[] = new boolean[42];
	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	private Calendar calFocused = Calendar.getInstance();
	
	public LinearLayout layContent = null;
	public LinearLayout layMain = null;
	private ProgressDialog progressDialog;
	private TextView monthTextView,yearTextView;
	public static final int SELECT_DATE_REQUEST = 111;
	private static final int iDayCellWidthPartrait = 68;
	private static final int iDayCellHeightPatrait = 82;
	private static final int iDayHeaderHeight = 34;
	private Context context;
	private String userId;
	
	ScheduleCalendarView(Context context,boolean[] hasEventArray,String userId){
		this.context = context;
		this.hasEventArray = hasEventArray;
		this.progressDialog = new ProgressDialog(this.context);
		this.userId = userId;
	}
	
	public TextView getMonthTextView() {
		return monthTextView;
	}

	public void setMonthTextView(TextView monthTextView) {
		this.monthTextView = monthTextView;
	}

	public Calendar getCalFocused() {
		return calFocused;
	}

	public void setCalFocused(Calendar calFocused) {
		this.calFocused = calFocused;
	}

	public Calendar getCalSelected() {
		
		return calSelected;
	}
	public void setCalSelected(Calendar calSelected) {
		this.calSelected = calSelected;
		//this.calFocused = calSelected;
	}

	public boolean[] getHasEventArray() {
		return hasEventArray;
	}

	public void setHasEventArray(boolean[] hasEventArray) {
		this.hasEventArray = hasEventArray;
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(context);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		return lay;
	}

	public void updateYearMonthText(){
		monthTextView.setText(format(calFocused.get(Calendar.MONTH)+1));
		yearTextView.setText(" "+calFocused.get(Calendar.YEAR)+"-");
	}

	
	public View generateContentViewPortrait() {
		layMain = createLayout(LinearLayout.VERTICAL);
		layMain.setPadding(2, 8, 0, 8);
		layContent = createLayout(LinearLayout.VERTICAL);
		yearTextView = new TextView(context);
		yearTextView.setPadding(8, 8, 0, 8);
		yearTextView.setTextAppearance(context, R.style.Bold20sp);
		yearTextView.setWidth(100);
		yearTextView.setBottom(15);
		monthTextView = new TextView(context);
		monthTextView.setPadding(0, 8, 8, 8);
		monthTextView.setTextAppearance(context, R.style.Bold20sp);;
		monthTextView.setWidth(50);
		monthTextView.setBottom(15);
		updateYearMonthText();
		
		Button btnPrevMonth = new Button(context);
		btnPrevMonth.setLayoutParams(new LayoutParams(50,50));
		btnPrevMonth.setBackgroundResource(R.drawable.previous);
		
		Button btnNextMonth = new Button(context);
		btnNextMonth.setLayoutParams(new LayoutParams(50,50));
		btnNextMonth.setBackgroundResource(R.drawable.next);
		btnPrevMonth.setOnClickListener(new Button.OnClickListener(){
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPrevMonthViewItem();
			}
		});
		btnNextMonth.setOnClickListener(new Button.OnClickListener(){
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setNextMonthViewItem();
			}
		});
		LinearLayout layYearMonth = createLayout(LinearLayout.HORIZONTAL);
		layYearMonth.addView(btnPrevMonth);
		layYearMonth.addView(yearTextView);
		layYearMonth.addView(monthTextView);
		layYearMonth.addView(btnNextMonth);
		layYearMonth.setGravity(Gravity.CENTER_HORIZONTAL);
		
		generateCalendar(layContent,iDayCellWidthPartrait,iDayCellHeightPatrait);
		layContent.setGravity(Gravity.CENTER_HORIZONTAL);
		layContent.setBottom(90);
		layMain.addView(layYearMonth);
		layMain.addView(layContent);
		layMain.setBackgroundResource(R.drawable.page_bg);
		
		return layMain;
	}

	private View generateCalendarRow(int cellWidth,int cellHeight) {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(context,
					cellWidth, cellHeight);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}
		return layRow;
	}

	private View generateCalendarHeader(int cellWidth) {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayHeader day = new DateWidgetDayHeader(context,
					cellWidth, iDayHeaderHeight);
			final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}
		return layRow;
	}

	private void generateCalendar(LinearLayout layContent,int cellWidth,int cellHeight) {
		layContent.addView(generateCalendarHeader(cellWidth));
		days.clear();
		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow(cellWidth,cellHeight));
		}
	}

	public Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calFocused.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calFocused.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}
		updateStartDateForMonth();

		return calStartDate;
	}

	public DateWidgetDayCell updateCalendar() {
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		int i = 0;
		for (; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DateWidgetDayCell dayCell = days.get(i);
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;
			//check event
			
			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					hasEventArray[i],iMonthViewCurrentMonth,iDayOfWeek);
			bSelected = false;
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			dayCell.setSelected(bSelected);
			if (bSelected)
				daySelected = dayCell;
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		//layMain.invalidate();
		//layContent.invalidate();
		for(int j = 0 ;j < days.size() ; j++)
			days.get(j).invalidate();
		return daySelected;
	}
	
	
	
	public Calendar updateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
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
		return calStartDate;
	}

	private void setPrevMonthViewItem() {
		iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		Calendar calDateToCheck = Calendar.getInstance();
		calDateToCheck.setTimeInMillis(calStartDate.getTimeInMillis());
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calDateToCheck.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calDateToCheck.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calDateToCheck.add(Calendar.DAY_OF_WEEK, -iDay);
		calDateToCheck.set(Calendar.HOUR_OF_DAY, 0);
		calDateToCheck.set(Calendar.MINUTE, 0);
		calDateToCheck.set(Calendar.SECOND, 0);
		calDateToCheck.set(Calendar.MILLISECOND, 0);
		String calString = calDateToCheck.getTimeInMillis()+"";
		calFocused.set(Calendar.DAY_OF_MONTH, 1);
		calFocused.roll(Calendar.MONTH, false);
		new eventCheckAT().execute(calString,userId);
	}

	private void setNextMonthViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		Calendar calDateToCheck = Calendar.getInstance();
		calDateToCheck.setTimeInMillis(calStartDate.getTimeInMillis());
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calDateToCheck.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calDateToCheck.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calDateToCheck.add(Calendar.DAY_OF_WEEK, -iDay);
		calDateToCheck.set(Calendar.HOUR_OF_DAY, 0);
		calDateToCheck.set(Calendar.MINUTE, 0);
		calDateToCheck.set(Calendar.SECOND, 0);
		calDateToCheck.set(Calendar.MILLISECOND, 0);
		String calString = calDateToCheck.getTimeInMillis()+"";
		calFocused.set(Calendar.DAY_OF_MONTH, 1);
		calFocused.roll(Calendar.MONTH, true);
		new eventCheckAT().execute(calString,userId);
		
	}
	
	private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
		public void OnClick(DateWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			calFocused.setTimeInMillis(item.getDate().getTimeInMillis());
			item.setSelected(true);
			Calendar calForTest = Calendar.getInstance();
			calForTest.setTimeInMillis(calStartDate.getTimeInMillis());
			calStartDate = getCalendarStartDate();
			if(!calStartDate.equals(calForTest)){
				String calString = calStartDate.getTimeInMillis()+"";
				new eventCheckAT().execute(calString,userId);
			}else{
				updateYearMonthText();
				updateCalendar();
			}
			Intent i = new Intent(context,DateActivity.class);
			i.putExtra("year", calSelected.get(Calendar.YEAR));
			i.putExtra("month", calSelected.get(Calendar.MONTH)+1);
			i.putExtra("dayOfMonth", calSelected.get(Calendar.DAY_OF_MONTH));
			context.startActivity(i);
		}
	};
	
	private void updateCenterTextView(int iMonthViewCurrentMonth,int iMonthViewCurrentYear){
        monthTextView.setText(format(iMonthViewCurrentMonth+1)+"");
        yearTextView.setText(" " + iMonthViewCurrentYear + "-");
	}
	
	private void updateDate() {
		updateStartDateForMonth();
		updateCalendar();
	}

	public void update(){
		getCalendarStartDate();
		updateYearMonthText();
		//updateStartDateForMonth();
		updateCalendar();
	}
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
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
					Toast connectError = Toast.makeText(context,
						     "Cannot connect to the server", Toast.LENGTH_LONG);
					connectError.setGravity(Gravity.CENTER, 0, 0);
					connectError.show();
					break;
				case Primitive.ACCEPT:
					JSONArray jArray = result.getJSONArray("hasEventArray");
					for(int i = 0 ; i < jArray.length() ; i++){
					hasEventArray[i] = jArray.getBoolean(i);
					}
					updateDate();
					updateCenterTextView(iMonthViewCurrentMonth,iMonthViewCurrentYear);
					break;
				case Primitive.DBCONNECTIONERROR:
					Toast DBError = Toast.makeText(context,
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
	
}
