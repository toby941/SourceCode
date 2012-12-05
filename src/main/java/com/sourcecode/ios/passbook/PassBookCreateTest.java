package com.sourcecode.ios.passbook;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKLocation;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.passes.PKCoupon;
import de.brendamour.jpasskit.passes.PKEventTicket;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.passes.PKStoreCard;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import de.brendamour.jpasskit.signing.PKSigningUtil;

public class PassBookCreateTest {

    public static void zipPass(PKPass pass) throws Exception {
        String passKey = "D:\\workspace\\emms_maven\\src\\main\\webapp\\WEB-INF\\config\\passbookkey\\pass.p12";
        String password = "sonic333";
        String appleFile =
                "D:\\workspace\\emms_maven\\src\\main\\webapp\\WEB-INF\\config\\passbookkey\\AppleWWDRCA.pem";
        String pathToTemplateDirectory = "/passbooktmp/";
        PKSigningInformation pkSigningInformation =
                PKSigningUtil.loadSigningInformationFromPKCS12FileAndIntermediateCertificateFile(passKey, password,
                        appleFile);
        byte[] passZipAsByteArray =
                PKSigningUtil.createSignedAndZippedPkPassArchive(pass, pathToTemplateDirectory, pkSigningInformation);
        FileUtils.forceDelete(new File("D:\\workspace\\emms_maven\\src\\main\\webapp\\WEB-INF\\passbook\\new.pkpass"));
        FileUtils.writeByteArrayToFile(new File(
                "D:\\workspace\\emms_maven\\src\\main\\webapp\\WEB-INF\\passbook\\new.pkpass"), passZipAsByteArray);
    }

    @Test
    public void createEvent() throws Exception {
        PKPass pass = new PKPass();
        PKEventTicket genericCard = new PKEventTicket();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey("Test Date");
        balanceField.setLabel("有效期");
        balanceField.setValue("2013-11-23");

        List<PKField> seondFields = new ArrayList<PKField>();
        PKField seondField = new PKField();
        seondField.setKey("Test Dates");
        seondField.setLabel("卡号");
        seondField.setValue("NO. 000888");
        seondFields.add(seondField);

        primaryFields.add(balanceField);
        primaryFields.add(seondField);
        // genericCard.setPrimaryFields(primaryFields);
        genericCard.setSecondaryFields(primaryFields);
        // genericCard.setAuxiliaryFields(seondFields);

        List<PKField> backFields = new ArrayList<PKField>();
        PKField backField = new PKField();
        backField.setKey("Test Date");
        backField.setLabel("活动规则");
        backField
                .setValue("活动时间：2012年11月20日——11月23日\r\n活动内容：下载活动奖券即可参与抽奖，有机会获得价值158元的喜多屋畅吃美食劵一张，开奖时间：12月23日中午12:00\r\n获奖人数：5名\r\n本次活动仅限南京纬思武德投资实业有限公司员工参加。");
        PKField airField = new PKField();
        airField.setKey("airad");
        airField.setLabel("制作");
        airField.setValue("大气创媒 http://www.airad.com");
        // backFields.add(backField);
        backFields.add(airField);
        genericCard.setBackFields(backFields);

        PKBarcode barcode = new PKBarcode();
        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("http://emms.airad.com/passbook/down/t/dejil.pkpass");
        barcode.setMessageEncoding(Charset.forName("utf-8"));
        pass.setBarcode(barcode);

        Date d = Calendar.getInstance().getTime();
        Date d2 = DateUtils.addHours(d, 3);
        pass.setRelevantDate(d2);

        pass.setLocations(getLocations());

        pass.setFormatVersion(1);
        pass.setDescription("*Event");
        pass.setOrganizationName("德基广场欢迎您");
        pass.setPassTypeIdentifier("pass.com.toby.push");
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setSerialNumber("deji002");

        pass.setLogoText("德基会员卡");
        pass.setEventTicket(genericCard);
        pass.setBackgroundColorAsObject(Color.RED);
        pass.setForegroundColorAsObject(Color.black);
        pass.setLabelColorAsObject(Color.DARK_GRAY);

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        // pass.setWebServiceURL(new URL("https://emms.airad.com/passapi/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.247:8860/passapi/"));
        pass.setAuthenticationToken("s1jpIl6nsdqDI2LAskBz5w==");
        zipPass(pass);
    }

    // @Test
    public void createEventWin() throws Exception {
        PKPass pass = new PKPass();
        PKBarcode barcode = new PKBarcode();
        PKEventTicket genericCard = new PKEventTicket();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey("Test Date");
        balanceField.setLabel("Date 2012-11-16");
        balanceField.setValue("凯润中奖啦");
        balanceField.setChangeMessage("Changed to %@");

        primaryFields.add(balanceField);

        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("ABCDEFG");
        barcode.setMessageEncoding(Charset.forName("utf-8"));

        genericCard.setPrimaryFields(primaryFields);

        Date d = Calendar.getInstance().getTime();
        Date d2 = DateUtils.addHours(d, 1);
        pass.setRelevantDate(d2);

        // pass.setLocations(getLocations());

        pass.setFormatVersion(1);
        pass.setDescription("*Event");
        pass.setOrganizationName("恭喜您中奖了");
        pass.setPassTypeIdentifier("pass.com.airad.toby");
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setSerialNumber("kairuntest");
        pass.setBarcode(barcode);
        pass.setLogoText("event 大范围");
        pass.setEventTicket(genericCard);
        pass.setBackgroundColorAsObject(Color.BLACK);
        pass.setForegroundColor("rgb(255,255,255 )");

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        // pass.setWebServiceURL(new URL("https://emms.airad.com/passapi/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.247:8860/passapi/"));
        pass.setAuthenticationToken("s1jpIl6nsdqDI2LAskBz5w==");

        zipPass(pass);
    }

    // @Test
    public void createGeneric() throws Exception {
        PKPass pass = new PKPass();
        PKBarcode barcode = new PKBarcode();
        PKGenericPass genericCard = new PKGenericPass();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey("Test Date");
        balanceField.setLabel("Date 2012-11-17");
        balanceField.setValue("Generic");

        primaryFields.add(balanceField);

        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("ABCDEFG");
        barcode.setMessageEncoding(Charset.forName("utf-8"));

        genericCard.setPrimaryFields(primaryFields);

        Date d = Calendar.getInstance().getTime();
        Date d2 = DateUtils.addHours(d, 4);
        // pass.setRelevantDate(d2);

        pass.setLocations(getLocations());

        pass.setFormatVersion(1);
        pass.setDescription("Generic");
        pass.setOrganizationName("Generic小范围");
        pass.setPassTypeIdentifier("pass.com.airad.toby");
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setSerialNumber("000000007");
        pass.setBarcode(barcode);
        pass.setLogoText("Generic小范围");
        pass.setGeneric(genericCard);
        pass.setBackgroundColorAsObject(Color.BLACK);
        pass.setForegroundColor("rgb(255,255,255 )");

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        // pass.setWebServiceURL(new URL("https://emms.airad.com/passapi/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.247:8860/passapi/"));
        pass.setAuthenticationToken("s1jpIl6nsdqDI2LAskBz5w==");

        zipPass(pass);
    }

    // @Test
    public void createCoupon() throws Exception {
        PKPass pass = new PKPass();
        PKBarcode barcode = new PKBarcode();
        PKCoupon couponCard = new PKCoupon();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey("Test");
        balanceField.setLabel("location");
        balanceField.setValue(20.0);
        balanceField.setCurrencyCode("EUR");

        primaryFields.add(balanceField);

        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("ABCDEFG");
        barcode.setMessageEncoding(Charset.forName("utf-8"));

        couponCard.setPrimaryFields(primaryFields);

        pass.setLocations(getLocations());
        Date d = Calendar.getInstance().getTime();
        Date d2 = DateUtils.addHours(d, 1);
        pass.setRelevantDate(d2);

        pass.setFormatVersion(1);
        pass.setDescription("couponCard");
        pass.setOrganizationName("OrgName");
        pass.setPassTypeIdentifier("pass.com.airad.toby");
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setSerialNumber("000000002");
        pass.setBarcode(barcode);
        pass.setLogoText("192.168.1.13");
        pass.setCoupon(couponCard);
        pass.setBackgroundColorAsObject(Color.BLACK);
        pass.setForegroundColor("rgb(255,255,255 )");

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        // pass.setWebServiceURL(new URL("https://emms.airad.com/passapi/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.247:8860/passapi/"));
        pass.setAuthenticationToken("s1jpIl6nsdqDI2LAskBz5w==");

        zipPass(pass);
    }

    // @Test
    public void create() throws Exception {
        PKPass pass = new PKPass();
        PKBarcode barcode = new PKBarcode();
        PKStoreCard storeCard = new PKStoreCard();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey("Test");
        balanceField.setLabel("location");
        balanceField.setValue(20.0);
        balanceField.setCurrencyCode("EUR");

        primaryFields.add(balanceField);

        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("ABCDEFG");
        barcode.setMessageEncoding(Charset.forName("utf-8"));

        storeCard.setPrimaryFields(primaryFields);

        pass.setLocations(getLocations());

        pass.setFormatVersion(1);
        pass.setPassTypeIdentifier("pass.com.airad.toby");
        pass.setSerialNumber("000000002");
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setBarcode(barcode);
        pass.setOrganizationName("OrgName");
        pass.setLogoText("192.168.1.13");
        pass.setStoreCard(storeCard);
        pass.setDescription("192.168.1.13");
        pass.setBackgroundColorAsObject(Color.BLACK);
        pass.setForegroundColor("rgb(255,255,255 )");

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        // pass.setWebServiceURL(new URL("http://emms.airad.com/passapi/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.247:8860/passapi/"));
        pass.setAuthenticationToken("D9BB5A2D-9BDA-4E01-9480-604F235BCDF");

        zipPass(pass);
    }

    public static void createWitharg(String text, Double lat, Double lon, String serNo, String outName)
            throws Exception {
        PKPass pass = new PKPass();
        PKBarcode barcode = new PKBarcode();
        PKStoreCard storeCard = new PKStoreCard();

        List<PKField> primaryFields = new ArrayList<PKField>();

        PKField balanceField = new PKField();
        balanceField.setKey(text);
        balanceField.setLabel("test");
        balanceField.setValue(20.0);
        balanceField.setCurrencyCode("EUR");

        primaryFields.add(balanceField);

        barcode.setFormat(PKBarcodeFormat.PKBarcodeFormatQR);
        barcode.setMessage("ABCDEFG");
        barcode.setMessageEncoding(Charset.forName("utf-8"));

        storeCard.setPrimaryFields(primaryFields);

        List<PKLocation> locationList = new ArrayList<PKLocation>();
        Double[] ds = new Double[]{};
        PKLocation l1 = new PKLocation();
        l1.setLatitude(lat); // 维度
        l1.setLongitude(lon);// 经度
        l1.setRelevantText(text);
        locationList.add(l1);

        pass.setLocations(locationList);

        pass.setFormatVersion(1);
        pass.setPassTypeIdentifier("pass.com.airad.toby");
        pass.setSerialNumber(serNo);
        pass.setTeamIdentifier("QBHE7996Q5");
        pass.setBarcode(barcode);
        pass.setOrganizationName("OrgName");
        pass.setLogoText(text);
        pass.setStoreCard(storeCard);
        pass.setDescription(text);
        pass.setBackgroundColorAsObject(Color.BLACK);
        pass.setForegroundColor("rgb(255,255,255 )");

        // pass.setWebServiceURL(new URL("https://api.passtools.com/apple/"));
        // pass.setWebServiceURL(new URL("http://192.168.1.13:9091/passapi/"));
        pass.setWebServiceURL(new URL("https://emms.airad.com/passapi/"));
        pass.setAuthenticationToken("D9BB5A2D-9BDA-4E01-9480-604F235BCDFF");

        zipPass(pass);
    }

    /**
     * 大阪府大阪市北区天満4-14-19 34.69537, 135.51481 <br/>
     * 大阪府大阪市北区芝田１丁目１−２ 34.70531, 135.49857 <br/>
     * 京都府京都市右京区西院追分町２５−１ 34.99764, 135.72735 <br/>
     * 河原町通三条下ル山崎町２５１ 35.00701, 135.76898 <br/>
     * 東京都渋谷区渋谷1-7-3 35.66098, 139.70655
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        createWitharg("大阪府大阪市北区天満4-14-19", 34.69537, 135.51481, "0000000008", "r1.pkpass");
        createWitharg("大阪府大阪市北区芝田１丁目１−２", 34.70531, 135.49857, "0000000009", "r2.pkpass");
        createWitharg("京都府京都市右京区西院追分町２５−１", 34.99764, 135.72735, "00000000010", "r3.pkpass");
        createWitharg("河原町通三条下ル山崎町２５１", 35.00701, 135.76898, "00000000011", "r4.pkpass");
        createWitharg("東京都渋谷区渋谷1-7-3", 35.66098, 139.70655, "00000000012", "r5.pkpass");
    }

    /**
     * 智慧谷中心 32.076716,118.748142 <br/>
     * 金城路 32.077275,118.747289 <br/>
     * 智慧谷大门 32.076375,118.747814 <br/>
     * 苏友宾馆 32.075497,118.748978 <br/>
     * ios定位点 32.076784,118.748517 <br/>
     * 金城路下坡 32.075816,118.749837 <br/>
     * ios定位点2 32.076861,118.748463 <br/>
     * ios定位点3 32.076693,118.748485 <br/>
     * ios定位点4 32.076902,118.74819 <br/>
     * ios定位点5 32.076666,118.748313<br/>
     * 苏友宾馆南面空地 32.075425,118.749601 <br/>
     * 苏友宾馆南面空地偏东 32.074975,118.750051 <br/>
     * 丁山公寓楼南 32.076452,118.74915 <br/>
     * 公司南阳台 32.076675,118.748399 <br/>
     * 公司停车场 32.077057,118.747804 <br/>
     * 公司大楼前 32.076647,118.747943 <br/>
     * 公司大楼前2 32.076579,118.748222 <br/>
     * 山坡 32.076538,118.748356 <br/>
     * 山坡2 32.07632,118.748474 <br/>
     * 山坡3 32.076202,118.748506 <br/>
     * fix2 118.74267778567503 32.078606898476124 公司地址纠偏后
     * 
     * @return
     */
    private List<PKLocation> getLocations() {
        List<PKLocation> locationList = new ArrayList<PKLocation>();
        Double[] ds = new Double[]{};
        PKLocation l1 = new PKLocation();
        // l1.setLatitude(32.078606); // 维度
        // l1.setLongitude(118.742677);// 经度
        // l1.setRelevantText("智慧谷");
        l1.setLatitude(32.078606); // 维度
        l1.setLongitude(118.742677);// 经度
        l1.setRelevantText("公司地址纠偏后 ");

        locationList.add(l1);
        return locationList;
    }

}
