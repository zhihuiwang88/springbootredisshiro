package com.zhy.springbootshirorsa.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhy.springbootshirorsa.dao.SysUserDao;
import com.zhy.springbootshirorsa.pojo.SysUser;

@Service
public class SysUserService {

	@Autowired
	private SysUserDao sysUserDao;
	
	/**
	 * 查询出所有数据
	 * @return
	 */
	public List<SysUser> find() {
		return sysUserDao.findAll();
	}

	public SysUser findByUserName(String userName) {
		return sysUserDao.findByUserName(userName);
	}

	@Transactional
	public int addUser(SysUser sysUser) throws IOException {
		SysUser user = new SysUser();
		user.setUserName(sysUser.getUserName());
		// 将密码转ByteSource对象
		ByteSource  source = ByteSource.Util.bytes(sysUser.getPassword().getBytes());
		// 随机加盐
		long random = System.currentTimeMillis();
		 ByteArrayOutputStream bao = new ByteArrayOutputStream();
	     DataOutputStream dos =new DataOutputStream(bao);
	     dos.writeLong(random);
	     byte[] byteSalt =bao.toByteArray();
		// 密码盐值统一加密，数据库保存密文
		SimpleHash simpleHash = new SimpleHash("MD5", source, byteSalt);
		user.setPassword(simpleHash.toHex());
		user.setSalt(String.valueOf(simpleHash));
		return sysUserDao.addUser(user);
	}
	
	
	public static void main(String[] args) throws IOException {
		String salt = "rsa_shiro";
		// 生成随机数，并转为Byte数组
		 long random = System.currentTimeMillis();
		 ByteArrayOutputStream bao = new ByteArrayOutputStream();
	     DataOutputStream dos =new DataOutputStream(bao);
	     dos.writeLong(random);
	     byte[] byteSalt =bao.toByteArray();
	    // 新盐值
	     String newSalt = salt + random;
	     String newSalt2 = salt + byteSalt;
		 String password = "123";
		// 将密码转ByteSource对象
		ByteSource source = ByteSource.Util.bytes(password.getBytes());
		/*
		 * 密码盐值统一加密
		 * (1成功) SimpleHash simpleHash = new SimpleHash("MD5", source,byteSalt);
		 * (2成功) SimpleHash simpleHash = new SimpleHash("MD5", source,newSalt2);
		 * (3失败) SimpleHash simpleHash = new SimpleHash("MD5", source,random);
		 * (4成功采纳) SimpleHash simpleHash = new SimpleHash("MD5", source,newSalt);
		 */
		
		SimpleHash simpleHash = new SimpleHash("SHA", source,newSalt);
		System.out.println("toBase64:" + simpleHash.toBase64());
//		SimpleHash simpleHash = new SimpleHash("MD5", source,newSalt);
		// 打印密码盐值
		System.out.println("加密后的密码：" + simpleHash.toHex());
		System.out.println("盐 值 输入为： " + String.valueOf(simpleHash));
		System.out.println("盐 值 长度为： " + String.valueOf(simpleHash).length());
		System.out.println("newSalt:" + newSalt);
	}

	public SysUser getPasswordByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public SysUser getUsersByAccount(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
