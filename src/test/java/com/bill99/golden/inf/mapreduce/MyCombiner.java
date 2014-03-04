package com.bill99.golden.inf.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author jun.bao
 * @since 2014年1月3日
 */
public class MyCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		int temp = Integer.MIN_VALUE;
		for (IntWritable value : values) {
			if (value.get() > temp) {
				temp = value.get();
			}
		}
		context.write(new Text(), new IntWritable(temp));
	}
}
