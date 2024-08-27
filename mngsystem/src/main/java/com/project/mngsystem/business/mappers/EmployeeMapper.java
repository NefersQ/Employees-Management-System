package com.project.mngsystem.business.mappers;

import com.project.mngsystem.business.repository.model.EmployeeDAO;
import com.project.mngsystem.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDAO employeeToEmployeeDAO(Employee employee){
        if ( employee == null ) {
            return null;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();

        employeeDAO.setId( employee.getId() );
        employeeDAO.setName( employee.getName() );
        employeeDAO.setDepartment( employee.getDepartment() );
        employeeDAO.setJoinDate( employee.getJoinDate() );

        return employeeDAO;
    }
    public Employee employeeDAOToEmployee(EmployeeDAO employeeDAO) {
            if ( employeeDAO == null ) {
                return null;
            }

            Employee employee = new Employee();

            employee.setId( employeeDAO.getId() );
            employee.setName( employeeDAO.getName() );
            employee.setDepartment( employeeDAO.getDepartment() );
            employee.setJoinDate( employeeDAO.getJoinDate() );

            return employee;
        }
}
