package com.sourcecode.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author jun.bao
 * @since 2013年12月19日
 */
public class JsonDomain {
	private Integer id;
	private String name;
	private Date time;
	private Long value;
	private List<String> arr;

	public JsonDomain() {
		super();
		id = RandomUtils.nextInt();
		name = "name" + id;
		time = new Date();
		value = RandomUtils.nextLong();
		arr = new ArrayList<String>();
		for (int i = 0; i < RandomUtils.nextInt(10) + 1; i++) {
			arr.add("value" + i);
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public List<String> getArr() {
		return arr;
	}

	public void setArr(List<String> arr) {
		this.arr = arr;
	}

	@Override
	public String toString() {
		return "JsonDomain [id=" + id + ", name=" + name + ", time=" + time + ", value=" + value + ", arr=" + arr + "]";
	}

}
