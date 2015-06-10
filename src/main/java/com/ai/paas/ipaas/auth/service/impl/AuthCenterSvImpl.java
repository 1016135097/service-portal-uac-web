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
import com.ai.paas.ipaas.auth.service.dto.OperResult;
import com.esotericsoftware.minlog.Log;
@Transactional(rollbackFor = Exception.class)
@Service
public class AuthCenterSvImpl implements IAuthCenterSv {

	@Autowired
	private SqlSessionTemplate template;

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
        	AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
        	authCenterCriteria.createCriteria().andAuthUserIdEqualTo(authDes.getAuthUserId()).andAuthUserNameEqualTo(authDes.getAuthUserName());
			List<AuthCenter> authResults = mapper.selectByExample(authCenterCriteria);
			if(authResults.size() > 0){
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
	            res.setResultMessage("svsdk Success");
			}else{
				int svsdkResult = mapper.insert(authDes);
				if(svsdkResult > 0){
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
		            res.setResultMessage("svsdk Success");
				}else{
					res.setResultCode(AuthConstants.AuthResult.FAIL);
		            res.setResultMessage("svsdk fail");
				}
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
			if(email.indexOf(AuthConstants.INNER_EMAIL_SUFFIX) > -1){
				if(svwebResult1 > 0 ){
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
		            res.setResultMessage("svweb Success");
				}else{
					throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "svweb fail");
				}
			}else{
				authCenter.setAuthUserName(phone);
				int svwebResult2 = mapper.insert(authCenter);
				if(svwebResult1 > 0 && svwebResult2 > 0){
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
		            res.setResultMessage("svweb Success");
				}else{
					throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "svweb fail");
				}
			}
			
            
        } catch (UserClientException e) {
        	throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
        }
        return res;
	}


	@Override
	public String queryUserIdByUserName(String userName) throws PaasException {
		try {
			AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria().andAuthUserNameEqualTo(userName);
			List<AuthCenter> authResults = mapper.selectByExample(authCenterCriteria);
			if (authResults.size() == 1) {
				AuthCenter authResult = authResults.get(0);
				return "{\"userId\":"+authResult.getAuthUserId()+"}";
			}else{
				throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "you got too many results by this userName,please check your database!");
			}

		} catch (Exception e) {
			Log.error("queryUserIdByUserName:"+e.getMessage(), e);
			throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
	}


	@Override
	public String modifyServPwd(String newPwd, String oldPwd, String serviceId,
			String userId) throws PaasException {
		Log.info("begin to modifyServPwd ====");
		try {
			AuthCenter authCenter = new AuthCenter();
			authCenter.setAuthPassword(oldPwd);
			authCenter.setAuthUserId(userId);
			authCenter.setAuthUserName(serviceId);
			AuthCenterMapper mapper = template.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			List<AuthCenter> authResults = mapper.selectByExample(authCenterCriteria);
			if (authResults.size() == 1) {
				authCenter.setAuthPassword(newPwd);
				if(mapper.updateByExampleSelective(authCenter, authCenterCriteria) > 0){
					return "{\"resultCode\":\"000000\"}";
				}
			}else{
				throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "your oldPwd or serviceId or userId is not correct you stupid!");
			}
			
			
		} catch (Exception e) {
			Log.error(e.getMessage());
			throw new UserClientException(PaaSConstant.ExceptionCode.SYSTEM_ERROR, "modifyServPwd error you stupid!");
		}
		return "{\"resultCode\":\"999999\"}";
	}
}
