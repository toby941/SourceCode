package com.bill99.golden.inf.mapreduce;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.yarn.server.nodemanager.NodeStatusUpdaterImpl;

/**
 * @author jun.bao
 * @since 2014年1月3日
 */
public class MyMapper extends Mapper<Object, Text, Text, IntWritable> {

	private Log log = LogFactory.getLog(NodeStatusUpdaterImpl.class);

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		log.info("****************************value:" + value);
		System.err.println(value);
		context.write(new Text(), new IntWritable(Integer.parseInt(value.toString())));
	}

}
