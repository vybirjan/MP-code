package cz.cvut.fit.vybirjan.mp.web.dto;

import java.text.DateFormat;
import java.text.Format;
import java.util.Date;

public class DTO {

	public static final String NONE = "-";
	public static final Format DATE_FORMAT = DateFormat.getInstance();

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

}
