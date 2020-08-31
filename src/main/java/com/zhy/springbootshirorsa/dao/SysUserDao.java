package com.zhy.springbootshirorsa.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.zhy.springbootshirorsa.pojo.SysUser;

@Mapper
public interface SysUserDao {

	List<SysUser>  findAll();

	SysUser findByUserName(String userName);
	
	List<String> findUserPermissions(Integer id);

	int addUser(SysUser user);
	
}
