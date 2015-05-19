package com.ai.paas.ipaas.auth.service.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.paas.ipaas.PaaSConstant;
import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.auth.constants.AuthConstants;
import com.ai.paas.ipaas.auth.constants.UserClientException;
import com.ai.paas.ipaas.auth.dao.interfaces.AuthCenterMapper;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenter;
import com.ai.paas.ipaas.auth.dao.mapper.bo.AuthCenterCriteria;
import com.ai.paas.ipaas.auth.service.IAuthCenterSv;
import com.ai.paas.ipaas.auth.service.dto.AuthDescriptor;
import com.ai.paas.ipaas.auth.service.dto.AuthResult;
import com.ai.paas.ipaas.auth.service.dto.ConfigInfo;
import com.ai.paas.ipaas.auth.service.dto.OperResult;
import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
@Transactional(rollbackFor = Exception.class)
@Service
public class AuthCenterSvImpl implements IAuthCenterSv {

	@Autowired
	private SqlSessionTemplate template;

	@Override
	public AuthResult authLogin(AuthDescriptor authDes) throws UserClientException  {
		AuthResult result = new AuthResult();
		try {
			String authUserName = authDes.getAuthUserName();
			String password = authDes.getPassword();
			// restful链接、用户编码、密码、账户来源
			AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();

			authCenterCriteria.createCriteria().andAuthUserNameEqualTo(authUserName).andAuthPasswordEqualTo(password);

			List<AuthCenter> authResults = mapper.selectByExample(authCenterCriteria);
			
			if (authResults.size() == 1) {
				
				AuthCenter authResult = authResults.get(0);
				result.setSuccessed(true);
				result.setUserId(authResult.getAuthUserId());
			}else{
				result.setSuccessed(false);
				result.setAuthMsg(AuthConstants.AUTH_FAILURE_MSG);
			}

		} catch (Exception e) {
			Log.error("authLogin:"+e.getMessage(), e);
			throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
		return result;
	}


	@Override
	public AuthResult authService(AuthDescriptor authDes) throws UserClientException {
		AuthResult result = new AuthResult();
		String userId = null;
		try {
			String authUserName = authDes.getAuthUserName();
			//根据用户名查询userId
			AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteriaUserId = new AuthCenterCriteria();
			authCenterCriteriaUserId.createCriteria().andAuthUserNameEqualTo(authUserName);
			List<AuthCenter> authResultsUserId = mapper.selectByExample(authCenterCriteriaUserId);
			if(authResultsUserId.size() > 0){
				userId = authResultsUserId.get(0).getAuthUserId();
			}
			if(userId != null){
				String password = authDes.getPassword();
				// restful链接、用户编码、密码、账户来源
				AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
				authCenterCriteria.createCriteria().andAuthUserNameEqualTo(authDes.getServiceId()).andAuthUserIdEqualTo(userId).andAuthPasswordEqualTo(password);
				List<AuthCenter> authResults = mapper.selectByExample(authCenterCriteria);
				
				if (authResults.size() == 1) {
					
					AuthCenter authResult = authResults.get(0);
					Gson son = new Gson();
					ConfigInfo info = son.fromJson(authResult.getAuthParam(), ConfigInfo.class);
					result.setSuccessed(true);
					result.setConfigAddr(info.getConfigAddr());
					result.setConfigPasswd(info.getConfigPwd());
					result.setConfigUser(info.getConfigUser());
					result.setUserId(userId);
					result.setUserName(authUserName);
				}else{
					result.setSuccessed(false);
					result.setAuthMsg(AuthConstants.AUTH_FAILURE_MSG);
				}
			}

		} catch (Exception e) {
			Log.error("authService:"+e.getMessage(), e);
			throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
		return result;
	}


	@Override
	public OperResult activeUser(AuthCenter authDes) throws UserClientException {
        OperResult res = new OperResult();
        try {
        	AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria().andAuthUserNameEqualTo(authDes.getAuthUserName());
			int activeResult = mapper.updateByExampleSelective(authDes, authCenterCriteria);
			if(activeResult > 0){
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
	            res.setResultMessage("activeUser Success");
			}else{
				res.setResultCode(AuthConstants.AuthResult.FAIL);
	            res.setResultMessage("activeUser fail");
			}
            
        } catch (UserClientException e) {
            res.setResultCode(AuthConstants.AuthResult.FAIL);
            res.setResultMessage(e.getMessage());
        }
        return res;
	}


	@Override
	public OperResult svsdk(AuthCenter authDes) throws PaasException {
		OperResult res = new OperResult();
        try {
        	AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			int svsdkResult = mapper.insert(authDes);
			if(svsdkResult > 0){
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
	            res.setResultMessage("svsdk Success");
			}else{
				res.setResultCode(AuthConstants.AuthResult.FAIL);
	            res.setResultMessage("svsdk fail");
			}
            
        } catch (UserClientException e) {
            res.setResultCode(AuthConstants.AuthResult.FAIL);
            res.setResultMessage(e.getMessage());
        }
        return res;
	}


	@Override
	public OperResult svweb(AuthCenter authCenter) throws PaasException {
		OperResult res = new OperResult();
        try {
        	AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
        	String[] names = authCenter.getAuthUserName().split("\\|");
        	String email = names[0];
        	String phone = names[1];
        	authCenter.setAuthUserName(email);
			int svwebResult1 = mapper.insert(authCenter);
			authCenter.setAuthUserName(phone);
			int svwebResult2 = mapper.insert(authCenter);
			if(svwebResult1 > 0 && svwebResult2 > 0){
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
	            res.setResultMessage("svweb Success");
			}else{
				throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "svweb fail");
			}
            
        } catch (UserClientException e) {
        	throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
        }
        return res;
	}
}
