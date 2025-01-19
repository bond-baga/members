package com.example.demo.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.LoginForm;
import com.example.demo.dto.UserForm;
import com.example.demo.service.LoginService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    
    @Autowired
    LoginService loginService;
    
    @GetMapping("/")
    public String success(@ModelAttribute("UserForm")UserForm form, Model model, HttpSession session) {
        model.addAttribute("title", "一覧");
        if (session.getAttribute("user") == null) {
            form.getChecked().add("通常");
        }
        model.addAttribute("UserForm", form);
        // セッションの初期化は行わない
        // セッションにログインユーザーを保存
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        session.setAttribute("user", loginService.select(auth.getName()));
        
        // 職員データの取得
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("users", userService.select(form));
        model.addAttribute("data", data);
        // プルダウン項目の取得
        Map<String, List<Map<String, Object>>> items = new HashMap<>();
        // 状態は空白行を設けない
        items.put("statename", userService.items_no_blank("statename"));
        items.put("divisionname", userService.items("divisionname"));
        model.addAttribute("items", items);
        return "list";
    }

    @GetMapping(value={"/login"})
    public String getHome(@ModelAttribute("LoginForm")LoginForm form, @ModelAttribute("map")HashMap<String, Boolean> map, Model model) {
      model.addAttribute("title", "ログイン");
      model.addAttribute("LoginForm", form);
      if (!map.containsKey("registered")) {
      	map.put("registered", false);
      }
      
      model.addAttribute("map", map);
  	  return "login";
    }
    
    @GetMapping("/register")
    public String registerform(@ModelAttribute("LoginForm")LoginForm form, @ModelAttribute("map")HashMap<String, Boolean> map, Model model)  {
        model.addAttribute("title", "アカウント登録");
        model.addAttribute("LoginForm", form);
        if (!map.containsKey("exists")) {
        	map.put("exists", false);
        }
        if (!map.containsKey("failed")) {
        	map.put("failed", false);
        }
        if (!map.containsKey("registered")) {
        	map.put("registerd", false);
        } 
        model.addAttribute("map", map);        
        return "register";
    }
    
    
    @PostMapping("/register") 
    public String register(LoginForm form, Model model, RedirectAttributes redirectAttributes) {
        LoginForm loginform = loginService.select(form.getUsername()); 
    	HashMap<String, Boolean> map = new HashMap<>();

    	if(loginform.getId() != 0){
            // ユーザーが既に存在する場合の処理
        	redirectAttributes.addFlashAttribute("LoginForm", form);  
        	map.put("exists", true);
        	redirectAttributes.addFlashAttribute("map", map);    	
            return "redirect:/register"; 
        } else {
            if (loginService.insert(form)) {
            	// 新規のアカウントが登録された場合　
            	redirectAttributes.addFlashAttribute("LoginForm", form);    	
            	map.put("registered", true);
            	redirectAttributes.addFlashAttribute("map", map);    	
                return "redirect:/login";             	
            } else {
            	// 新規のアカウントの登録に失敗した場合
            	redirectAttributes.addFlashAttribute("LoginForm", form);    	
            	map.put("failed", true);
            	redirectAttributes.addFlashAttribute("map", map);    	
                return "redirect:/register";             	            	
            }        	
        } 
    }    
        
    @PostMapping("/search")
    public String getsearch(UserForm form, Model model,HttpSession session, RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("UserForm", form);    	
        // セッションにログインユーザーを保存
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        session.setAttribute("user", auth.getName());
        //session.setAttribute("user",loginService.select(auth.getName()));
        return "redirect:/";
    }


    @PostMapping("/insert")
    public String insert(@ModelAttribute UserForm form,  Model model, HttpSession session) {
        model.addAttribute("title", "新規");
        model.addAttribute("UserForm", userService.clear(form));
        model.addAttribute("user", session.getAttribute("user"));
        return "edit";
    } 

    @PostMapping("/update")
    public String update(@ModelAttribute UserForm form,  Model model, HttpSession session) {
        model.addAttribute("title", "更新");
        model.addAttribute("UserForm", userService.select(form.getCode()));
        model.addAttribute("user", session.getAttribute("user"));
        return "edit";
    } 

    @PostMapping("/delete")
    public String delete(@ModelAttribute UserForm form,  Model model, HttpSession session) {
        model.addAttribute("title", "削除");
        model.addAttribute("UserForm", userService.select(form.getCode()));
        model.addAttribute("user", session.getAttribute("user"));
        return "edit";
    } 

    @PostMapping("/write")
    public String write(@ModelAttribute UserForm form,  Model model) {
    	userService.update(form);	

        model.addAttribute("title", "一覧");
        model.addAttribute("UserForm", form);
                
        // 職員データの取得
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("user", userService.select(form));
        model.addAttribute("data", data);
        // プルダウン項目の取得
        Map<String, List<Map<String, Object>>> items = new HashMap<>();
        // 状態は空白行を設けない
        items.put("statename", userService.items_no_blank("statename"));
        items.put("divisionname", userService.items("divisionname"));
        model.addAttribute("items", items);
        return "list";
    }
    
}