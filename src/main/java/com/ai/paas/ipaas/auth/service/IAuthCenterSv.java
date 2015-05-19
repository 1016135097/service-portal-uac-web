package com.ai.paas.ipaas.auth.service;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenter;
import com.ai.paas.ipaas.auth.service.dto.AuthDescriptor;
import com.ai.paas.ipaas.auth.service.dto.AuthResult;
import com.ai.paas.ipaas.auth.service.dto.OperResult;

public interface IAuthCenterSv {
	AuthResult authLogin(AuthDescriptor authDes) throws PaasException;
	AuthResult authService(AuthDescriptor authDes) throws PaasException;
	OperResult activeUser(AuthCenter authDes) throws PaasException;
	OperResult svsdk(AuthCenter authDes) throws PaasException;
	OperResult svweb(AuthCenter authDes) throws PaasException;
}
