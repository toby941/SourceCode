package com.bill99.golden.inf.hbase.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jun.bao
 * @since 2013年12月25日
 */
/**
 * 查询配置文件参数
 * @author jun.bao
 */
@Service
public class ConfigurationService {

	@Autowired
	private Configuration configuration;

	private static final String[][] CONFIG_KEYS = new String[][] {
			{ "zookeeper.session.timeout", "Rs与zk的timeout时间" },
			{ "hbase.regionserver.handler.count", "RegionServers受理的RPCServer实例数量 决定了用户表接受外来请求的线程数。默认为10" },
			{ "hbase.hregion.max.filesize", "最大HStoreFile大小。若某个列族的HStoreFile增长达到这个值，这个Hegion会被切割成两个。 默认: 10G." },
			{ "fs.default.name", "namenode的配置" },
			{ "dfs.replication", "数据需要备份的数量，默认是三" },
			{ "hadoop.tmp.dir", "Hadoop的默认临时路径" },
			{ "mapred.child.java.opts", "java虚拟机的参数配置" },
			{ "dfs.block.size", "block的大小，单位字节，必须是512的倍数，因为采用crc作文件完整性交验，默认配置512是checksum的最小单元" },
			{ "hbase.hregion.memstore.flush.size", "当单个Region内所有的memstore大小总和超过指定值时，flush该region的所有memstore" },
			{ "hbase.regionserver.global.memstore.upperLimit",
					"当ReigonServer内所有region的memstores所占用内存总和达到heap的40%时，HBase会强制block所有的更新并flush这些region以释放所有memstore占用的内存" },
			{
					"hbase.regionserver.global.memstore.lowerLimit",
					"同upperLimit，只不过lowerLimit在所有region的memstores所占用内存达到Heap的35%时，不flush所有的memstore。它会找一个memstore内存占用最大的region，做个别flush，此时写更新还是会被block。lowerLimit算是一个在所有region强制flush导致性能降低前的补救措施。" },
			{ "hfile.block.cache.size", "默认值：0.2,storefile的读缓存占用Heap的大小百分比，0.2表示20%。该值直接影响数据读的性能。" },
			{ "hbase.hstore.blockingStoreFiles",
					"默认值：7,在flush时，当一个region中的Store（CoulmnFamily）内有超过7个storefile时，则block所有的写请求进行compaction，以减少storefile数量。" },
			{ "hbase.hstore.compactionThreshold",
					"默认 3 当一个HStore含有多于这个值的HStoreFiles(每一个memstore flush产生一个HStoreFile)的时候，会执行一个合并操作，把这HStoreFiles写成一个。这个值越大，需要合并的时间就越长。" },
			{ "hbase.hregion.memstore.block.multiplier",
					"默认值：2,当一个region里的memstore占用内存大小超过hbase.hregion.memstore.flush.size两倍的大小时，block该region的所有请求，进行flush，释放内存。" },
			{ "hbase.hregion.memstore.mslab.enabled", "默认值：true,减少因内存碎片导致的Full GC，提高整体性能。" },
			{ "fs.local.block.size", "写入的数据达到这个大小时，会触发namenode执行fsync()操作,每次发生这个操作时，都会造成读响应的变慢" },
			{ "dfs.datanode.max.xcievers", "一个 Hadoop HDFS Datanode 有一个同时处理文件的上限" },
			{ "hbase.client.write.buffer", "HTable客户端的写缓冲的默认大小 设置大些会减少client和server端见的RPC次数，但相应的每次通信的数据量要增加。" },
			{ "hbase.rootdir", "region server的共享目录，用来持久化HBase" },
			{ "hbase.master.port", "HBase的Master的端口. 默认: 60000" },
			{ "hbase.cluster.distributed",
					"HBase的运行模式。false是单机模式，true是分布式模式。若为false,HBase和Zookeeper会运行在同一个JVM里面 默认:false" },
			{ "hbase.master.info.port", "HBase Master web 界面端口. 设置为-1 意味着你不想让他运行。 默认: 60010" },
			{ "hbase.master.info.bindAddress", "HBase Master web 界面绑定的端口 默认: 0.0.0.0" },
			{ "hbase.regionserver.port", "HBase RegionServer绑定的端口 60020" },
			{ "hbase.regionserver.info.port",
					"HBase RegionServer web 界面绑定的端口 设置为 -1 意味这你不想与运行 RegionServer 界面.默认: 60030" },
			{ "hbase.regionserver.info.bindAddress", "HBase RegionServer web 界面的IP地址 默认: 0.0.0.0" },
			{ "hbase.client.pause", "默认: 1000 通常的客户端暂停时间。最多的用法是客户端在重试前的等待时间。比如失败的get操作和region查询操作等都很可能用到。" },
			{ "hbase.client.retries.number",
					"默认: 10 最大重试次数。所有需重试操作的最大值。例如从root region服务器获取root ,region，Get单元值，行Update操作等等。这是最大重试错误的值。 Default: 10." },
			{ "hbase.bulkload.retries.number", "默认: 0,最大重试次数。 原子批加载尝试的迭代最大次数。 0 永不放弃." },
			{
					"hbase.client.scanner.caching",
					"默认: 100当调用Scanner的next方法，而值又不在缓存里的时候，从服务端一次获取的行数。越大的值意味着Scanner会快一些，但是会占用更多的内存。当缓冲被占满的时候，next方法调用会越来越慢。慢到一定程度，可能会导致超时。例如超过了hbase.regionserver.lease.period。" },
			{
					"hbase.client.keyvalue.maxsize",
					"默认:10M一个KeyValue实例的最大size.这个是用来设置存储文件中的单个entry的大小上界。因为一个KeyValue是不能分割的，所以可以避免因为数据过大导致region不可分割。明智的做法是把它设为可以被最大region size整除的数。如果设置为0或者更小，就会禁用这个检查。默认10MB。" },
			{ "hbase.regionserver.lease.period",
					"默认: 60000客户端租用HRegion server 期限，即超时阀值。单位是毫秒。默认情况下，客户端必须在这个时间内发一条信息，否则视为死掉。" },
			{ "hbase.regionserver.regionSplitLimit",
					"region的数量到了这个值后就不会在分裂了。这不是一个region数量的硬性限制。但是起到了一定指导性的作用，到了这个值就该停止分裂了。默认是MAX_INT.就是说不阻止分裂。" },
			{ "hbase.balancer.period", "默认: 300000 Master执行region balancer的间隔。" },
			{ "hbase.zookeeper.quorum", "Zookeeper集群的地址列表" }

	};

	public Map<String, String[]> loadConfiguration() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (String[] keys : CONFIG_KEYS) {
			String value = configuration.get(keys[0]);
			if (StringUtils.isBlank(value)) {
				value = StringUtils.EMPTY;
			}
			map.put(keys[0], new String[] { value, keys[1] });
		}
		return map;
	}

	public String getConfig(String key) {
		if (StringUtils.isBlank(key)) {
			return StringUtils.EMPTY;
		}

		String value = configuration.get(key.trim());
		if (StringUtils.isNotBlank(value)) {
			return value;
		} else {
			return StringUtils.EMPTY;
		}
	}

}
