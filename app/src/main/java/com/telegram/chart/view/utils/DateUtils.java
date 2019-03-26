package com.telegram.chart.view.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	public static String INFO_FORMAT = "EEE, MMM d";
	public static String X_FORMAT = "MMM d";
	public static String XMAX = "MMM dd";
	private static SimpleDateFormat INFO_DATE_FORMAT = new SimpleDateFormat(INFO_FORMAT, Locale.ENGLISH);
	private static SimpleDateFormat X_DATE_FORMAT = new SimpleDateFormat(X_FORMAT, Locale.ENGLISH);
	private static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

	public static String getInfoDate(long date){
		calendar.setTimeInMillis(date);
		return INFO_DATE_FORMAT.format(calendar.getTime());
	}

	public static String getDateX(long date){
		calendar.setTimeInMillis(date);
		return X_DATE_FORMAT.format(calendar.getTime());
	}
}