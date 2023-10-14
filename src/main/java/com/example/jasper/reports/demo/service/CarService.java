package com.example.jasper.reports.demo.service;

import com.example.jasper.reports.demo.domain.Car;
import com.example.jasper.reports.demo.dto.CarDto;
import com.example.jasper.reports.demo.exception.EntityNotFoundException;
import com.example.jasper.reports.demo.mapper.CarMapper;
import com.example.jasper.reports.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Page<CarDto> getAllCars(Pageable pageable) {

        return this.carRepository.findAll(pageable).map(CarMapper.INSTANCE::carToDto);
    }

    public CarDto getCarById(UUID id) {

        Optional<Car> optionalCar = this.carRepository.findById(id);

        if(optionalCar.isEmpty()){

            throw new EntityNotFoundException("Car could not be found");
        }

        return CarMapper.INSTANCE.carToDto(optionalCar.get());
    }

    public CarDto createCar(CarDto carDto) {

        carDto.setId(UUID.randomUUID());

        Car newCar = CarMapper.INSTANCE.dtoToCar(carDto);

        return CarMapper.INSTANCE.carToDto(this.carRepository.save(newCar));
    }

    public CarDto updateCarById(UUID id, CarDto carDto) {

        Optional<Car> optionalCar = this.carRepository.findById(id);

        if(optionalCar.isEmpty()){

            throw new EntityNotFoundException("Car could not be found");
        }

        Car newCar = optionalCar.get();

        if(carDto.getName() != null){

            newCar.setName(carDto.getName());
        }

        if(carDto.getQuantity() != null){

            newCar.setQuantity(carDto.getQuantity());
        }

        return CarMapper.INSTANCE.carToDto(this.carRepository.save(newCar));
    }

    public void deleteCarById(UUID id) {

        Optional<Car> optionalCar = this.carRepository.findById(id);

        if(optionalCar.isEmpty()){

            throw new EntityNotFoundException("Car could not be found");
        }

        this.carRepository.deleteById(id);
    }
}
