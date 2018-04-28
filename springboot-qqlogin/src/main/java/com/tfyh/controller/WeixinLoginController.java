package com.tfyh.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.MidiDevice.Info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qq.connect.QQConnectException;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.Response;
import com.qq.connect.utils.json.JSONException;
import com.qq.connect.utils.json.JSONObject;

@Controller
public class WeixinLoginController {
	Logger logger = LoggerFactory.getLogger(WeixinLoginController.class);
	
	@Value("${weixin.appid}")
	private String appId;
	
	@Value("${weixin.appsecret}")
	private String appSecret;
	
	@RequestMapping(value="/weixinlogin")
	public void goWeixinAuth(HttpServletResponse response) throws IOException{
		logger.info("-----����weixinlogin----");
		String redirect_url = "http://wangfeifei.free.ngrok.cc/weixinLoginCallback";
		String url = "https://open.weixin.qq.com/connect/qrconnect?"
				+ "appid="+appId+""
				+ "&redirect_uri="+redirect_url+""
				+ "&response_type=code"
				+ "&scope=snsapi_login"
				+ "&state=wangfeifei";
		response.sendRedirect(url);
	}
	
 @RequestMapping(value="/weixinLoginCallback")
 public String weixinLoginCallback(HttpServletRequest request) throws QQConnectException, JSONException{
	String code =  request.getParameter("code");
	String state =  request.getParameter("state");//�����state�����ϲ�����ȥ��state
	logger.info("-=-=����΢�Żص�����-=-=-������code={},state={}",code,state);
	if(code == null || !"wangfeifei".equals(state)){
		logger.info("��Ȩʧ�ܡ�����");
		return null;
	}
	//ͨ��code��ȡtoken  get��ʽ
	String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
			+ "appid="+appId+""
			+ "&secret="+appSecret+""
			+ "&code="+code+""
			+ "&grant_type=authorization_code";
	HttpClient httpClient = new HttpClient();
	Response response = httpClient.get(url);
	JSONObject jsonObject = response.asJSONObject();
	String access_token = jsonObject.getString("access_token");
	String openid = jsonObject.getString("openid");
	String refresh_token = jsonObject.getString("refresh_token");
	//ͨ��token����û���Ϣ
	String infoUrl = "https://api.weixin.qq.com/sns/userinfo?"
			+ "access_token="+access_token+""
			+ "&openid="+openid+"";
	Response infoResponse = httpClient.get(infoUrl);
	JSONObject infoObject = infoResponse.asJSONObject();
	String nickname = infoObject.getString("nickname");//�ǳ�
	String headimgurl = infoObject.getString("headimgurl");//ͷ��
	String unionid = infoObject.getString("unionid");//union
	 return "";
 }

}
