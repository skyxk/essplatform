package com.chen.service.impl;


import com.chen.dao.PhoneLoginInfoMapper;
import com.chen.entity.PhoneLoginInfo;
import com.chen.service.PhoneLoginInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PhoneLoginInfoServiceImpl implements PhoneLoginInfoService {

	@Autowired
	private PhoneLoginInfoMapper phoneLoginInfoMapper;

	/**
	 * 通过personId进行手机用户查询
	 * @param personId
	 * @return
	 */
	@Override
	public PhoneLoginInfo findByPersonId(String personId) {
		PhoneLoginInfo info = phoneLoginInfoMapper.findByPersonId(personId);
//		info.setAccount_num(info.getAccount_num());
//		info.setPsw(info.getPsw());
		return info;
	}


	/**
	 * 更改随机数信息
	 * @param info
	 * @return
	 */
	@Override
	public Integer updatePhoneLoginRandom(PhoneLoginInfo info) {
		return phoneLoginInfoMapper.updatePhoneLoginRandom(info);
	}

	/**
	 * 通过随机数进行用户查询
	 * @param random
	 * @return
	 */
	public PhoneLoginInfo queryByRandom(String random){
		return phoneLoginInfoMapper.queryByRandom(random);
	}
}
