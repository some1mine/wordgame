package com.example.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.type.TrueFalseConverter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class GameInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameKey;

    private String initial;

    private String name;

    private Integer capacity;

    @ColumnDefault("0")
    @Convert(converter = TrueFalseConverter.class)
    private Boolean isEnded;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private LocalDateTime lastLoginDate;


    @OneToMany(mappedBy = "game")
    @Builder.Default
    @ToString.Exclude
    @JsonManagedReference
    private List<UserInfo> participants = new ArrayList<>();

    public void addPlayer(UserInfo p) {
        this.participants.add(p);
        p.setGame(this);
    }
}

