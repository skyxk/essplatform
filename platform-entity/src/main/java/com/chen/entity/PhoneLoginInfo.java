package com.chen.entity;

/**
 * 手机号密码登录
 * 2018.05.22
 * @author pjx
 *
 */
public class PhoneLoginInfo {

	// 人员id
	private String personId;
	
	// 手机号
	private String account_num;
	
	// 密码
	private String psw;

	//随机数
	private String random;

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getAccount_num() {
		return account_num;
	}

	public void setAccount_num(String account_num) {
		this.account_num = account_num;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}
}
