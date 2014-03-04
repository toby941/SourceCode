package com.bill99.golden.inf.mapreduce;

import java.io.IOException;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;

/**
 * @author jun.bao
 * @since 2014年1月2日
 */
public class HbaseMapper extends TableMapper<ImmutableBytesWritable, Put> {

	public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
		// this example is just copying the data from the source table...
		context.write(row, resultToPut(row, value));
	}

	private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
		Put put = new Put(key.get());
		for (KeyValue kv : result.raw()) {
			put.add(kv);
		}
		return put;
	}
}
