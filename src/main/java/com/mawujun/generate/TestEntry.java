package com.mawujun.generate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "t_testentry")
public class TestEntry {
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(
	        name = "uuid",
	        strategy = "org.hibernate.id.UUIDGenerator"
	    )
	@Column(length=36)
	//@GeneratedValue
	private String id;
	@Column(length=30)  
	private String loginname;
	@Column(length=30)  
	private String password;
	
	private Boolean isdel;
	
	public Boolean getIsdel() {
		return isdel;
	}
	public void setIsdel(Boolean isdel) {
		this.isdel = isdel;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(length=30)  
	private String username;
}
