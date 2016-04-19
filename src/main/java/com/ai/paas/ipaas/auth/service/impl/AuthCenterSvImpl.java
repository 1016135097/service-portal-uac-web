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
	public AuthResult authLogin(AuthDescriptor authDes)
			throws UserClientException {
		AuthResult result = new AuthResult();
		try {
			String authUserName = authDes.getAuthUserName();
			String password = authDes.getPassword();
			// restful link, userId, password, account
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();

			authCenterCriteria.createCriteria()
					.andAuthUserNameEqualTo(authUserName)
					.andAuthPasswordEqualTo(password);

			List<AuthCenter> authResults = mapper
					.selectByExample(authCenterCriteria);

			if (authResults.size() == 1) {
				AuthCenter authResult = authResults.get(0);
				result.setSuccessed(true);
				result.setUserId(authResult.getAuthUserId());
			} else {
				result.setSuccessed(false);
				result.setAuthMsg(AuthConstants.AUTH_FAILURE_MSG);
			}
		} catch (Exception e) {
			Log.error("authLogin:" + e.getMessage(), e);
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}

		return result;
	}

	@Override
	public AuthResult authService(AuthDescriptor authDes)
			throws UserClientException {
		AuthResult result = new AuthResult();
		String userId = null;

		try {
			String authUserName = authDes.getAuthUserName();
			// to query userId
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteriaUserId = new AuthCenterCriteria();
			authCenterCriteriaUserId.createCriteria().andAuthUserNameEqualTo(
					authUserName);
			List<AuthCenter> authResultsUserId = mapper
					.selectByExample(authCenterCriteriaUserId);
			if (authResultsUserId.size() > 0) {
				userId = authResultsUserId.get(0).getAuthUserId();
			}
			if (userId != null) {
				String password = authDes.getPassword();
				// restful链接、用户编码、密码、账户来源
				AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
				authCenterCriteria.createCriteria()
						.andAuthUserNameEqualTo(authDes.getServiceId())
						.andAuthUserIdEqualTo(userId)
						.andAuthPasswordEqualTo(password);
				List<AuthCenter> authResults = mapper
						.selectByExample(authCenterCriteria);

				if (authResults.size() == 1) {

					AuthCenter authResult = authResults.get(0);
					Gson son = new Gson();
					ConfigInfo info = son.fromJson(authResult.getAuthParam(),
							ConfigInfo.class);
					result.setSuccessed(true);
					result.setConfigAddr(info.getConfigAddr());
					result.setConfigPasswd(info.getConfigPwd());
					result.setConfigUser(info.getConfigUser());
					result.setUserId(userId);
					result.setUserName(authUserName);
				} else {
					result.setSuccessed(false);
					result.setAuthMsg(AuthConstants.AUTH_FAILURE_MSG);
				}
			}

		} catch (Exception e) {
			Log.error("authService:" + e.getMessage(), e);
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
		return result;
	}

	@Override
	public OperResult activeUser(AuthCenter authDes) throws UserClientException {
		OperResult res = new OperResult();
		try {
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria().andAuthUserNameEqualTo(
					authDes.getAuthUserName());
			int activeResult = mapper.updateByExampleSelective(authDes,
					authCenterCriteria);
			if (activeResult > 0) {
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
				res.setResultMessage("activeUser Success");
			} else {
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
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria()
					.andAuthUserIdEqualTo(authDes.getAuthUserId())
					.andAuthUserNameEqualTo(authDes.getAuthUserName());
			List<AuthCenter> authResults = mapper
					.selectByExample(authCenterCriteria);
			if (authResults.size() > 0) {
				res.setResultCode(AuthConstants.AuthResult.SUCCESS);
				res.setResultMessage("svsdk Success");
			} else {
				int svsdkResult = mapper.insert(authDes);
				if (svsdkResult > 0) {
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
					res.setResultMessage("svsdk Success");
				} else {
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
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			String[] names = authCenter.getAuthUserName().split("\\|");
			String email = names[0];
			String phone = names[1];
			authCenter.setAuthUserName(email);
			int svwebResult1 = mapper.insert(authCenter);
			if (email.indexOf(AuthConstants.INNER_EMAIL_SUFFIX) > -1) {
				if (svwebResult1 > 0 || "18610176415".equals(phone)) {
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
					res.setResultMessage("svweb Success");
				} else {
					throw new UserClientException(
							PaaSConstant.ExceptionCode.SYSTEM_ERROR,
							"svweb fail");
				}
			} else {
				authCenter.setAuthUserName(phone);
				int svwebResult2 = mapper.insert(authCenter);
				if (svwebResult1 > 0 && svwebResult2 > 0) {
					res.setResultCode(AuthConstants.AuthResult.SUCCESS);
					res.setResultMessage("svweb Success");
				} else {
					throw new UserClientException(
							PaaSConstant.ExceptionCode.SYSTEM_ERROR,
							"svweb fail");
				}
			}

		} catch (UserClientException e) {
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
		return res;
	}

	@Override
	public String queryUserIdByUserName(String userName) throws PaasException {
		try {
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria()
					.andAuthUserNameEqualTo(userName);
			List<AuthCenter> authResults = mapper
					.selectByExample(authCenterCriteria);
			if (authResults.size() == 1) {
				AuthCenter authResult = authResults.get(0);
				return "{\"userId\":" + authResult.getAuthUserId() + "}";
			} else {
				throw new UserClientException(
						PaaSConstant.ExceptionCode.SYSTEM_ERROR,
						"you got too many results by this userName,please check your database!");
			}

		} catch (Exception e) {
			Log.error("queryUserIdByUserName:" + e.getMessage(), e);
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR, e);
		}
	}

	@Override
	public OperResult modifyServPwd(String newPwd, String oldPwd,
			String serviceId, String userId) throws PaasException {
		Log.info("begin to modifyServPwd ====");
		OperResult result = new OperResult();
		try {
			AuthCenter authCenter = new AuthCenter();
			authCenter.setAuthPassword(oldPwd);
			authCenter.setAuthUserId(userId);
			authCenter.setAuthUserName(serviceId);
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria()
					.andAuthUserNameEqualTo(serviceId)
					.andAuthPasswordEqualTo(oldPwd)
					.andAuthUserIdEqualTo(userId);
			List<AuthCenter> authResults = mapper
					.selectByExample(authCenterCriteria);
			if (authResults.size() == 1) {
				authCenter.setAuthPassword(newPwd);
				if (mapper.updateByExampleSelective(authCenter,
						authCenterCriteria) > 0) {
					result.setResultCode(AuthConstants.AuthResult.SUCCESS);
					return result;
				}
			} else {
				result.setResultCode(AuthConstants.AuthResult.FAIL);
				result.setResultMessage("oldPwd or serviceId not correct!");
			}

		} catch (Exception e) {
			Log.error(e.getMessage());
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR,
					"modifyServPwd error you stupid!");
		}
		return result;
	}

	@Override
	public OperResult modifyAccount(String mail, String userId)
			throws PaasException {
		Log.info("begin to modifyAccount ====");
		OperResult result = new OperResult();
		try {
			AuthCenter authCenter = new AuthCenter();
			authCenter.setAuthUserId(userId);
			authCenter.setAuthSource(AuthConstants.AUTH_WEB_USER);
			authCenter.setAuthUserName(mail);
			AuthCenterMapper mapper = template
					.getMapper(AuthCenterMapper.class);
			AuthCenterCriteria authCenterCriteria = new AuthCenterCriteria();
			authCenterCriteria.createCriteria()
					.andAuthSourceEqualTo(AuthConstants.AUTH_WEB_USER)
					.andAuthUserIdEqualTo(userId);
			if (mapper.updateByExampleSelective(authCenter, authCenterCriteria) > 0) {
				result.setResultCode(AuthConstants.AuthResult.SUCCESS);
				return result;
			} else {
				result.setResultCode(AuthConstants.AuthResult.FAIL);
				result.setResultMessage("modifyAccount error you stupid!!");
			}

		} catch (Exception e) {
			Log.error(e.getMessage());
			throw new UserClientException(
					PaaSConstant.ExceptionCode.SYSTEM_ERROR,
					"modifyAccount error you stupid!");
		}
		return result;
	}

}
