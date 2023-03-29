package com.dxvalley.project.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class TotalCapital {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long totalCapitalId;
    private Double totalCapitalValue;
    @JsonFormat(pattern="yyyy-MM-dd",shape = Shape.STRING)
    @Column(name="date_generated")
    private String dateGenerated;

    //total capital with prCooperatives
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prCooperative_Id")
	private PrCooperative prCooperativeId;
}
