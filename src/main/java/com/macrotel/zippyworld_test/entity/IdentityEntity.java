package com.macrotel.zippyworld_test.entity;

import com.macrotel.zippyworld_test.pojo.IdentityData;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "identities")
public class IdentityEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String identityType;
    private String createdAt;
    private String status;

    public IdentityEntity() {
    }

    public IdentityEntity(IdentityData identityData) {
        this.createdAt = String.valueOf(LocalDate.now());
        this.status ="0";
        this.identityType = identityData.getIdentityName();
    }
}
