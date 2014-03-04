package com.bill99.golden.inf.mapreduce.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author jun.bao
 * @since 2014年1月6日
 */
public class LogAnalysiser extends Configured implements Tool {
	public static void main(String[] args) {
		try {
			int res;
			res = ToolRunner.run(new Configuration(), new LogAnalysiser(), args);
			System.exit(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int run(String[] args) throws Exception {
		if (args == null || args.length < 2) {
			System.out.println("need inputpath and outputpath");
			return 1;
		}
		String inputpath = args[0];
		String outputpath = args[1];
		String shortin = args[0];
		String shortout = args[1];
		if (shortin.indexOf(File.separator) >= 0)
			shortin = shortin.substring(shortin.lastIndexOf(File.separator));
		if (shortout.indexOf(File.separator) >= 0)
			shortout = shortout.substring(shortout.lastIndexOf(File.separator));
		SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
		shortout = new StringBuffer(shortout).append("-").append(formater.format(new Date())).toString();

		if (!shortin.startsWith("/"))
			shortin = "/" + shortin;
		if (!shortout.startsWith("/"))
			shortout = "/" + shortout;
		shortin = "/home/hadoop/dfs/" + shortin;
		shortout = "/home/hadoop/dfs/" + shortout;
		File inputdir = new File(inputpath);
		File outputdir = new File(outputpath);

		if (!inputdir.exists() || !inputdir.isDirectory()) {
			System.out.println("inputpath not exist or isn't dir!");
			return 0;
		}
		if (!outputdir.exists()) {
			new File(outputpath).mkdirs();
		}
		// 以下注释的是 hadoop 0.20.X 老版本的 Job 代码，在 hadoop0.23.X 新框架中已经大大简化
		// Configuration conf = getConf();
		// JobConf job = new JobConf(conf, LogAnalysiser.class);
		// JobConf conf = new JobConf(getConf(),LogAnalysiser.class);// 构建 Config
		// conf.setJarByClass(MapClass.class);
		// conf.setJarByClass(ReduceClass.class);
		// conf.setJarByClass(PartitionerClass.class);
		// conf.setJar("hadoopTest.jar");
		// job.setJar("hadoopTest.jar");

		// 以下是新的 hadoop 0.23.X Yarn 的 Job 代码

		Job job = new Job(getConf());
		job.setJarByClass(LogAnalysiser.class);
		job.setJobName("analysisjob");
		job.setOutputKeyClass(Text.class);// 输出的 key 类型，在 OutputFormat 会检查
		job.setOutputValueClass(IntWritable.class); // 输出的 value 类型，在 OutputFormat 会检查
		job.setJarByClass(LogAnalysiser.class);
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(ReduceClass.class);
		job.setReducerClass(ReduceClass.class);
		job.setPartitionerClass(PartitionerClass.class);
		job.setNumReduceTasks(2);// 强制需要有两个 Reduce 来分别处理流量和次数的统计
		FileInputFormat.setInputPaths(job, new Path(shortin));// hdfs 中的输入路径
		FileOutputFormat.setOutputPath(job, new Path(shortout));// hdfs 中输出路径

		Date startTime = new Date();
		System.out.println("Job started: " + startTime);
		job.waitForCompletion(true);
		Date end_time = new Date();
		System.out.println("Job ended: " + end_time);
		System.out.println("The job took " + (end_time.getTime() - startTime.getTime()) / 1000 + " seconds.");
		// 删除输入和输出的临时文件
		// fileSys.copyToLocalFile(new Path(shortout),new Path(outputpath));
		// fileSys.delete(new Path(shortin),true);
		// fileSys.delete(new Path(shortout),true);
		return 0;
	}

	public Configuration getConf() {
		Configuration conf = new Configuration();

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

}
