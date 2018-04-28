package com.tfyh.controller;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.tfyh.util.ShaUtil;
/**
 * 
 * @author wangfei
 * qq��������¼
 */
@Controller
public class QqloginController {
	public static Logger logger = LoggerFactory.getLogger(QqloginController.class);
	
	@RequestMapping(value="/")
	@ResponseBody
	public String root(HttpServletRequest request){
		String result = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>qq login</title></head><body><a href='http://wangfeifei.free.ngrok.cc/authorizeUrl'>qq��������½</a><br><a href='http://wangfeifei.free.ngrok.cc/weixinlogin'>΢�ŵ�������½</a></body></html>";
		return result;
	}
	
	
	/**
	 * �ض���qq��Ȩҳ��
	 * @param request
	 * @return
	 * @throws QQConnectException 
	 */
	@RequestMapping(value="/authorizeUrl")
	public String authorizeUrl(HttpServletRequest request) throws QQConnectException{
		String authorizeUrl = new Oauth().getAuthorizeURL(request);
		return "redirect:"+authorizeUrl;
	}
	
	/**
	 * qq��Ȩ�ص�·��
	 * @param request
	 * @return
	 * @throws QQConnectException 
	 */
	@RequestMapping(value="/qqLoginCallback",method=RequestMethod.GET)
	@ResponseBody
	public String qqLoginCallback(HttpServletRequest request) throws QQConnectException{
		//ͨ���ص��е�code�õ�accessToken
		AccessToken accessTokenObj = new Oauth().getAccessTokenByRequest(request);
		String accessToken = accessTokenObj.getAccessToken();
		if(accessToken == null || "".equals(accessToken)){
			throw new QQConnectException("accessTokenΪ�գ���Ȩʧ��");
		}
		//ͨ��accessToken�õ�openId
		OpenID openIdObj = new OpenID(accessToken);
		if(openIdObj == null || "".equals(openIdObj.getUserOpenID())){
			throw new QQConnectException("openIdObjΪ�գ���Ȩʧ��");
		}
		String openId = openIdObj.getUserOpenID();
		//ͨ��accessToken��openId�õ��û���Ϣ
		UserInfo qzoneUserInfo = new UserInfo(accessToken, openId);
		UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
		if(userInfoBean == null || userInfoBean.getRet() != 0){
			throw new QQConnectException(userInfoBean.getMsg()+",��Ȩʧ��");
		}
		//�õ��û��ǳ�
		String nickname = userInfoBean.getNickname();
		String imgUrl = userInfoBean.getAvatar().getAvatarURL30();
		String result = "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>qq login</title></head><body>qq�ǳƣ�"+nickname+"<br>qqͷ��:<img src='"+imgUrl+"'/></body></html>";
		return result;
	}
	

}
