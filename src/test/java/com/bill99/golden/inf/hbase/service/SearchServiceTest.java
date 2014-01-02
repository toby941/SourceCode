package com.bill99.golden.inf.hbase.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bill99.golden.inf.hbase.query.QueryResult;

/**
 * @author jun.bao
 * @since 2013年12月30日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context/*.xml")
public class SearchServiceTest {

	@Autowired
	private SearchService searchService;

	// @Test
	public void testGet() throws IOException {
		String table = "test";
		String row = "0toby1031931315895345152";
		Date d1 = new Date();
		Result t = searchService.get(table, row);
		Date d2 = new Date();
		System.out.println("query time: " + (d2.getTime() - d1.getTime()));
		System.out.println(Bytes.toString(t.raw()[0].getValue()));
		System.out.println(Bytes.toString(t.getRow()));
	}

	@Test
	public void testScan() throws IOException {
		String prefix = null;
		ResultScanner rs = null;
		HTableInterface table = null;
		List<QueryResult> qrList = null;
		long startTime = System.currentTimeMillis();
		System.out.println(new Date(1388538155415L));
		System.out.println(new Date(1388538755415L));
		String startRowKey = "minute#1#1#移通#1388145000000";
		String endRowKey = "minute#1#1#移通#1388145600000";
		try {
			Scan scan = new Scan();
			List<Filter> list = new ArrayList<Filter>();
			prefix = startRowKey.substring(0, startRowKey.indexOf("#") + 1);
			startRowKey = startRowKey.substring(startRowKey.indexOf("#") + 1);
			endRowKey = endRowKey.substring(endRowKey.indexOf("#") + 1);
			System.out.println("startRowKey: " + startRowKey);
			System.out.println("endRowKey: " + endRowKey);
			list.add(new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(endRowKey))));
			list.add(new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes
					.toBytes(startRowKey))));
			Filter filter = new FilterList(FilterList.Operator.MUST_PASS_ALL, list);
			scan.setFilter(filter);
			table = new HTable(searchService.getConfiguration(), "PRISM_MINUTE");
			System.out.println("Get HTableInterface time: " + (System.currentTimeMillis() - startTime));
			rs = table.getScanner(scan);
			System.out.println("end to rs: " + (System.currentTimeMillis() - startTime));
			qrList = new ArrayList<QueryResult>();
			for (Result r = rs.next(); r != null; r = rs.next()) {
				qrList.add(new QueryResult(prefix + Bytes.toString(r.getRow())));
				// Bytes.toString(r.getValue(FAMILY, QUALIFIER))
			}
			System.out.println(qrList.size());
		} finally {
			rs.close();
			table.close();
		}
		System.out.println("Complete to query: " + (System.currentTimeMillis() - startTime));
	}

	// @Test
	public void byteTest() throws NoSuchAlgorithmException {
		// long
		//
		long l = 1234567890L;
		byte[] lb = Bytes.toBytes(l);
		System.out.println("long bytes length: " + lb.length); // returns 8

		String s = "" + l;
		byte[] sb = Bytes.toBytes(s);
		System.out.println("long as string length: " + sb.length); // returns 10

		// hash
		//
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(Bytes.toBytes(s));
		System.out.println("md5 digest bytes length: " + digest.length); // returns 16

		String sDigest = new String(digest);
		byte[] sbDigest = Bytes.toBytes(sDigest);
		System.out.println("md5 digest as string length: " + sbDigest.length); // returns 26(译者注：实测值为22)
	}

}
