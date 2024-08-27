package com.project.mngsystem.business.repository;

import com.project.mngsystem.business.repository.model.EmployeeDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDAO,Long> {
    List<EmployeeDAO> findByDepartmentAndJoinDateAfter(String department, LocalDate joinDate);
}
