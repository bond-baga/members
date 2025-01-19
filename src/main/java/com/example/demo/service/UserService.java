package com.example.demo.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserForm;

@Service
public class UserService {
	/*
	@Autowired
    UsermstRepository userMstRepository;

    public List<Usermst> findAllData() {
        return userMstRepository.findAll();
    }
    */
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public UserForm clear(UserForm form) {
		form.setCode("");
		form.setKana("");
		form.setName("");
		
		return form;
	}
	
	public List<Map<String, Object>> select(UserForm form) {
		String query = "SELECT * FROM usermst WHERE name is not null";
		
		if (form.getName() != null && form.getName() != "") {
			query += " AND name LIKE '" + form.getName() + "%'";
		}
		if (form.getKana() != null && form.getKana() != "") {
			query += " AND kana LIKE '" + form.getKana() + "%'";
		}
		
		if (form.getChecked().size() != 0) {
			query += " AND (";
			for (int i = 0; i < form.getChecked().size(); i++) {
				if (i == 0) {
					query += " statename = '" + form.getChecked().get(i) + "'";
				}else{
					query += " OR statename = '" + form.getChecked().get(i) + "'";				
				}
			}
			query += ")";
		}

		/*
		if (form.getStatename() != null && form.getStatename() != "") {
			query += " AND statename = '" + form.getStatename() + "'";			
		}
		*/
		
		if (form.getDivisionname() != null && form.getDivisionname() != "") {
			query += " AND divisionname = '" + form.getDivisionname() + "'";
		}
		return jdbcTemplate.queryForList(query);
	}
	
	public UserForm select(String code) {
		String query = "SELECT * FROM usermst WHERE code = '" + code + "'";
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList(query);
		UserForm form = new UserForm();
		
		if (tmp.size() != 0) {
			form.setCode((String) tmp.get(0).get("code")); 
			form.setName((String) tmp.get(0).get("name")); 
			form.setKana((String) tmp.get(0).get("kana")); 			
			form.setStatename((String) tmp.get(0).get("statename")); 			
			form.setSectionname((String) tmp.get(0).get("sectionname")); 			
			form.setDivisionname((String) tmp.get(0).get("divisionname")); 			
		}
	
		return form;		
	}
	
	public Boolean update(UserForm form) {
		String query = "UPDATE usermst SET"
		+ " name = '" + form.getName() + "',"
		+ " kana = '" + form.getKana() + "',"
        + " statename = '" + form.getStatename() + "',"
        + " sectionname = '" + form.getSectionname() + "',"
        + " divisionname = '" + form.getDivisionname() + "'"
        + " WHERE code = '"  +  form.getCode() + "'";
		jdbcTemplate.execute(query);
		return true;
	}
	
	
	public List<Map<String, Object>> items(String table) {		
		String query = MessageFormat.format("SELECT ROW_NUMBER() OVER() no, {0} FROM (SELECT DISTINCT {0} FROM usermst) ORDER BY {0}", table);	
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList(query);
		// リストの先頭にマップ配列を挿入する
		tmp.add(0, new HashMap<String, Object>(){{put("no", "0");put(table, "");}});
		return tmp;	
	}	

	public List<Map<String, Object>> items_no_blank(String table) {		
		String query = MessageFormat.format("SELECT ROW_NUMBER() OVER() no, {0} FROM (SELECT DISTINCT {0} FROM usermst) ORDER BY {0}", table);	
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList(query);
		return tmp;	
	}	

}