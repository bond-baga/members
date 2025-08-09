package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.LoginForm;
import com.example.demo.dto.VideoForm;
import com.example.demo.service.VideoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OpenController {
    @Autowired
    VideoService videoService;
    
    @GetMapping(value={"/open"})
    public String getList(@ModelAttribute("VideoForm")VideoForm form, Model model, HttpSession session) {
		form.setId(((LoginForm)session.getAttribute("user")).getId());
    	model.addAttribute("title", "動画"); 
        Integer count = videoService.count(form);
        form.setCount(count);
        form.setMaxpage((int) (Math.floor((count - 1) / 9) + 1));
        if (form.getPage() == 0) form.setPage(1);
        form.getItems().clear();
        videoService.select(form);
        model.addAttribute("VideoForm", form);
        return "open";
    }
        
    @PostMapping(value={"/open"})
    public String postList(VideoForm form, BindingResult resul, RedirectAttributes redirectAttributes, HttpSession session) {
		redirectAttributes.addFlashAttribute("VideoForm", form); 
        return "redirect:/open";
    }
    
    @PostMapping(value={"/open_page"})
    public String postPage(VideoForm form, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session){
    	redirectAttributes.addFlashAttribute("VideoForm", form); 
        return "redirect:/open";
    }
    
}