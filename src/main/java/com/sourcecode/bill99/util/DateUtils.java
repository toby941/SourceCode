package com.sourcecode.bill99.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.bao
 * @since 2013年10月31日
 */
public class DateUtils {

	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	private static final SimpleDateFormat dateFormat_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static boolean isToady(String dateStr) {
		try {
			if (StringUtils.isEmpty(dateStr)) {
				return false;
			}
			Date date = dateFormat_yyyyMMdd.parse(dateStr);
			Date now = Calendar.getInstance().getTime();
			return org.apache.commons.lang.time.DateUtils.isSameDay(date, now);
		} catch (ParseException e) {
			logger.error("parst date:{} error", dateStr, e);
			return false;
		}
	}

}
