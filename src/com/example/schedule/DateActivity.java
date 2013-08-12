package com.example.schedule;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DateActivity extends Activity {

	private TextView dateTextView;
	private ListView dateListView;
	private JSONArray eventArray;
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
		try {
			eventArray =  new JSONArray(gainIntent.getStringExtra("eventArray"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dateTextView = (TextView)findViewById(R.id.tv_date);
		dateTextView.setText(date);
		dateListView = (ListView)findViewById(R.id.lv_date);
		
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.list_item_date,
                new String[]{"timeBegin","timeEnd","eventName","eventContent"},
                new int[]{R.id.tv_date_list_item_time_begin,
							R.id.tv_date_list_item_time_end,
							R.id.tv_date_list_item_event_name,
							R.id.tv_date_list_item_event_content});
		dateListView.setAdapter(adapter);
	}
	
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i = 0 ; i < eventArray.length() ; i++)
        {
        	
        	try {
        		JSONObject eventObject = (JSONObject)eventArray.get(i);
				long fromTimeMillis = eventObject.getLong("calFrom");
				long toTimeMillis = eventObject.getLong("calTo");
				Calendar calFrom = Calendar.getInstance();
				calFrom.setTimeInMillis(fromTimeMillis);
				Calendar calTo = Calendar.getInstance();
				calTo.setTimeInMillis(toTimeMillis);
				String eventName = eventObject.getString("eventName");
				String eventContent = eventObject.getString("decription");
				String locationName = eventObject.getString("locationName");
				Map<String, Object> map = new HashMap<String, Object>();
	            map.put("timeBegin", dateFormat(calFrom.getTime()));
	            map.put("timeEnd", dateFormat(calTo.getTime()));
	            map.put("eventName", eventName);
	            map.put("eventContent", eventContent);
	            list.add(map);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }         
        return list;
    }

	private String dateFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
        		"yyyy.MM.dd HH:mm");
        return dateFormat.format(date);
    }

}
