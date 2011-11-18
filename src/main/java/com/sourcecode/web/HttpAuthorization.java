/*
 * Copyright 2011 MITIAN Technology, Co., Ltd. All rights reserved.
 */
package com.sourcecode.web;

/**
 * HttpAuthorization.java
 * 
 * @author baojun
 */
public class HttpAuthorization {

    /**
     * 
     */
    public HttpAuthorization() {
        // @RequestMapping(params = "action=changeAdParam")
        // public ModelAndView changeAdParam(HttpServletRequest request, HttpServletResponse response) {
        // String authorization = request.getHeader("Authorization");
        // if (!AuthUtils.isPassBasicAuth(authorization, "admin", "123")) {
        // response.setHeader("WWW-Authenticate", "BASIC   realm=\"后台管理\"");
        // response.setStatus(401);
        // return null;
        // }
        // String adId = request.getParameter("adId");
        // String budgetDay = request.getParameter("budgetDay");
        // String budgetTotal = request.getParameter("budgetTotal");
        // if (adId != null && budgetDay != null && budgetTotal != null) {
        // CoreAd ad = new CoreAd();
        // ad.setAdId(Integer.parseInt(adId));
        // ad.setBudgetDay(new BigDecimal(budgetDay));
        // ad.setBudgetTotal(new BigDecimal(budgetTotal));
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // try {
        // Date date = sdf.parse("2011-09-01 00:00:00");
        // ad.setStartTime(date);
        // }
        // catch (ParseException e) {
        // e.printStackTrace();
        // }
        // int isOK = coreAdDAO.updateByPrimaryKeySelective(ad);
        // if (isOK == 1) {
        // request.setAttribute("msg", "修改成功!");
        // }
        // }
        // ModelAndView mv = new ModelAndView("test/adParamsChangeFor06");
        // return mv;
        // }
    }

}
