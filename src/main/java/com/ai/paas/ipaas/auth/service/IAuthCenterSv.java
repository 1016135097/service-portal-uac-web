package com.ai.paas.ipaas.auth.service;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenter;
import com.ai.paas.ipaas.auth.service.dto.OperResult;

public interface IAuthCenterSv {
	/**
	 * 邮件激活
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult activeUser(AuthCenter authDes) throws PaasException;
	/**
	 * 服务审核通过写入认证中心
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult svsdk(AuthCenter authDes) throws PaasException;
	/**
	 * 用户注册成功写入认证中心
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult svweb(AuthCenter authDes) throws PaasException;
}
