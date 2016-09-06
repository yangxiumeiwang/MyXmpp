package com.xmpp.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yxm on 2016/8/31.
 * 时间工具
 */
public class TimeRenderUtil {

	private static SimpleDateFormat formatBuilder;

	public static String getDate(String format) {
		formatBuilder = new SimpleDateFormat(format);
		return formatBuilder.format(new Date());
	}

	public static String getDate() {
		return getDate("MM-dd  hh:mm:ss");
	}
}
