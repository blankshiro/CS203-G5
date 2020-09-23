package com.cs203t5.ryverbank.entity.User;

import java.util.Arrays;
import java.util.Collection;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

import com.cs203t5.ryverbank.entity.Transaction.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class User implements UserDetails{

    private static final long serialVersionUID = 1L;

    private @Id  @GeneratedValue Long id;

    @NotNull(message = "Username should not be null")
    private String username;


    @NotNull(message = "Password should not be null")
    @Size(min = 5, message = "Password should be at least 5 characters long")
    private String password;


    @NotNull(message = "Authorities should not be null") //client, staff, supervisor...
    private String authorities;


    @JsonIgnore
    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public User(String username, String password, String authorities){
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(authorities));
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
}
