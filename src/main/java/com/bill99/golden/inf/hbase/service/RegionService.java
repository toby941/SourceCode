package com.bill99.golden.inf.hbase.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HServerLoad;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bill99.golden.inf.hbase.domain.Region;
import com.bill99.golden.inf.hbase.domain.Table;
import com.bill99.golden.inf.hbase.util.HBaseUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author jun.bao
 * @since 2013年12月13日
 */
@Service
public class RegionService {
	@Autowired
	private Configuration configuration;

	private final static Logger log = Logger.getLogger(RegionService.class);

	private final static String CACHE_KEY_SERVER_NAMES = "servernames";
	private final static String CACHE_KEY_TABLE_INFO = "tableinfo";
	private final static String CACHE_KEY_TABLE_DESCRIPTOR = "tabledescriptor";

	private Cache<String, Object> cache;

	@PostConstruct
	public void init() {
		// 定义一个20分钟的缓存，减少频繁请求页面加载时间
		cache = CacheBuilder.newBuilder().concurrencyLevel(4).expireAfterWrite(20, TimeUnit.MINUTES).build();
	}

	@PreDestroy
	public void destory() {
		cache.invalidateAll();
		cache.cleanUp();
		cache = null;
	}

	public void cleanCache() {
		cache.invalidateAll();
	}

	public void cleanCache(String key) {
		if (StringUtils.isNotBlank(key)) {
			cache.invalidate(key);
		}
	}

	/**
	 * 获取当前集群server信息
	 * @return
	 * @throws IOException
	 */
	public List<String> getServerNames() throws IOException {

		Object o = cache.getIfPresent(CACHE_KEY_SERVER_NAMES);
		if (o != null) {
			return (List<String>) o;
		}

		HBaseAdmin admin = null;
		try {
			admin = getHBaseAdmin();
			ClusterStatus clusterStatus = admin.getClusterStatus();
			Collection<ServerName> serverNames = clusterStatus.getServers();
			List<String> names = new ArrayList<String>();
			for (ServerName serverName : serverNames) {
				names.add(serverName.getHostname());
			}

			Collections.sort(names, new Comparator<String>() {
				@Override
				public int compare(String t1, String t2) {
					return t1.compareTo(t2);
				}
			});

			cache.put(CACHE_KEY_SERVER_NAMES, names);
			return names;
		} finally {
			HBaseUtils.closeQuietly(admin);
		}
	}

	private static final Pattern tableNamePattern = Pattern.compile("([^,]*).*");

	public HBaseAdmin getHBaseAdmin() throws MasterNotRunningException, ZooKeeperConnectionException {
		return new HBaseAdmin(configuration);
	}

	private static String masterName = null;

	/**
	 * 获取jmx访问的url与端口
	 * @return
	 */
	public String getBaseUrl() {
		String hostName = getMasterName();
		return String.format("http://%s:60010/", hostName);
	}

	/**
	 * 返回hbase的master域名
	 * @return
	 */
	public String getMasterName() {
		if (StringUtils.isNotBlank(masterName)) {
			return masterName;
		}
		HBaseAdmin admin = null;
		try {
			admin = getHBaseAdmin();
			ClusterStatus clusterStatus = admin.getClusterStatus();
			String name = clusterStatus.getMaster().getHostname();
			masterName = name;
			return masterName;
		} catch (Exception e) {
			log.error("getMasterName error", e);
			return "unknowmaster";
		} finally {
			HBaseUtils.closeQuietly(admin);

		}
	}

	/**
	 * 按datanode获取所有表信息,包括表名，region数量，所占大小(MB)
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Table>> getTableInfo() throws IOException {

		Object o = cache.getIfPresent(CACHE_KEY_TABLE_INFO);
		if (o != null) {
			return (Map<String, List<Table>>) o;
		}

		HBaseAdmin admin = null;
		try {
			admin = getHBaseAdmin();
			ClusterStatus clusterStatus = admin.getClusterStatus();
			Collection<ServerName> serverNames = clusterStatus.getServers();
			if (serverNames == null) {
				return null;
			}
			Map<String, List<Table>> serverMap = new HashMap<String, List<Table>>();
			for (ServerName sn : serverNames) {
				String serverNameStr = sn.getHostname();
				HServerLoad serverLoad = clusterStatus.getLoad(sn);
				Map<String, Table> map = new HashMap<String, Table>();
				for (Map.Entry<byte[], HServerLoad.RegionLoad> entry : serverLoad.getRegionsLoad().entrySet()) {
					final HServerLoad.RegionLoad regionLoad = entry.getValue();
					String name = regionLoad.getNameAsString();
					Matcher matcher = tableNamePattern.matcher(name);
					if (matcher.matches()) {
						String tableName = matcher.group(1);
						Table t = map.get(tableName);
						if (t == null) {
							t = new Table(tableName, serverNameStr, regionLoad.getStorefileSizeMB());
							HTableDescriptor hds = getHTableDescriptor(tableName);
							if (hds != null) {
								t.setSplitPolicy(hds.getRegionSplitPolicyClassName());
							}
							map.put(tableName, t);
						} else {
							t.add(regionLoad.getStorefileSizeMB());
						}
					}
				}
				List<Table> list = new ArrayList<Table>();
				Set<String> keySet = map.keySet();
				for (String s : keySet) {
					list.add(map.get(s));
				}
				Collections.sort(list, new Comparator<Table>() {
					@Override
					public int compare(Table t1, Table t2) {
						return t1.getTableName().compareTo(t2.getTableName());
					}

				});
				serverMap.put(serverNameStr, list);
			}

			cache.put(CACHE_KEY_TABLE_INFO, serverMap);
			return serverMap;
		} finally {
			HBaseUtils.closeQuietly(admin);
		}
	}

	public HTableDescriptor getHTableDescriptor(String tableName) {
		HTableDescriptor[] tables = getTableDescriptors();
		for (HTableDescriptor h : tables) {
			String name = h.getNameAsString();
			if (tableName.equals(name)) {
				return h;
			}
		}
		return null;
	}

	public HTableDescriptor[] getTableDescriptors() {
		Object o = cache.getIfPresent(CACHE_KEY_TABLE_DESCRIPTOR);
		if (o != null) {
			return (HTableDescriptor[]) o;
		}
		HBaseAdmin admin = null;
		HTableDescriptor[] tableDescriptors = new HTableDescriptor[] {};
		try {
			admin = getHBaseAdmin();
			tableDescriptors = admin.listTables();
			cache.put(CACHE_KEY_TABLE_DESCRIPTOR, tableDescriptors);
			return tableDescriptors;
		} catch (Exception e) {
			log.error("getTableNamses", e);
			return tableDescriptors;
		} finally {
			HBaseUtils.closeQuietly(admin);
		}
	}

	public List<String> getTableNames() {
		HBaseAdmin admin = null;
		List<String> tableNames = new ArrayList<String>();
		try {
			admin = getHBaseAdmin();
			HTableDescriptor[] tableDescriptors = getTableDescriptors();
			for (HTableDescriptor tableDescriptor : tableDescriptors) {
				tableNames.add(tableDescriptor.getNameAsString());
			}
			return tableNames;
		} catch (Exception e) {
			log.error("getTableNamses", e);
			return tableNames;
		} finally {
			HBaseUtils.closeQuietly(admin);
		}

	}

	public List<Region> getRegionInfo(String tableName) {
		String key = tableName;
		Object o = cache.getIfPresent(key);
		if (o != null) {
			return (List<Region>) o;
		}

		HBaseAdmin admin = null;
		try {
			admin = getHBaseAdmin();
			ClusterStatus clusterStatus = admin.getClusterStatus();
			Collection<ServerName> serverNames = clusterStatus.getServers();

			List<Region> list = new ArrayList<Region>();
			for (ServerName sn : serverNames) {
				final HServerLoad serverLoad = clusterStatus.getLoad(sn);
				for (Map.Entry<byte[], HServerLoad.RegionLoad> entry : serverLoad.getRegionsLoad().entrySet()) {
					final HServerLoad.RegionLoad regionLoad = entry.getValue();
					String name = regionLoad.getNameAsString();
					if (name.indexOf(tableName) > -1) {
						Region r = new Region(tableName, sn.getHostname(), sn.getPort(), regionLoad);
						list.add(r);
					}
				}
			}
			Collections.sort(list, new Comparator<Region>() {
				@Override
				public int compare(Region r1, Region r2) {
					return -(r1.getSize() - r2.getSize());
				}
			});
			cache.put(key, list);
			return list;
		} catch (Exception e) {
			log.error("getRegionInfo :" + tableName, e);
			return null;
		} finally {
			HBaseUtils.closeQuietly(admin);
		}

	}

	public List<Region> getRegionInfo(String tableName, String nodeName) throws IOException {

		List<Region> regions = getRegionInfo(tableName);
		if (regions == null) {
			return null;
		}
		List<Region> returnList = new ArrayList<Region>();
		for (Region r : regions) {
			if (r.getHostName().equals(nodeName)) {
				returnList.add(r);
			}
		}
		return returnList;

	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
