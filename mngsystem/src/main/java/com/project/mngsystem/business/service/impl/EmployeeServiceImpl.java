package com.project.mngsystem.business.service.impl;

import com.project.mngsystem.business.mappers.EmployeeMapper;
import com.project.mngsystem.business.repository.EmployeeRepository;
import com.project.mngsystem.business.repository.model.EmployeeDAO;
import com.project.mngsystem.business.service.EmployeeService;
import com.project.mngsystem.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<EmployeeDAO> employees;
        if (department != null && year != null) {
            employees = employeeRepository.findByDepartmentAndJoinDateAfter(department, year);
        } else {
            employees = employeeRepository.findAll();
        }
        return employees.stream()
                .map(employeeMapper::employeeDAOToEmployee)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(Employee employee) {
        EmployeeDAO employeeDAO = employeeMapper.employeeToEmployeeDAO(employee);
        EmployeeDAO savedEmployee = employeeRepository.save(employeeDAO);
        return employeeMapper.employeeDAOToEmployee(savedEmployee);
    }

    public String deleteEmployee(Long id) {
        Optional<EmployeeDAO> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            employeeRepository.deleteById(id);
            return "Employee successfully deleted.";
        } else {
            throw new RuntimeException("Employee not found or has left the organization.");
        }
    }

    public void exportEmployeesToCSV(String department, LocalDate year, String filePath) throws IOException {
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
        }
    }
}