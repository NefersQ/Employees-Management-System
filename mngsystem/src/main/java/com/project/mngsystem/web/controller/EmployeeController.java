package com.project.mngsystem.web.controller;

import com.project.mngsystem.business.service.EmployeeService;
import com.project.mngsystem.model.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate year) {
        log.info("Fetching employees with department: {} and year: {}", department, year);
        List<Employee> employees = employeeService.getAllEmployees(department, year);
        log.info("Fetched {} employees", employees.size());
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        log.info("Creating new employee: {}", employee);
        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            log.info("New employee created with ID: {}", createdEmployee.getId());
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating employee: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        try {
            String message = employeeService.deleteEmployee(id);
            log.info("Employee with ID: {} deleted successfully", id);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error deleting employee with ID: {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportEmployeesToCSV(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate year) {
            log.info("Exporting employees to CSV file with department: {} and who joined after: {}", department, year);
        try {
            String filePath = "employees.csv";
            employeeService.exportEmployeesToCSV(department, year, filePath);
            log.info("Data exported to {}", filePath);
            return new ResponseEntity<>("Data exported to " + filePath, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error exporting data: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error exporting data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
