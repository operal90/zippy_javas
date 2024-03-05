package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table (name = "security_questions")
public class SecurityQuestionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String question;
    private String createdAt;
    private String createdBy;

    public SecurityQuestionEntity() {
    }
}
