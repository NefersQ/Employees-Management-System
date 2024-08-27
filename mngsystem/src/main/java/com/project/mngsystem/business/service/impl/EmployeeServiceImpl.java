package com.project.mngsystem.business.service.impl;

import com.project.mngsystem.business.handlers.CSVExportException;
import com.project.mngsystem.business.handlers.EmployeeNotFoundException;
import com.project.mngsystem.business.mappers.EmployeeMapper;
import com.project.mngsystem.business.repository.EmployeeRepository;
import com.project.mngsystem.business.repository.model.EmployeeDAO;
import com.project.mngsystem.business.service.EmployeeService;
import com.project.mngsystem.model.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public List<Employee> getAllEmployees(String department, LocalDate year) {
        log.debug("Getting all employees with department: {} and year: {}", department, year);
        List<EmployeeDAO> employees;
        if (department != null && year != null) {
            employees = employeeRepository.findByDepartmentAndJoinDateAfter(department, year);
        } else {
            employees = employeeRepository.findAll();
        }
        log.debug("Found {} employees", employees.size());
        return employees.stream()
                .map(employeeMapper::employeeDAOToEmployee)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(Employee employee) {
        log.debug("Creating employee: {}", employee);
        EmployeeDAO employeeDAO = employeeMapper.employeeToEmployeeDAO(employee);
        EmployeeDAO savedEmployee = employeeRepository.save(employeeDAO);
        log.debug("Employee created with ID: {}", savedEmployee.getId());
        return employeeMapper.employeeDAOToEmployee(savedEmployee);
    }

    public String deleteEmployee(Long id) {
        log.debug("Deleting employee with ID: {}", id);
        Optional<EmployeeDAO> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            employeeRepository.deleteById(id);
            log.info("Employee with ID: {} successfully deleted.", id);
            return "Employee successfully deleted.";
        } else {
            log.warn("Employee with ID: {} not found or has left the organization.", id);
            throw new EmployeeNotFoundException("Employee not found or has left the organization.");
        }
    }

    public void exportEmployeesToCSV(String department, LocalDate year, String filePath) {
        log.debug("Exporting employees to CSV file with department: {} and year: {}", department, year);
        List<Employee> employees = getAllEmployees(department, year);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"ID", "Name", "Department", "Join Date"};
            writer.writeNext(header);

            for (Employee employee : employees) {
                String[] data = {
                        employee.getId().toString(),
                        employee.getName(),
                        employee.getDepartment(),
                        employee.getJoinDate().toString()
                };
                writer.writeNext(data);
            }
            log.info("Successfully exported {} employees to {}", employees.size(), filePath);
        } catch (IOException e) {
            log.error("Error exporting data to CSV: {}", e.getMessage(), e);
            throw new CSVExportException("Error exporting data to CSV", e);
        }
    }
}