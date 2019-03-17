package com.telegram.chart.view.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM", Locale.getDefault());
	private static Calendar calendar = Calendar.getInstance();

	static {
		calendar.setTimeZone(TimeZone.getDefault());
	}

	public static String getDate(long date){
		calendar.setTimeInMillis(date);
		return DATE_FORMAT.format(calendar.getTime());
	}
}