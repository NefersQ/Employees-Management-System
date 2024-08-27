package com.project.mngsystem.business.handlers;

public class EmployeeNotFoundException extends RuntimeException{
        public EmployeeNotFoundException(String message) {
            super(message);
        }
}
