package org.redrock.springbootdemo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.redrock.springbootdemo.dao.AutorityRepository;
import org.redrock.springbootdemo.dao.MappingRepository;
import org.redrock.springbootdemo.entity.Authority;
import org.redrock.springbootdemo.entity.Mapping;
import org.redrock.springbootdemo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Aspect
@Order(2)
public class AuthenticationAspect {
    @Autowired
    MappingRepository mappingRepository;
    @Autowired
    AutorityRepository autorityRepository;

    @Pointcut("execution(public * org.redrock.springbootdemo.controller.*.*(..))")
    public void authentication() {
    }

    @Around("authentication()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("aop-----around 1");
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        User role = (User) request.getSession().getAttribute("role");
        Integer level = null;
        String remark = "\0";
        if (role != null) {
            List<Mapping> mappingList = mappingRepository.findByUserId(role.getId());
            List<Optional<Authority>> authorityList = new ArrayList<>();
            for (Mapping content : mappingList) {
                Optional<Authority> authority = autorityRepository.findById(content.getAuthorityId());
                authorityList.add(authority);
            }
            for (int i = 0; i < authorityList.size(); i++) {
                if ((level != null ? level.intValue() : 0) < authorityList.get(i).get().getLevel()) {
                    level = authorityList.get(i).get().getLevel();
                }
                remark = remark + (i + 1) + "." + authorityList.get(i).get().getRemark() + "   ";
            }
        }
        if (request.getRequestURI().contains("login") && role != null) {
            if ((level != null ? level.intValue() : 0) < 2) {
                return "你的权限等级为" + level + "级;  你拥有以下权限:" + remark + ";   你不能访问管理员页面";
            }
            return "你的权限等级为" + level + "级;   你拥有以下权限:" + remark + ";   你可以访问管理员页面";
        }
        if (request.getRequestURI().contains("admin")) {
            if ((level != null ? level.intValue() : 0) < 2) {
                return "403";
            }
        }
        Object res = null;
        res = pjp.proceed();
        System.out.println("aop-----around 2");
        return res;
    }
}
