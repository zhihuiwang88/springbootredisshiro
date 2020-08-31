package com.zhy.springbootshirorsa.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class SysUser implements Serializable{

	/**
	 * 用户实体类
	 */
	private static final long serialVersionUID = -2041757889178706760L;

	private Integer id;
	private String userName;
	private String password;
	private String salt;
	
}
