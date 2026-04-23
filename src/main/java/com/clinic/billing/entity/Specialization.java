package com.clinic.billing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "specialization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String status;
}
