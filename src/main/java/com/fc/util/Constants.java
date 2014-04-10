package com.fc.util;

/**
 * @author jun.bao
 * @since 2013年10月17日
 */
public class Constants {

	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_TRACE = "TRACE";

	public static final Integer DEPLOY_TYPE_CLIENT = 1;
	public static final Integer DEPLOY_TYPE_SERVER = 2;

	public final static Integer FLAG_ACTIVE = 1;
	public final static Integer FLAG_PAUSE = 2;
	public final static Integer FLAG_DELETE = 0;

	public final static String TOKEN_RESULT_CODE = "token";

	/**
	 * 优先级配置value，组与组之间的分隔符
	 */
	public final static String PRIORITY_VALUE_GROUP_DELIMITER = "#";
	/**
	 * 优先级配置 多个key或多个value之间的分隔符
	 */
	public final static String PRIORITY_VALUE_DELIMITER = ":";

	/**
	 * 默认每个token队列可借token占总容量的阈值为50%
	 */
	public final static Float DEFAULT_TOKEN_BORROW_THRESHOLD = 0.5f;

	/**
	 * 默认每个token队列获取token,并发排队最大数为10个
	 */
	public final static Integer DEFAULT_MAX_TOKEN_TASK_POOL_SIZE = 10;

	/**
	 * 不区分优先级排队时,并发排队最大数为20个
	 */
	public final static Integer DEFAULT_MAX_TOKEN_TASK_POOL_SIZE_NO_PRIORITY = 20;

}
