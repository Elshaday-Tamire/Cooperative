package com.dxvalley.project.models;

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
public class Asset {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long assetId;
    private String assetName;
    private Double value;
    private String type;

    //user with union
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "union_Id")
	private Unions unionId;

    //user with prCooperative
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prCooperative_Id")
	private PrCooperative prCooperativeId;
}
