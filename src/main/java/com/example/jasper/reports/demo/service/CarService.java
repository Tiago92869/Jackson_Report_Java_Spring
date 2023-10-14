package com.example.jasper.reports.demo.service;

import com.example.jasper.reports.demo.domain.Car;
import com.example.jasper.reports.demo.dto.CarDto;
import com.example.jasper.reports.demo.exception.BadRequestException;
import com.example.jasper.reports.demo.exception.EntityNotFoundException;
import com.example.jasper.reports.demo.mapper.CarMapper;
import com.example.jasper.reports.demo.repository.CarRepository;
import com.example.jasper.reports.demo.utils.ImageUtil;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
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

    public ResponseEntity<?> getCarImageById(UUID id) {

        Optional<Car> maybeOptional = this.carRepository.findById(id);

        if(maybeOptional.isEmpty()){
            throw new EntityNotFoundException("Car could not be found");
        }

        Car car = maybeOptional.get();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_PNG)
                .body(ImageUtil.decompressImage(maybeOptional.get().getImage()));
    }

    public void uploadCarImage(UUID id, MultipartFile file) {

        Optional<Car> maybeOptional = this.carRepository.findById(id);

        if(maybeOptional.isEmpty()){
            throw new EntityNotFoundException("Car could not be found");
        }

        try{
            Car car = maybeOptional.get();
            //check for content type of image
            if(file != null) {

                if(!Objects.equals(file.getContentType(), "image/png")
                        && !Objects.equals(file.getContentType(), "image/jpeg")){

                    throw new ValidationException("The image must be of type .png, .jpeg or .jpg");
                }
            }

            car.setImage(ImageUtil.compressImage(file.getBytes()));
            this.carRepository.save(car);
        }
        catch (Exception e){
            throw new BadRequestException("Something went wrong when uploading the image " + e.getMessage());
        }
    }
}
