package com.example.demo.domain.entity;

import com.example.demo.domain.enums.RoleInGame;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userKey;

    private String userId;

    private String password;

    private String name;

    @ColumnDefault("0")
    private Integer wins;

    @ColumnDefault("0")
    private Integer loses;

    @ManyToOne
    @ToString.Exclude
    @JsonBackReference
    private GameInfo game;

    private Integer score;

    private RoleInGame roleInGame;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private LocalDateTime lastLoginDate;

    public UserInfo passwordEncodedUser(PasswordEncoder passwordEncoder) {
        this.setPassword(passwordEncoder.encode(this.password));
        return this;
    }

}
