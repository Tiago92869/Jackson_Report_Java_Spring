package com.example.jasper.reports.demo.service;

import com.example.jasper.reports.demo.domain.Car;
import com.example.jasper.reports.demo.dto.CarDto;
import com.example.jasper.reports.demo.exception.BadRequestException;
import com.example.jasper.reports.demo.exception.EntityNotFoundException;
import com.example.jasper.reports.demo.mapper.CarMapper;
import com.example.jasper.reports.demo.repository.CarRepository;
import com.example.jasper.reports.demo.utils.ImageUtil;
import jakarta.validation.ValidationException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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


    public void createCarFile() throws JRException, FileNotFoundException {

        List<Car> carList = this.carRepository.findAll();

        //Load file and compile it
        File file = ResourceUtils.getFile("classpath:cars.jrxml");

        //generate the jasper file
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        //catch the data needed from the list
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(carList);

        //create parameters (not needed)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Tiago92869");

        //Generate file
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Get the current working directory
        String currentDirectory = System.getProperty("user.dir");

        //Export file
        JasperExportManager.exportReportToHtmlFile(jasperPrint, currentDirectory + "/cars.html");
    }
}
