package com.cs203t5.ryverbank.customer;

import java.util.*;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

import com.cs203t5.ryverbank.account_transaction.*;

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
    @Column(name = "username", unique = true)
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

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL)
    private List<Account> accounts;

    public Customer(String full_name, String nric, String phone, String address, String username, String password,
            String authorities, boolean active) {
        this.full_name = full_name;
        this.nric = nric;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password = password;
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

    /**
     * Validates Singapore NRIC / FIN in 2 stages: 1) Ensure first letter starts
     * with S, T, F or G and the last letter is A-Z 2) Calculate weight of digits in
     * between first and last character of the input string, determine what the last
     * letter should be, then match it against the last character of the input
     * string.
     * 
     * Credit to: https://gist.github.com/squeeish/65cc82b0acaea3f551eac6e7885dc9c5
     *
     * @param nric NRIC / FIN string to be validated
     * @return true if NRIC/FIN passes, false otherwise
     */
    public boolean validateNric(String nric) {
        String nricToTest = nric.toUpperCase();

        // first letter must start with S, T, F or G. Last letter must be A - Z
        if (!Pattern.compile("^[STFG]\\d{7}[A-Z]$").matcher(nricToTest).matches()) {
            return false;
        } else {
            char[] icArray = new char[9];
            char[] st = "JZIHGFEDCBA".toCharArray();
            char[] fg = "XWUTRQPNMLK".toCharArray();

            for (int i = 0; i < 9; i++) {
                icArray[i] = nricToTest.charAt(i);
            }

            // calculate weight of positions 1 to 7
            int weight = (Integer.parseInt(String.valueOf(icArray[1]), 10)) * 2
                    + (Integer.parseInt(String.valueOf(icArray[2]), 10)) * 7
                    + (Integer.parseInt(String.valueOf(icArray[3]), 10)) * 6
                    + (Integer.parseInt(String.valueOf(icArray[4]), 10)) * 5
                    + (Integer.parseInt(String.valueOf(icArray[5]), 10)) * 4
                    + (Integer.parseInt(String.valueOf(icArray[6]), 10)) * 3
                    + (Integer.parseInt(String.valueOf(icArray[7]), 10)) * 2;

            int offset = icArray[0] == 'T' || icArray[0] == 'G' ? 4 : 0;

            int lastCharPosition = (offset + weight) % 11;

            if (icArray[0] == 'S' || icArray[0] == 'T') {
                return icArray[8] == st[lastCharPosition];
            } else if (icArray[0] == 'F' || icArray[0] == 'G') {
                return icArray[8] == fg[lastCharPosition];
            } else {
                return false; // this line should never reached due to regex above
            }
        }
    }

    public boolean validatePhone(String phone) {
        // validate phone numbers of format "1234567890"
        if (phone.matches("^[6|8|9]\\d{7}$"))
            return true;
        // return false if nothing matches the input
        else {
            return false;
        }
    }
}
