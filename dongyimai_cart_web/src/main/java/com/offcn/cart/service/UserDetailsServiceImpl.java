package com.offcn.cart.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/*** 认证类*/

public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色集合
        List<GrantedAuthority> authorities=new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        System.out.println(username);
        return new User(username, ""  , authorities);
    }
}