package com.project.mngsystem.web.controller;

import com.project.mngsystem.business.service.EmployeeService;
import com.project.mngsystem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testGetEmployees() throws Exception {
        Employee employee = new Employee();
        when(employeeService.getAllEmployees(anyString(), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(employee));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreateEmployee() {
        Employee employee = new Employee();
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        ResponseEntity<?> response = employeeController.createEmployee(employee);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(employee, response.getBody());
        verify(employeeService, times(1)).createEmployee(employee);
    }

    @Test
    void testCreateEmployee_InternalServerError() throws Exception {
        when(employeeService.createEmployee(any(Employee.class)))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content("{\"name\": \"John Doe\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doReturn("Employee successfully deleted.").when(employeeService).deleteEmployee(anyLong());

        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee successfully deleted."));
    }

    @Test
    void testDeleteEmployee_NotFound() throws Exception {
        doThrow(new RuntimeException("Employee not found or has left the organization."))
                .when(employeeService).deleteEmployee(anyLong());

        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found or has left the organization."));
    }

    @Test
    void testExportEmployeesToCSV() throws Exception {
        doNothing().when(employeeService).exportEmployeesToCSV(anyString(), any(LocalDate.class), anyString());

        mockMvc.perform(get("/api/v1/employees/export"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data exported to employees.csv"));
    }

    @Test
    void testExportEmployeesToCSVError() throws IOException {
        String department = "IT";
        LocalDate year = LocalDate.now();
        doThrow(new IOException("IO Error")).when(employeeService).exportEmployeesToCSV(department, year, "employees.csv");

        ResponseEntity<String> response = employeeController.exportEmployeesToCSV(department, year);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error exporting data: IO Error", response.getBody());
        verify(employeeService, times(1)).exportEmployeesToCSV(department, year, "employees.csv");
    }
}