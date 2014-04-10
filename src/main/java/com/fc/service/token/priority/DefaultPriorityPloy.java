package com.fc.service.token.priority;

import java.io.InputStream;

import com.bill99.fc.service.token.priority.PriorityPloy;

/**
 * 默认实现
 * 
 * @author jun.bao
 * @since 2013年8月29日
 */
public class DefaultPriorityPloy implements PriorityPloy {

	/**
	 * 不解析 请求内容，直接返回 {@link Integer#MAX_VALUE} 视为最低优先级
	 */
	@Override
	public Integer getPriority(InputStream in) {

		return Integer.MAX_VALUE;
	}

}
