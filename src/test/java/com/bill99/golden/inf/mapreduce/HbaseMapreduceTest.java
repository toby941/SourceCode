package com.bill99.golden.inf.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
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
public class HbaseMapreduceTest {

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

	public Configuration getConf() {
		Configuration conf = searchService.getConfiguration();

		conf.set("mapreduce.framework.name", "yarn");
		conf.set("hbase.zookeeper.quorum", "zk01,zk02");
		conf.set("fs.default.name", "hdfs://namenode01:9000");
		conf.set("yarn.resourcemanager.resource-tracker.address", "namenode01:8031");
		conf.set("yarn.resourcemanager.address", "namenode01:8032");
		conf.set("yarn.resourcemanager.scheduler.address", "namenode01:8030");
		conf.set("yarn.resourcemanager.admin.address", "namenode01:8033");
		conf.set("yarn.application.classpath", "$HADOOP_CONF_DIR," + "$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,"
				+ "$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*," + "$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,"
				+ "$YARN_HOME/*,$YARN_HOME/lib/*," + "$HBASE_HOME/*,$HBASE_HOME/lib/*,$HBASE_HOME/conf/*");
		conf.set("mapreduce.jobhistory.address", "namenode01:10020");
		conf.set("mapreduce.jobhistory.webapp.address", "namenode01:19888");
		conf.set("mapred.child.java.opts", "-Xmx256m");
		conf.set("user.name", "hadoop");
		conf.set("group.name", "hadoop");
		conf.set("hadoop.proxyuser.hadoop.groups", "hadoop");
		return conf;
	}

	// @Test
	public void mapperTest() throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = getConf();
		Job job = new Job(conf, "My Max Num");
		job.setJarByClass(HbaseMapreduceTest.class);
		job.setMapperClass(MyMapper.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setCombinerClass(MyCombiner.class);
		FileInputFormat.addInputPath(job, new Path("/tmp/test.log"));
		FileOutputFormat.setOutputPath(job, new Path("/tmp/output"));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	@Test
	public void copyData() throws IOException, InterruptedException, ClassNotFoundException {
		Configuration conf = searchService.getConfiguration();

		conf.set("mapreduce.framework.name", "yarn");
		conf.set("hbase.zookeeper.quorum", "zk01,zk02");
		conf.set("fs.default.name", "hdfs://namenode01:9000");
		conf.set("yarn.resourcemanager.resource-tracker.address", "namenode01:8031");
		conf.set("yarn.resourcemanager.address", "namenode01:8032");
		conf.set("yarn.resourcemanager.scheduler.address", "namenode01:8030");
		conf.set("yarn.resourcemanager.admin.address", "namenode01:8033");
		// conf.set("yarn.application.classpath", "$HADOOP_CONF_DIR," +
		// "$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,"
		// + "$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*," + "$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,"
		// + "$YARN_HOME/*,$YARN_HOME/lib/*," + "$HBASE_HOME/*,$HBASE_HOME/lib/*,$HBASE_HOME/conf/*");
		conf.set("mapreduce.jobhistory.address", "namenode01:10020");
		conf.set("mapreduce.jobhistory.webapp.address", "namenode01:19888");
		conf.set("mapred.child.java.opts", "-Xmx64m");
		conf.set("user.name", "hadoop");
		conf.set("group.name", "hadoop");
		conf.set("hadoop.proxyuser.hadoop.groups", "hadoop");

		Job job = new Job(conf, "ExampleReadWrite");
		job.setJarByClass(HbaseMapper.class); // class that contains mapper
		job.setUser("hadoop");
		Scan scan = new Scan();
		scan.setCaching(500); // 1 is the default in Scan, which will be bad for MapReduce jobs
		scan.setCacheBlocks(false); // don't set to true for MR jobs
		// set other scan attrs

		TableMapReduceUtil.initTableMapperJob("test", // input table
				scan, // Scan instance to control CF and attribute selection
				HbaseMapper.class, // mapper class
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
