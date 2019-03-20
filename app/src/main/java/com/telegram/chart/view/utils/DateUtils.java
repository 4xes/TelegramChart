package com.telegram.chart.view.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	private static SimpleDateFormat INFO_DATE_FORMAT = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
	private static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

	public static String getInfoDate(long date){
		calendar.setTimeInMillis(date);
		return INFO_DATE_FORMAT.format(calendar.getTime());
	}
}