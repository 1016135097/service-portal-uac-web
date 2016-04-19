package com.ai.paas.ipaas.auth.service;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenter;
import com.ai.paas.ipaas.auth.service.dto.AuthDescriptor;
import com.ai.paas.ipaas.auth.service.dto.AuthResult;
import com.ai.paas.ipaas.auth.service.dto.OperResult;

public interface IAuthCenterSv {
	AuthResult authLogin(AuthDescriptor authDes) throws PaasException;

	AuthResult authService(AuthDescriptor authDes) throws PaasException;

	/**
	 * 邮件激活
	 * 
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult activeUser(AuthCenter authDes) throws PaasException;

	/**
	 * 服务审核通过写入认证中心
	 * 
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult svsdk(AuthCenter authDes) throws PaasException;

	/**
	 * 用户注册成功写入认证中心
	 * 
	 * @param authDes
	 * @return
	 * @throws PaasException
	 */
	OperResult svweb(AuthCenter authDes) throws PaasException;

	/**
	 * 根据用户名获取用户id
	 * 
	 * @param userName
	 * @return
	 * @throws PaasException
	 */
	String queryUserIdByUserName(String userName) throws PaasException;

	/**
	 * 修改服务密码
	 * 
	 * @return
	 * @throws PaasException
	 */
	OperResult modifyServPwd(String newPwd, String oldPwd, String serviceId,
			String userId) throws PaasException;

	/**
	 * 账户置换
	 * 
	 * @return
	 * @throws PaasException
	 */
	OperResult modifyAccount(String mail, String userId) throws PaasException;
}
