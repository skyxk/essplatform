package com.chen.dao;

import com.chen.entity.PhoneLoginInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneLoginInfoMapper {

	/**
	 * 通过personId进行用户查询
	 * @param personId
	 * @return
	 */
	public PhoneLoginInfo findByPersonId(String personId);

	/**
	 * 根据手机号和密码查询
	 * 2018.05.22
	 * @param phone
	 * @param psw
	 * @return
	 */
	public PhoneLoginInfo queryByPhoneAndPsw(String phone, String psw);
	
	/**
	 * 添加手机号密码登录信息对象
	 * 密码需要进行MD5加密
	 * 返回受影响的记录数
	 * 2018.05.24
	 * @param phoneLoginInfo
	 */
//	public Integer addPhoneLoginInfo(PhoneLoginInfo phoneLoginInfo);

	/**
	 * 修改info对象
	 * 增加随机数字段
	 * @param info
	 * @return
	 */
	public Integer updatePhoneLoginRandom(PhoneLoginInfo info);

	/**
	 * 通过随机数查询到用户信息
	 * @param random
	 * @return
	 */
	PhoneLoginInfo queryByRandom(String random);
}
