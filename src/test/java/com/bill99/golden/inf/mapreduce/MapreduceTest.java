package com.bill99.golden.inf.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bill99.golden.inf.hbase.service.SearchService;

/**
 * @author jun.bao
 * @since 2014年1月2日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class MapreduceTest {

	@Autowired
	private SearchService searchService;

	// @Test
	public void copyTable() throws IOException {

		HTable t = new HTable(searchService.getConfiguration(), "test");
		HTableDescriptor hTableDescriptor = t.getTableDescriptor();
		HTableDescriptor newTabDes = new HTableDescriptor(hTableDescriptor);
		newTabDes.setName(Bytes.toBytes("test_copy"));
		HBaseAdmin admin = new HBaseAdmin(searchService.getConfiguration());
		admin.createTable(newTabDes);
	}

	@Test
	public void copyData() throws IOException, InterruptedException, ClassNotFoundException {
		Configuration config = searchService.getConfiguration();
		config.set("mapred.job.tracker", "namenode01:9001");
		Job job = new Job(config, "ExampleReadWrite");
		job.setJarByClass(MyMapper.class); // class that contains mapper

		Scan scan = new Scan();
		scan.setCaching(500); // 1 is the default in Scan, which will be bad for MapReduce jobs
		scan.setCacheBlocks(false); // don't set to true for MR jobs
		// set other scan attrs

		TableMapReduceUtil.initTableMapperJob("test", // input table
				scan, // Scan instance to control CF and attribute selection
				MyMapper.class, // mapper class
				null, // mapper output key
				null, // mapper output value
				job);
		TableMapReduceUtil.initTableReducerJob("test_copy", // output table
				null, // reducer class
				job);
		job.setNumReduceTasks(0);

		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error with job!");
		}
	}

}
