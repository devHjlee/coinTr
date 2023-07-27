package com.cointr.upbit.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coin implements Persistable<String> {
    @Id
    @Column(name = "market")
    private String id;
    @CreatedDate
    private LocalDateTime createdDate;
    private String koreaName;

    @Builder
    public Coin(String id, String koreaName, String englishName){
        this.id = id;
        this.koreaName = koreaName;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
