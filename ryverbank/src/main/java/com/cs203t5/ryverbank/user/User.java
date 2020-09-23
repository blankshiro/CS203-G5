package com.cs203t5.ryverbank.user;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

import com.cs203t5.ryverbank.transaction.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User implements UserDetails {
    private static final long serialVersionUID = 1L;

    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    @NotNull(message = "Username should not be null")
    @Size(min = 5, max = 20, message = "Username should be between 5 and 20 characters")
    @Column(name = "username")
    private String username;

    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "nric", unique = true)
    private String nric;
    
    @Column(name = "phone", unique = true)
    private String phoneNo;

    @Column(name = "address")
    private String address;

    @Column(name = "email", unique = true)
    private String email;

    @NotNull(message = "Authorities should not be null")
    private String authorities;

    private boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public User(String username, String password, String fullname, String nric, String phoneNo, String address, String email, String authorities) {
        this.username = username;
        this.password = password;
        this.fullName = fullname;
        this.nric = nric;
        this.phoneNo = phoneNo;
        this.address = address;
        this.email = email;
        this.authorities = authorities;
    }
    
    /*
     * Return a collection of authorities (roles) granted to the user.
     */
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
        return active;
    }
}
