package com.telegram.chart.view.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	public static String TOOLTIP_FORMAT = "EEE, MMM d yyyy";
	public static String TOOLTIP_FORMAT_MAX = "EEE, MMM dd yyyy";
	public static String X_FORMAT = "MMM d";
	public static String X_FORMAT_MAX = "MMM dd";
	private static SimpleDateFormat INFO_DATE_FORMAT = new SimpleDateFormat(TOOLTIP_FORMAT, Locale.ENGLISH);
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