package com.sourcecode.io;

import java.io.Serializable;
import java.util.List;

/**
 * @author jun.bao
 * @since 2014年3月27日
 */
public class FileSerializable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6601995018104685845L;
	private List<String> list;

	public FileSerializable() {
		super();
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

}
