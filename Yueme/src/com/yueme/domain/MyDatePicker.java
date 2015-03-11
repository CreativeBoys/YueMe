package com.yueme.domain;


import java.util.Calendar;

import com.yueme.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.NumberPicker;

public class MyDatePicker extends NumberPicker{
	
	MyDate myDates[];
	
	
	
	
	public MyDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
		
	}
	
	
	




	public MyDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}



	public MyDatePicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}



	public void init(){
		Calendar calendar = Calendar.getInstance();
		int currentMonth = calendar.get(Calendar.MONTH)+1;
		int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
		int monthLen = calendar.getActualMaximum(Calendar.DATE);
		
		myDates = new MyDate[31];
		
		String datesStr[] = new String[31];
		
		
		for(int i=0; i<myDates.length; i++) {
			myDates[i] = new MyDate();
			if(i<=monthLen-currentDay) {
				
				myDates[i].month = currentMonth;
				myDates[i].day = currentDay+i;
				
			}else{
				if(currentMonth!=12){
					myDates[i].month = currentMonth+1;
				} else{
					myDates[i].month = 1;
				}
				myDates[i].day = i-(monthLen-currentDay);
			}
			
			switch(i) {
			case 0:
				datesStr[i] = myDates[i].month+"月"+myDates[i].day+"日  今天";
				break;
			case 1:
				datesStr[i] = myDates[i].month+"月"+myDates[i].day+"日  明天";
				break;
			case 2:
				datesStr[i] = myDates[i].month+"月"+myDates[i].day+"日  后天";
				break;
			default:
				datesStr[i] = myDates[i].month+"月"+myDates[i].day+"日  "+i+"天后";
				break;
			}
			
					
			
		}
		
		setDisplayedValues(datesStr);
		setMaxValue(datesStr.length-1);
		setMinValue(0);
	}
	
	public int getMonth() {
		return myDates[getValue()].month;
	}
	
	public int getDay(){
		return myDates[getValue()].day;
	}
	
	class MyDate{
		int month;
		int day;
	}

}
