package com.project.mngsystem.business.service.impl;

import com.opencsv.CSVWriter;
import com.project.mngsystem.business.mappers.EmployeeMapper;
import com.project.mngsystem.business.repository.EmployeeRepository;
import com.project.mngsystem.business.repository.model.EmployeeDAO;
import com.project.mngsystem.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDAO employeeDAO;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeDAO = new EmployeeDAO(1L, "John Doe", "IT", LocalDate.of(2020, 1, 1));
        employee = new Employee(1L, "John Doe", "IT", LocalDate.of(2020, 1, 1));
    }

    @Test
    void testGetAllEmployees_withFilter() {
        when(employeeRepository.findByDepartmentAndJoinDateAfter("IT", LocalDate.of(2020, 1, 1)))
                .thenReturn(Arrays.asList(employeeDAO));
        when(employeeMapper.employeeDAOToEmployee(employeeDAO)).thenReturn(employee);


        List<Employee> employees = employeeService.getAllEmployees("IT", LocalDate.of(2020, 1, 1));


        assertEquals(1, employees.size());
        assertEquals(employee, employees.get(0));
    }

    @Test
    void testGetAllEmployees_withoutFilter() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeDAO));
        when(employeeMapper.employeeDAOToEmployee(employeeDAO)).thenReturn(employee);

        List<Employee> employees = employeeService.getAllEmployees(null, null);

        assertEquals(1, employees.size());
        assertEquals(employee, employees.get(0));
    }

    @Test
    void testCreateEmployee() {
        when(employeeMapper.employeeToEmployeeDAO(employee)).thenReturn(employeeDAO);
        when(employeeRepository.save(employeeDAO)).thenReturn(employeeDAO);
        when(employeeMapper.employeeDAOToEmployee(employeeDAO)).thenReturn(employee);

        Employee createdEmployee = employeeService.createEmployee(employee);

        assertEquals(employee, createdEmployee);
        verify(employeeRepository).save(employeeDAO);
    }

    @Test
    void testDeleteEmployee_Exists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeDAO));

        String message = employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
        assertEquals("Employee successfully deleted.", message);
    }

    @Test
    void testDeleteEmployee_NotExist() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(1L));
        assertEquals("Employee not found or has left the organization.", exception.getMessage());
    }

    @Test
    void testExportEmployeesToCSV() throws IOException {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeRepository.findByDepartmentAndJoinDateAfter("IT", LocalDate.of(2020, 1, 1)))
                .thenReturn(Arrays.asList(employeeDAO));
        when(employeeMapper.employeeDAOToEmployee(employeeDAO)).thenReturn(employee);

        try (MockedConstruction<FileWriter> fileWriterMockedConstruction = mockConstruction(FileWriter.class, (mock, context) -> {
        });
             MockedConstruction<CSVWriter> csvWriterMockedConstruction = mockConstruction(CSVWriter.class, (mock, context) -> {
                 doNothing().when(mock).writeNext(any(String[].class));
             })) {

            employeeService.exportEmployeesToCSV("IT", LocalDate.of(2020, 1, 1), "test.csv");

            CSVWriter mockCsvWriter = csvWriterMockedConstruction.constructed().get(0);
            verify(mockCsvWriter).writeNext(new String[]{"ID", "Name", "Department", "Join Date"});
            verify(mockCsvWriter).writeNext(new String[]{"1", "John Doe", "IT", "2020-01-01"});
        }
    }
}