package com.telegram.chart.view.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	public static String TOOLTIP_DAY_FORMAT = "EEE, d";
	public static String TOOLTIP_DAY_FORMAT_MAX = "EEE, dd";
	public static String TOOLTIP_MONTH_YEAR = "MMM yyyy";
	public static String TOOLTIP_YEAR_FORMAT_MAX = "MMM yyyy";
	public static String X_FORMAT = "MMM d";
	public static String X_FORMAT_MAX = "MMM dd";
	private static SimpleDateFormat INFO_DATE_DAY_FORMAT = new SimpleDateFormat(TOOLTIP_DAY_FORMAT, Locale.ENGLISH);
	private static SimpleDateFormat INFO_DATE_MONTH_YEAR_FORMAT = new SimpleDateFormat(TOOLTIP_MONTH_YEAR, Locale.ENGLISH);
	private static SimpleDateFormat X_DATE_FORMAT = new SimpleDateFormat(X_FORMAT, Locale.ENGLISH);
	private static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

	public static String TITLE_FORMAT = "dd MMM yyyy";
	private static SimpleDateFormat titleFormat = new SimpleDateFormat(TITLE_FORMAT, Locale.ENGLISH);

	public static String getTitle(long start, long end) {
		String startDate = getTitle(start);
		if (start == end) {
			return startDate;
		} else {
			return startDate + " - " + getTitle(end);
		}
	}

	public static String getTitle(long date) {
		calendar.setTimeInMillis(date);
		return titleFormat.format(calendar.getTime());
	}

	public static String getToolTipDay(long date){
		calendar.setTimeInMillis(date);
		return INFO_DATE_DAY_FORMAT.format(calendar.getTime());
	}

	public static String getToolTipMonthAndYear(long date){
		calendar.setTimeInMillis(date);
		return INFO_DATE_MONTH_YEAR_FORMAT.format(calendar.getTime());
	}

	public static String getDateX(long date){
		calendar.setTimeInMillis(date);
		return X_DATE_FORMAT.format(calendar.getTime());
	}


}