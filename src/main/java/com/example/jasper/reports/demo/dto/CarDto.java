package com.example.jasper.reports.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarDto {

    private UUID id;

    private String name;

    private String quantity;

    private byte[] image;
}
