package com.bill99.golden.inf.mapreduce.log;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author jun.bao
 * @since 2014年1月6日
 */
public class ReduceClass extends Reducer<Text, IntWritable, Text, IntWritable> {
	private IntWritable result = new IntWritable();

	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
			InterruptedException {

		int tmp = 0;
		for (IntWritable val : values) {
			tmp = tmp + val.get();
		}
		result.set(tmp);
		context.write(key, result);// 输出最后的汇总结果
	}
}
