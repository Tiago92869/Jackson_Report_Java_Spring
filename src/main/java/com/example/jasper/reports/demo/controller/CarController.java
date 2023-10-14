package com.example.jasper.reports.demo.controller;

import com.example.jasper.reports.demo.dto.CarDto;
import com.example.jasper.reports.demo.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Car", description = "Car management")
@RequestMapping("/car")
@RestController
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    @Operation(summary = "List all cars")
    @ResponseStatus(HttpStatus.OK)
    public Page<CarDto> getAllCars(Pageable pageable){

        return this.carService.getAllCars(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by id")
    @ResponseStatus(HttpStatus.OK)
    public CarDto getCarById(@PathVariable UUID id){

        return this.carService.getCarById(id);
    }

    @PostMapping("/")
    @Operation(summary = "Create car")
    @ResponseStatus(HttpStatus.OK)
    public CarDto createCar(@RequestBody CarDto carDto){

        return this.carService.createCar(carDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update car by id")
    @ResponseStatus(HttpStatus.OK)
    public CarDto updateCarById(@PathVariable UUID id, @RequestBody CarDto carDto){

        return this.carService.updateCarById(id, carDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car by id")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCarById(@PathVariable UUID id){

        this.carService.deleteCarById(id);
    }
}
