package com.cs203t5.ryverbank.token;

import java.time.*;
import java.util.UUID;

import javax.persistence.*;

import com.cs203t5.ryverbank.user.*;

import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private String tokenid;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    private LocalDate createdDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = LocalDate.now();
        confirmationToken = UUID.randomUUID().toString();
    }
}
