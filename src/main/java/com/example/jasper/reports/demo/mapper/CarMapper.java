package com.example.jasper.reports.demo.mapper;

import com.example.jasper.reports.demo.domain.Car;
import com.example.jasper.reports.demo.dto.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    CarDto carToDto(Car car);

    Car dtoToCar(CarDto carDto);
}
