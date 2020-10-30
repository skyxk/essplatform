package com.chen.service;

import com.chen.entity.PhoneLoginInfo;

public interface PhoneLoginInfoService {

	/**
	 * 根据personId获取用户
	 * @param personId
	 * @return
	 */
	public PhoneLoginInfo findByPersonId(String personId);

	/**
	 * 更改手机用户信息
	 */
	public Integer updatePhoneLoginRandom(PhoneLoginInfo info);

	/**
	 * 通过随机数查询用户信息
	 * @param random
	 * @return
	 */
	public PhoneLoginInfo queryByRandom(String random);
}
