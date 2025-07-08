package com.qualitymanagementsystemfc.qualitymanagementsystem.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private String id;
    private String username;
    @JsonIgnore
    private String password;
    private GrantedAuthority authority; // Single role

    public UserDetailsImpl(String id, String username, String password, GrantedAuthority authority) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authority = authority;
    }

    public static UserDetailsImpl build(UserDO user) {
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        return new UserDetailsImpl(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                authority
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return authority.getAuthority();
    }
}
