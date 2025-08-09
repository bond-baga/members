package com.example.demo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.VideoForm;

import jakarta.servlet.ServletContext;


@Service
public class VideoService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	ServletContext context;	
	
	public void select(VideoForm form) {
		String query = 
		"SELECT"+
		" ROW_NUMBER() OVER(ORDER BY a.id) no"+ 
		",a.id"+
		",a.username"+
		",a.fullname"+
		",b.loginid"+
		",b.filename"+
		",b.contenttype"+
		",b.video"+
		",b.comment"+
		",b.lastupdate"+
		" FROM login a"+
		" INNER JOIN video b"+
		" ON a.id=b.loginid"+
		" WHERE a.id = " + form.getId();
		switch (form.getDispindex()){
		default:
		case 1:
			query += " ORDER BY loginid ASC,lastupdate ASC";		
			break;
		case 2:
			query += " ORDER BY loginid ASC,lastupdate DESC";		
			break;
		case 3:
			query += " ORDER BY loginid ASC,comment ASC";		
			break;
		}
		query = "SELECT no, id, loginid, filename, username, fullname, contenttype, video, comment" +
		" FROM (" + query +") WHERE no BETWEEN " + Integer.toString((form.getPage()-1) * 9 + 1) + " AND " + Integer.toString(form.getPage() * 9);

		List<Map<String, Object>> tmp = jdbcTemplate.queryForList(query);
		for (int i=0; i<tmp.size(); i++) {
			if (tmp.get(i).get("filename") != null) {
				HashMap<String, Object> mp = new HashMap<String, Object>();
				mp.put("filename", "/videofiles/" + String.format("%06d", form.getId()) + "/" + (String)tmp.get(i).get("filename"));
				mp.put("comment", (String)tmp.get(i).get("comment"));
				form.getItems().add(mp);
			}
		}
		return;
	}
	
	public Integer count(VideoForm form) {
		String query = 
		"SELECT count(*)"+
		" FROM login a"+
		" INNER JOIN video b"+
		" ON a.id=b.loginid"+
		" WHERE a.id = " + form.getId();
		int tmp = (Integer) jdbcTemplate.queryForObject(query, Integer.class);
		return tmp;
	}
	
	public Boolean insert(VideoForm form) throws Exception{
		List<MultipartFile> files = form.getVideofiles();
		for (int i = 0; i < files.size(); i++) {
			String contenttype = files.get(i).getContentType();
			String comment = (String) form.getItems().get(i).get("comment");
			String query = "INSERT INTO video (loginid, filename, video, contenttype, comment, lastupdate) VALUES (?,?,?,?,?,?)";  
			jdbcTemplate.update(query, new Object[] {form.getId(), files.get(i).getOriginalFilename(), null, contenttype, comment, LocalDate.now()});		
			// アップロードフォルダー
			File uploadDir = new File("./videofiles/" + String.format("%06d", form.getId()));    // returns the folder where the jar is.			
			if (!uploadDir.exists()) {
				// フォルダ作成
				uploadDir.mkdirs();
			}	    
			// FileCopy
			Files.copy(files.get(i).getInputStream(), Paths.get(uploadDir +"/" +  files.get(i).getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
		}
		return true;
	}

	public Boolean update(VideoForm form) throws Exception{
		for (int i = 0; i < form.getItems().size(); i++) {
			String comment = (String) form.getItems().get(i).get("comment");
			String filename = (String) form.getItems().get(i).get("filename");	 
			filename = filename.substring(("/videofiles/" + String.format("%06d", form.getId()) + "/").length());
			String query = "SELECT * FROM video WHERE (loginid = ? AND filename = ?)";  
			Map<String, Object> tmp = jdbcTemplate.queryForMap(query, new Object[] {form.getId(), filename});
			if (tmp != null && !tmp.get("comment").equals(comment)) {
				query = "UPDATE video SET comment = ?, lastupdate = ? WHERE (loginid = ? AND filename = ?)";  
				jdbcTemplate.update(query, new Object[] {comment, LocalDateTime.now(), form.getId(), filename});
			}		
		}
		return true;
	}

	public byte[] filedata(MultipartFile multipartFile) throws Exception{
		BufferedImage originalImg = ImageIO.read(multipartFile.getInputStream());
		String formatName="";
//		if (multipartFile.getContentType().equals("image/jpeg")) formatName="jpeg";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImg, formatName, baos);
		return baos.toByteArray();
	}	
	
}