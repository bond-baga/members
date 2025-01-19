package com.example.demo.dto;
import java.io.Serializable;

import lombok.Data;

@Data
public class LoginForm implements Serializable{
	private int id;
	
	private String username;
	private String password;
	private String fullname;
	private String email;
	
	public LoginForm() {
		id = 0;
		username = "";
		password = "";
		fullname = "";
		email = "";
	}
}
