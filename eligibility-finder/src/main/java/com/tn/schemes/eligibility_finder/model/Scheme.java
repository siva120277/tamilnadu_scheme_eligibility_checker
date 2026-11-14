package com.tn.schemes.eligibility_finder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scheme")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code", unique = true, nullable = false)
    private String schemeCode;

    @Column(nullable = false)
    private String name;

    private String category;
    private String benefitAmount;

    @Column(length = 1000)
    private String description;

    private String level; // STATE or CENTRAL
    private String state;
    private String officialUrl;

    @OneToMany(mappedBy = "scheme", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RequiredDocument> requiredDocuments = new ArrayList<>();
}