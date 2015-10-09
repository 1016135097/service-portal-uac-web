package com.ai.paas.ipaas.auth.constants;

public class AuthConstants extends RuntimeException{
	
	private static final long serialVersionUID = 1760256946303391094L;
	
	/**
	 * 注意常量命名规范
	 */
	public static final String AUTH_WEB_USER = "WEB";
	public static final String AUTH_SDK_USER = "SDK";
	public static final String INNER_EMAIL_SUFFIX = "asiainfo.com";
	/**
	 * 是什么SPLITER要
	 */
	public static final String SPLITER = "\\|";
	/**
	 * 下面三行用窦博的国际化
	 */
	public static final String AUTH_FAILURE_MSG = "username or password is wrong";
	public static final String SYSTEM_ERROR_MSG = "System error...";
	public static final String PARAM_ERROR_MSG = "param error...";
	
	
	
	public static final String SECURITY_KEY = "7331c9b6b1a1d521363f7bca8acb095f";// md5
	public static final String AUTH_STATE_NOT_ACTIVED = "1";
	public static final String AUTH_STATE_ACTIVED = "2";
	/**
	 * 认证返回结果
	 * @author mapl
	 */
	public static class AuthResult{
		public final static String SUCCESS = "000000"; //成功
		public final static String FAIL = "999999"; //失败
	}
}
