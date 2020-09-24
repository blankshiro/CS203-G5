package com.cs203t5.ryverbank.customer;

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
public class Customer implements UserDetails {
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
    private String full_name;

    @Column(name = "nric", unique = true)
    private String nric;
    
    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "address")
    private String address;

    @NotNull(message = "Authorities should not be null")
    private String authorities;

    private boolean active = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public Customer(String username, String password, String full_name, String nric, String phone, String address, String authorities, boolean active) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.nric = nric;
        this.phone = phone;
        this.address = address;
        this.authorities = authorities;
        this.active = active;
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
