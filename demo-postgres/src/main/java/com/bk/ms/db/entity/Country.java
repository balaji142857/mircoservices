package com.bk.ms.db.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue
    @Column(unique = true)
    private Long id;

    @Column(unique = true)
    private String code;
    private String name;
    private String nativeName;
    private int phone;
    private String capital;
    private String continent;
    private String currency;
    private String language;

}