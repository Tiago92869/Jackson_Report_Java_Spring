package com.example.jasper.reports.demo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car", description = "Car management")
@RequestMapping("/car")
@RestController
public class CarController {
}
