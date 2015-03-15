package com.yueme.values;

public class ConstantValues {
	/**
	 * 登陆
	 */
	public static final int LOGIN = 1;
	/**
	 * 注册
	 */
	public static final int REGISTER = 2;
	/**
	 * 得到首页信息
	 */
	public static final int GET_HOME_PAGE_INFO = 3;
	/**
	 * 添加信息
	 */
	public static final int ADD_INFO = 4;
	/**
	 * 获取用户头像url
	 */
	public static final int GET_USER_HEAD_IMG = 5;
	/**
	 * 得到WEB-INF下面的图片
	 */
	public static final int GET_IMAGE = 6;
	/**
	 * 保存用户信息
	 */
	public static final int SAVE_USER_INFO = 7;
	/**
	 * 得到用户所有信息
	 */
	public static final int GET_USER_ALL_INFO = 8;
	/**
	 * 得到首页信息的图片
	 */
	public static final int GET_INFO_HEAD_IMG = 9;
	/**
	 * 得到参加某个活动的所有参加者
	 */
	public static final int GET_PARTICIPANTS = 10;
	/**
	 * 添加参加活动的用户
	 */
	public static final int ADD_PARTICIPANTS = 11;
	/**
	 * 主机URL
	 */
	
	
//	public static final String HOST = "http://192.168.253.1/yueme";
	public static final String HOST = "http://182.92.239.129/yueme";
	/**
	 * 发送请求的地址
	 */
	public static final String DISPATCHING_URL = HOST+"/servlet/MainController";
	/**
	 * 发送请求的参数
	 */
	public static final String REQUESTPARAM = "requestCode";
	/**
	 * 上传头像的地址
	 */
	public static final String UPLOAD_HEAD_IMG = HOST+"/servlet/UploadHeadImgServlet";
	/**
	 * 得到用户是否已经参加某个活动
	 */
	public static final int GET_USER_IS_PARTICIPATED = 12;
	/**
	 * 取消参加活动
	 */
	public static final int DELETE_PARTICIPANTS = 13;
	/**
	 * 得到某个用户参加的所有活动
	 */
	public static final int GET_USER_ALL_PARTICIPATED_INFO = 14;
	
	/**
	 * 是否登录SharedPreferences标记
	 */
	public static final String IS_LOGINED="IS_LOGINED";
}
