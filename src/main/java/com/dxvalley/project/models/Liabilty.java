package com.dxvalley.project.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Liabilty {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long liabilityId;
    private Double liabilityValue;
    @JsonFormat(pattern="yyyy-MM-dd",shape = Shape.STRING)
    @Column(name="date_generated")
    private String dateGenerated;
}
