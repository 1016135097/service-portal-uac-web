package com.ai.paas.ipaas.auth.controller;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.auth.constants.AuthConstants;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenter;
import com.ai.paas.ipaas.auth.service.IAuthCenterSv;
import com.ai.paas.ipaas.auth.service.dto.OperResult;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.CiperUtil;
import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/service")
public class ServiceController {

	@Autowired
	IAuthCenterSv authCenterSv;
	
	
	@RequestMapping(value = "/svsdk", produces = "text/html;charset=UTF-8")
	public @ResponseBody String svsdk(@RequestParam String authUserName,
													String authUserId,
													String authParam,
													String authPassword)  throws PaasException  {
		Gson son = new Gson();
		Assert.notNull(authUserName, "authUserName is null");
		Assert.notNull(authUserId, "authUserId is null");
		Assert.notNull(authParam, "authParam is null");
		Assert.notNull(authPassword, "authPassword is null");
		AuthCenter ac = new AuthCenter();
		ac.setAuthUserId(authUserId);
		ac.setAuthUserName(authUserName);
		ac.setAuthParam(authParam);
		ac.setAuthPassword(CiperUtil.encrypt(AuthConstants.SECURITY_KEY, authPassword));
		ac.setAuthRegisterTime(new Timestamp((new Date().getTime())));
		ac.setAuthSource(AuthConstants.AUTH_SDK_USER);
		ac.setAuthState(AuthConstants.AUTH_STATE_NOT_ACTIVED);
		OperResult operRes = authCenterSv.svsdk(ac);
		return son.toJson(operRes);
	}
	@RequestMapping(value = "/svweb", produces = "text/html;charset=UTF-8")
	public @ResponseBody String svweb(@RequestParam String authUserName,
													String authUserId,
													String authPassword)  throws PaasException  {
		Gson son = new Gson();
		Assert.notNull(authUserName, "authUserName is null");
		Assert.notNull(authUserId, "authUserId is null");
		Assert.notNull(authPassword, "authPassword is null");
		AuthCenter ac = new AuthCenter();
		ac.setAuthUserId(authUserId);
		ac.setAuthUserName(authUserName);
		ac.setAuthPassword(authPassword);
		ac.setAuthRegisterTime(new Timestamp((new Date().getTime())));
		ac.setAuthSource(AuthConstants.AUTH_WEB_USER);
		ac.setAuthState(AuthConstants.AUTH_STATE_NOT_ACTIVED);
		OperResult operRes = authCenterSv.svweb(ac);
		return son.toJson(operRes);
	}
	@RequestMapping(value = "/queryUserIdByUserName", produces = "text/html;charset=UTF-8")
	public @ResponseBody String queryUserIdByUserName(@RequestParam String userName)  throws PaasException  {
		Assert.notNull(userName, "userName is null");
		return authCenterSv.queryUserIdByUserName(userName);
	}
	@RequestMapping(value = "/modifyServPwd", produces = "text/html;charset=UTF-8")
	public @ResponseBody String modifyServPwd(@RequestParam String newPwd, String oldPwd, String serviceId, String userId)  throws PaasException  {
		Assert.notNull(newPwd, "newPwd is null");
		Assert.notNull(newPwd, "oldPwd is null");
		Assert.notNull(newPwd, "serviceId is null");
		Assert.notNull(newPwd, "userId is null");
		return authCenterSv.modifyServPwd(newPwd, oldPwd, serviceId, userId);
	}
	@RequestMapping(value = "/verfiy_email", produces = "text/html;charset=UTF-8")
	public @ResponseBody String activeUser(@RequestParam String token) throws PaasException  {
		Gson son = new Gson();
		Assert.notNull(token, "authUserName is null");
		AuthCenter ac = new AuthCenter();
		ac.setAuthUserName(CiperUtil.decrypt(AuthConstants.SECURITY_KEY, token));
		ac.setAuthState(AuthConstants.AUTH_STATE_ACTIVED);
		ac.setAuthActiveTime(new Timestamp((new Date().getTime())));
		OperResult operRes = authCenterSv.activeUser(ac);
		return son.toJson(operRes);
	}
	

}
