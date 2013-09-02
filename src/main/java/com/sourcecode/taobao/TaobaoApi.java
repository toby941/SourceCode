package com.sourcecode.taobao;


//import com.taobao.api.ApiException;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoClient;
//import com.taobao.api.domain.Shop;
//import com.taobao.api.request.ShopGetRequest;
//import com.taobao.api.request.ShopcatsListGetRequest;
//import com.taobao.api.request.TopatsItemcatsGetRequest;
//import com.taobao.api.request.TopatsResultGetRequest;
//import com.taobao.api.response.ShopGetResponse;
//import com.taobao.api.response.ShopcatsListGetResponse;
//import com.taobao.api.response.TopatsItemcatsGetResponse;
//import com.taobao.api.response.TopatsResultGetResponse;

public class TaobaoApi {
	// public static void main(String[] args) throws Exception {
	//
	// getJsonToSql();
	// }
	//
	// static String sql =
	// "insert into MINCOW_SHOP_CATS (CID, PARENT_CID, NAME, IS_PARENT, STATUS, SORT_ORDER, ADD_TIME,CAT_TYPE) values ({0}, 0, {1}, 0 , \"1\", 1,now(),1);";
	//
	// public static void getJsonToSql() throws Exception {
	// String jsonStr = FileUtils.readFileToString(new File("D:/tmp/json.txt"),
	// "UTF-8");
	// JSONArray jsonArray = JSONArray.fromObject(jsonStr);
	// for (int i = 0; i < jsonArray.size(); i++) {
	// JSONObject obj = (JSONObject) jsonArray.get(i);
	// String cid = obj.getString("cid");
	// String name = obj.getString("name");
	// System.out.println(MessageFormat.format(sql, cid, "'" + name + "'"));
	// }
	//
	// }
	//
	// public static void getItemcats() throws ApiException {
	// TaobaoClient client = getClient();
	// TopatsItemcatsGetRequest req = new TopatsItemcatsGetRequest();
	// req.setCids("0");
	// req.setOutputFormat("json");
	// // req.setType(1L);
	// TopatsItemcatsGetResponse response = client.execute(req);
	// System.out.println(ReflectionToStringBuilder.toString(response));
	// }
	//
	// public static void getTaskResult() throws ApiException {
	// TaobaoClient client = getClient();
	// TopatsResultGetRequest req = new TopatsResultGetRequest();
	// req.setTaskId(40573391L);
	// TopatsResultGetResponse response = client.execute(req);
	// System.out.println(ReflectionToStringBuilder.toString(response));
	// }
	//
	// public static TaobaoClient getClient() {
	// return new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest",
	// "21114646",
	// "e107350218b7e5c0c1c8517f064b3899");
	// }
	//
	// public static void testgetUser() throws ApiException {
	// ShopGetRequest req = new ShopGetRequest();
	// req.setFields("id,memberid,sid,cid,nick,title,desc,bulletin,pic_path,created,modified,shop_score");
	// req.setNick("jessicay28");
	// ShopGetResponse response = getClient().execute(req);
	// Shop shop = response.getShop();
	// System.out.println(ReflectionToStringBuilder.toString(shop));
	// }
	//
	// public static void testShopcatsList() throws ApiException {
	// ShopcatsListGetRequest req = new ShopcatsListGetRequest();
	// req.setFields("cid,parent_cid,name,is_parent");
	// ShopcatsListGetResponse response = getClient().execute(req);
	// System.out.println(ReflectionToStringBuilder.toString(response));
	// }
}
