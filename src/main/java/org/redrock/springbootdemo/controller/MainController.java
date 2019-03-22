package org.redrock.springbootdemo.controller;

import org.redrock.springbootdemo.annotation.Cache;
import org.redrock.springbootdemo.dao.UserRepository;
import org.redrock.springbootdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.timer.Timer;
import javax.servlet.http.HttpSession;

@RestController
public class MainController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String Login(String username, String password, HttpSession session){
        User user = null;
        user = userRepository.findByUsernameAndPassword(username,password);
        System.out.println("过服务");
        if(user != null){
            session.setAttribute("role",user);
            return user.getUsername()+"登录成功，刷新页面将获得详细信息！";
        }else {
            return "登录失败！";
        }
    }
    @GetMapping("/admin/deleteAllDatabases")
    public String deleteAllDb(){
        return "删库成功！";
    }
    @GetMapping("/welcome")
    @Cache
    public String welcome(String username){
        System.out.println("模拟计算和访问数据库的开销");
        try {
            Thread.sleep(5 * Timer.ONE_SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "欢迎用户"+username;
    }
}
