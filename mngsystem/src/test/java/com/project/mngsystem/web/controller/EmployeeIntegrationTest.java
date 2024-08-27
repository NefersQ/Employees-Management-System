package com.project.mngsystem.web.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mngsystem.business.repository.EmployeeRepository;
import com.project.mngsystem.business.repository.model.EmployeeDAO;
import com.project.mngsystem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    public void testCreateEmployee() throws Exception {
        Employee employee = new Employee(null, "John Doe", "Engineering", LocalDate.of(2022, 1, 1));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        EmployeeDAO employee1 = new EmployeeDAO(null, "John Doe", "Engineering", LocalDate.of(2022, 1, 1));
        EmployeeDAO employee2 = new EmployeeDAO(null, "Jane Doe", "HR", LocalDate.of(2023, 2, 1));
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        mockMvc.perform(get("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        EmployeeDAO employee = new EmployeeDAO(null, "John Doe", "Aviation", LocalDate.of(2022, 1, 1));
        employee = employeeRepository.save(employee);

        mockMvc.perform(delete("/api/v1/employees/" + employee.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee successfully deleted."));
    }

    @Test
    public void testExportEmployeesToCSV() throws Exception {
        EmployeeDAO employee = new EmployeeDAO(null, "John Doe", "Space", LocalDate.of(2022, 1, 1));
        employeeRepository.save(employee);

        mockMvc.perform(get("/api/v1/employees/export")
                        .param("department", "Space")
                        .param("year", "2021-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Data exported to employees.csv"));
    }
}
