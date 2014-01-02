package com.bill99.golden.inf.hbase.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.alibaba.fastjson.JSON;
import com.bill99.golden.inf.hbase.domain.PageResult;
import com.bill99.golden.inf.hbase.domain.Region;
import com.bill99.golden.inf.hbase.domain.Table;
import com.bill99.golden.inf.hbase.domain.zookeeper.Base;
import com.bill99.golden.inf.hbase.service.ConfigurationService;
import com.bill99.golden.inf.hbase.service.RegionService;
import com.bill99.golden.inf.hbase.service.SearchService;
import com.bill99.golden.inf.hbase.service.SplitService;
import com.bill99.golden.inf.hbase.service.ZooKeeperService;
import com.bill99.golden.inf.hbase.service.jmx.JMXHBaseAttrService;
import com.bill99.golden.inf.hbase.service.jmx.JMXMasterStatService;
import com.bill99.golden.inf.hbase.service.jmx.JMXRPCStatService;
import com.bill99.golden.inf.hbase.service.jmx.beans.attr.HBaseAttributeBean;
import com.bill99.golden.inf.hbase.service.jmx.beans.stat.MasterStat;
import com.bill99.golden.inf.hbase.service.jmx.beans.stat.RPCStat;
import com.bill99.golden.inf.hbase.util.StringUtil;

/**
 * @author jun.bao
 * @since 2013年12月13日
 */
@Controller
public class IndexController {

	@Autowired
	private RegionService regionService;

	@Autowired
	private ZooKeeperService zkService;
	@Autowired
	private SplitService splitService;

	@Autowired
	private JMXHBaseAttrService hBaseAttrService;
	@Autowired
	private JMXMasterStatService masterStatService;
	@Autowired
	private JMXRPCStatService rpcStatService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping("/hbase/index.do")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws IOException {

		List<String> serverNames = regionService.getServerNames();
		Map<String, List<Table>> map = regionService.getTableInfo();
		ModelAndView mv = new ModelAndView("hbase/index");
		mv.addObject("serverNames", serverNames);
		mv.addObject("tables", map);
		return mv;
	}

	/**
	 * 查看指定tabel在各个节点的region
	 * @param tableName
	 * @param nodeName
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hbase/regioninfo.do")
	public ModelAndView regionInfo(@RequestParam(value = "t", required = false) String tableName,
			@RequestParam(value = "n", required = false) String nodeName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("hbase/region");
		if (StringUtils.isNotBlank(tableName) && StringUtils.isNotBlank(nodeName)) {
			List<Region> list = regionService.getRegionInfo(tableName, nodeName);
			mv.addObject(PageResult.VAR_PAGE_DATA, list);
		}
		return mv;

	}

	/**
	 * 跳转hbase主键的监控页面跳板
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hbase/proxy.do")
	public ModelAndView proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("hbase/proxy");
		String masterHost = regionService.getMasterName();
		mv.addObject("master", masterHost);
		return mv;

	}

	/**
	 * zk服务监控页
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hbase/zk.do")
	public ModelAndView zk(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("hbase/zk");
		Base base = zkService.dumpZookeeperInfo();
		String regionserver = StringUtil.replaceSlashNToBr(base.getRegionServers());
		mv.addObject("base", base);
		mv.addObject("regionserver", regionserver);
		return mv;
	}

	/**
	 * 集群属性监控页
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hbase/attr.do")
	public ModelAndView arrt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("hbase/attr");

		HBaseAttributeBean[] attrArr = hBaseAttrService.genBeans().getBeans();
		mv.addObject("attr", attrArr[0]);

		MasterStat[] mstatsArr = masterStatService.genBeans().getBeans();
		mv.addObject("mstat", mstatsArr[0]);

		Map<String, String[]> map = configurationService.loadConfiguration();
		mv.addObject("conf", map);

		RPCStat[] rstatArr = rpcStatService.genBeans().getBeans();
		mv.addObject("rstat", rstatArr[0]);

		return mv;
	}

	/**
	 * 返回指定table的所有region list 在手工split操作使用
	 * @param tableName
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/hbase/regionlist.do", method = RequestMethod.GET)
	public ModelAndView getRegionInfo(@RequestParam(value = "tablename", required = false) String tableName,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("hbase/split_region");
		if (StringUtils.isNotBlank(tableName)) {
			List<Region> list = regionService.getRegionInfo(tableName);
			mv.addObject(PageResult.VAR_PAGE_DATA, list);
		}
		return mv;
	}

	/**
	 * 手工split 入口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/hbase/split.do", method = RequestMethod.GET)
	public ModelAndView split(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("hbase/split");
		List<String> tableName = regionService.getTableNames();
		mv.addObject("tableNames", tableName);

		return mv;
	}

	/**
	 * search 入口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/hbase/search.do", method = RequestMethod.GET)
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("hbase/search");
		List<String> tableName = regionService.getTableNames();
		mv.addObject("tableNames", tableName);
		return mv;
	}

	@RequestMapping(value = "/hbase/tabledesc.do", method = RequestMethod.GET)
	public void getTableDescriptor(@RequestParam(value = "tablename", required = false) String tableName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (StringUtils.isBlank(tableName)) {
			return;
		}
		HTableDescriptor descriptor = regionService.getHTableDescriptor(tableName);
		if (descriptor != null) {
			response.getWriter().println(descriptor.toString());
		}
	}

	@RequestMapping(value = "/hbase/dosearch.do", method = RequestMethod.GET)
	public void doSearch(@RequestParam(value = "tablename", required = false) String tableName,
			@RequestParam(value = "rowkey", required = false) String rowkey, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (StringUtils.isBlank(tableName) || StringUtils.isBlank(rowkey)) {
			return;
		}
		Result r = searchService.get(tableName, rowkey);
		if (r != null) {
			String key = Bytes.toString(r.getRow());
			KeyValue[] kv = r.raw();
			if (kv != null && kv.length > 0) {

				String value = Bytes.toString(r.raw()[0].getValue());
				Map<String, String> map = new HashMap<String, String>();
				map.put(key, value);
				String result = JSON.toJSONString(map);
				response.getWriter().println(result);
				return;
			}
		}
		response.getWriter().println(String.format("table: %s, rowkey: %s  not found", tableName, rowkey));

	}

	@RequestMapping(value = "/hbase/dosplit.do", method = RequestMethod.POST)
	public ModelAndView dosplit(@RequestParam(value = "tablename", required = false) String tableName,
			@RequestParam(value = "region", required = false) String regionName,
			@RequestParam(value = "splitpoint", required = false) String splitPoint, HttpServletRequest request,
			HttpServletResponse response) {

		if (StringUtils.isBlank(tableName) && StringUtils.isBlank(regionName)) {
			return new ModelAndView(new InternalResourceView(request.getContextPath() + "/hbase/split.do"));
		}

		if (StringUtils.isNotBlank(regionName)) {
			splitService.split(regionName, splitPoint);
		} else {
			splitService.split(tableName, splitPoint);
		}
		regionService.cleanCache(tableName);

		ModelAndView mv = new ModelAndView("hbase/split_done");
		return mv;
	}

	/**
	 * 清理hbase查询缓存
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/hbase/clean.do")
	public ModelAndView clean(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mv = new ModelAndView("hbase/clean");
		regionService.cleanCache();
		return mv;
	}

	@RequestMapping("/hbase/config.do")
	public void queryConfig(@RequestParam(value = "key", required = false) String key, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (StringUtils.isNotBlank(key)) {
			String value = configurationService.getConfig(key);
			if (StringUtils.isNotBlank(value)) {
				response.getWriter().println(value);
			}
		}
	}
}
