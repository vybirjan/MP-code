package cz.cvut.fit.vybirjan.mp.web.dto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DTO {

	public static final String NONE = "-";
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	public static String format(Date d) {
		if (d == null) {
			return NONE;
		} else {
			return DATE_FORMAT.format(d);
		}
	}

	public static String notNull(String s) {
		return s == null ? NONE : s;
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static Date parseDate(String date) throws ParseException {
		if (isNullOrEmpty(date)) {
			return null;
		} else {
			return DATE_FORMAT.parse(date);
		}
	}

}
