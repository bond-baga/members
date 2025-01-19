package com.example.demo.dto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UserForm implements Serializable{
	private String code;
	private String name;
	private String kana;
	private String password;
	private String statename;
	private String sectionname;
	private String divisionname;
	private List<String> checked;
	
	public UserForm() {
		code = "";
		name = "";
		kana= "";
		password = "";
		statename = "";
		sectionname = "";
		divisionname = "";
		checked = new ArrayList<String>();
	}
}
