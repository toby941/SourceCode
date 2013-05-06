package com.yogapppackage;

import net.sf.json.JSONObject;

/**
 * @author xuanrui company
 * @parameter Map parameters from client
 * @return Map results to client 该接口定义客户端能够访问的方法
 */
public interface BasicAPI {
    public String TestInfoShow();

    /**
     * CEP活动的全集查询
     * 1.CEP活动列表查询--------------------------------------------------------------------------------------------
     * ---------------- <br/>
     * { "AllCepInfo": [ { "cepid": "某某活动1", "ceptitle":"活动标题1", "cepcontent":"活动内容的具体说明1", "ceptime":
     * "20130818090000","cepplace":"某地点1","joinnum":"100","signupnum":"20","commentnum":"10", commentdetail:[ {"cepid":
     * "某某活动1",
     * "commentid","某评论者ID1","picturehttp":"http://localhost:8083/XuanR_YogAppProject/commenter1.jpg","commentname"
     * :"JOHN","commenttime":"20130818090000","commentconent":"评论内容1" }, {"cepid": "某某活动1",
     * "commentid","某评论者ID2","picturehttp"
     * :"http://localhost:8083/XuanR_YogAppProject/commenter2.jpg","commentname":"ROSE"
     * ,"commenttime":"20130818090000","commentconent":"评论内容2" } ] }, { "cepid": "某某活动2", "ceptitle":"活动标题2",
     * "cepcontent":"活动内容的具体说明2", "ceptime":
     * "20130818090000","cepplace":"某地点2","joinnum":"200","signupnum":"20","commentnum":"10", commentdetail:[ {"cepid":
     * "某某活动2",
     * "commentid","某评论者ID3","picturehttp":"http://localhost:8083/XuanR_YogAppProject/commenter3.jpg","commentname"
     * :"JACK","commenttime":"20130818090000","commentconent":"评论内容1" }, {"cepid": "某某活动2",
     * "commentid","某评论者ID4","picturehttp"
     * :"http://localhost:8083/XuanR_YogAppProject/commenter4.jpg","commentname":"MARY"
     * ,"commenttime":"20130818090000","commentconent":"评论内容2" } ] } ]}
     * 
     * @return
     */
    public JSONObject SelectAllCepActive();

    /**
     * CEP单个活动详情查询<br/>
     * { "TheOneCepInfo": { "cepid": "某某活动1", "ceptitle":"活动标题1", "cepcontent":"活动内容的具体说明1", "ceptime":
     * "20130818090000","cepplace":"某地点1","joinnum":"200","signupnum":"20","commentnum":"10", commentdetail:[ {"cepid":
     * "某某活动1",
     * "commentid","某评论者ID1","picturehttp":"http://localhost:8083/XuanR_YogAppProject/commenter1.jpg","commentname"
     * :"JOHN","commenttime":"20130818090000","commentconent":"评论内容1" }, {"cepid": "某某活动1",
     * "commentid","某评论者ID2","picturehttp"
     * :"http://localhost:8083/XuanR_YogAppProject/commenter2.jpg","commentname":"ROSE"
     * ,"commenttime":"20130818090000","commentconent":"评论内容2" } ] } }
     * 
     * @param cepid
     * @return
     */
    public JSONObject SelectTheOneCepActive(String cepid);

    /**
     * CEP预约报名<br/>
     * 3.1 如果报名成功 { "PrecontractSignUp": { "sucessmark":"P1","sucesstext":"您已预约成功，请到XXX服务台确认，服务台地址：XXX，联系电话：XXX" } } <br/>
     * 3.2 如果报名不成功 { "PrecontractSignUp": { "sucessmark":"P0","sucesstext":"您未预约成功" } }
     * 
     * @param cepid
     * @param userid
     * @return
     */
    public JSONObject PrecontractSignUpCepActive(String cepid, String userid);

    /**
     * CEP预约报名取消<br/>
     * 4.1 如果取消成功 { "PrecontractCancel": { "sucessmark":"C1","sucesstext":"该活动取消成功" } } <br/>
     * 4.2 如果取消不成功 { "PrecontractCancel": { "sucessmark":"C0","sucesstext":"该活动取消不成功" } }
     * 
     * @param cepid
     * @param userid
     * @return
     */
    public JSONObject PrecontractCancelCepActive(String cepid, String userid);

    /**
     * Cep活动报名确认 5.1 如果报名确认通过 { "ConfirmCepActive": { "confirmmark":"Q1","confirmtext":"您预约的CEP活动获得批准" } }<br/>
     * 5.2 如果报名确认不通过 { "ConfirmCepActive": { "confirmmark":"Q0","confirmtext":"活动报名人数已满" } } <br/>
     * 5.3 如果报名确认中 { "ConfirmCepActive": { "confirmmark":"Q9","confirmtext":"预约活动正在批准中" } }
     * 
     * @param cepid
     * @param userid
     * @return
     */
    public JSONObject ConfirmCepActive(String cepid, String userid);

    /**
     * CEP报名活动查询<br/>
     * { "SelectConfirmCepActive":[ { "cepid": "某某活动1", "cepstate":"P1","ceptitle":"活动标题1",
     * "cepcontent":"活动内容的具体说明1","ceptime": "20130818090000","cepplace":"某地点1","signuptime":"20130818090000"}, {
     * "cepid": "某某活动2", "cepstate":"Q1","ceptitle":"活动标题2", "cepcontent":"活动内容的具体说明2","ceptime":
     * "20130818090000","cepplace":"某地点2","signuptime":"20130818090000"} ]}
     * 
     * @param userid
     * @return
     */
    public JSONObject SelectConfirmCepActive(String userid);

    /**
     * CEP活动评价<br/>
     * 7.1 活动评价成功 { "CepActiveComment": { "commentmark":"1","commenttext":"评论成功" } }<br/>
     * 7.2 活动评价不成功 { "CepActiveComment": { "commentmark":"0","commenttext":"评论不成功" } }
     * 
     * @param cepid
     * @param userid
     * @param commentcontent
     * @return
     */
    public JSONObject CepActiveComment(String cepid, String userid, String commentcontent);

    /**
     * CEP活动签到<br/>
     * 8.1 活动签到成功 { "SignInCepActive": { "signinmark":"S1","signintext":"活动签到成功" } }<br/>
     * 8.2 活动评价不成功 { "CepActiveComment": { "signinmark":"S0","signintext":"活动签到不成功" } }
     * 
     * @param twobarcode
     * @param userid
     * @param lng
     * @param lat
     * @return
     */
    public JSONObject SignInCepActive(String twobarcode, String userid, String lng, String lat);

}
