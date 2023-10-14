package com.example.jasper.reports.demo.repository;

import com.example.jasper.reports.demo.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarRepository extends JpaRepository<Car, UUID> {
}
