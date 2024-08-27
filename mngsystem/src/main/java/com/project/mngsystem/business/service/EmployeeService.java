package com.project.mngsystem.business.service;

import com.project.mngsystem.model.Employee;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees(String department, LocalDate year);
    Employee createEmployee(Employee employee);
    String deleteEmployee(Long id);
    void exportEmployeesToCSV(String department, LocalDate year, String filePath) throws IOException;
}
