package com.cs203t5.ryverbank.entity.User;

import java.util.List;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import com.cs203t5.ryverbank.entity.Transaction.*;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    private @Id String id;
    private String password;

    @JsonIgnore
    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    public User(String id, String password){
        this.id = id;
        this.password = password;
    }
}
