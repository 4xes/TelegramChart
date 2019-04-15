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

	public static String X_FORMAT_TIME_FORMAT = "h:mm";
	public static String X_FORMAT_TIME_MAX = "hh:mm";
	private static SimpleDateFormat INFO_DATE_DAY_FORMAT = new SimpleDateFormat(TOOLTIP_DAY_FORMAT, Locale.ENGLISH);
	private static SimpleDateFormat INFO_DATE_MONTH_YEAR_FORMAT = new SimpleDateFormat(TOOLTIP_MONTH_YEAR, Locale.ENGLISH);
	private static SimpleDateFormat X_DATE_FORMAT = new SimpleDateFormat(X_FORMAT, Locale.ENGLISH);


	private static SimpleDateFormat X_DATE_TIME = new SimpleDateFormat(X_FORMAT_TIME_FORMAT, Locale.ENGLISH);
	private static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	private static Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	public static String TITLE_FORMAT = "dd MMM yyyy";
	private static SimpleDateFormat titleFormat = new SimpleDateFormat(TITLE_FORMAT, Locale.ENGLISH);

	public static String getTitle(long start, long end) {
		String startDate = getTitle(start);
		String endDate = getTitle(end);
		if (start == end || startDate.equals(endDate)) {
			return startDate;
		} else {
			return startDate + " - " + endDate;
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

	public static String getTime(long date){
		calendar.setTimeInMillis(date);
		return X_DATE_TIME.format(calendar.getTime());
	}

	public static String getPath(int num, long date) {
		calendar.setTimeInMillis(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String strDay = String.valueOf(day);
		String strYear = String.valueOf(year);
		String strMonth = String.valueOf(month);
		if (strDay.length() == 1) {
			strDay = "0" + strDay;
		}

		if (strMonth.length() == 1) {
			strMonth = "0" + strMonth;
		}
		return num +"/" + strYear + "-" + strMonth + "/" + strDay;
	}

}