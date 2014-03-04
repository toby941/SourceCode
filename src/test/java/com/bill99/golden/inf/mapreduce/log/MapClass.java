package com.bill99.golden.inf.mapreduce.log;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * @author jun.bao
 * @since 2014年1月6日
 */
public class MapClass extends Mapper<Object, Text, Text, IntWritable> {
	private Text record = new Text();
	private static final IntWritable recbytes = new IntWritable(1);

	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		// 没有配置 RecordReader，所以默认采用 line 的实现，
		// key 就是行号，value 就是行内容，
		// 按行 key-value 存放每行 loglevel 和 logmodule 内容
		if (line == null || line.equals(""))
			return;
		String[] words = line.split("> <");
		if (words == null || words.length < 2)
			return;
		String logLevel = words[1];
		String moduleName = words[2];

		record.clear();
		record.set(new StringBuffer("logLevel::").append(logLevel).toString());
		context.write(record, recbytes);
		// 输出日志级别统计结果，通过 logLevel:: 作为前缀来标示。

		record.clear();
		record.set(new StringBuffer("moduleName::").append(moduleName).toString());
		context.write(record, recbytes);
		// 输出模块名的统计结果，通过 moduleName:: 作为前缀来标示
	}
}
