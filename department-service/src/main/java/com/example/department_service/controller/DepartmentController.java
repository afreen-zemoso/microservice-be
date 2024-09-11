package com.example.department_service.controller;

import com.example.department_service.client.EmployeeClient;
import com.example.department_service.model.Department;
import com.example.department_service.model.Employee;
import com.example.department_service.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    private final RestTemplate restTemplate;
    private static final Logger LOGGER
            = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private DepartmentRepository repository;

    public DepartmentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public Department add(@RequestBody Department department) {
        LOGGER.info("Department add: {}", department);
        return repository.addDepartment(department);
    }

    @GetMapping
    public List<Department> findAll() {
        LOGGER.info("Department find All");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Department findById(@PathVariable Long id) {
        LOGGER.info("Department find: id={}", id);
        return repository.findById(id);
    }

//    @GetMapping("/with-employees")
//    public List<Department> findAllWithEmployees() {
//        LOGGER.info("Department find All with employees");
//        List<Department> departments
//                = repository.findAll();
//        departments.forEach(department ->
//                department.setEmployees(
//                        employeeClient.findByDepartment(department.getId())));
//        return  departments;
//    }

    @GetMapping("/all")
    public List<Department> findFullDeptData() {
        List<Department> departments = repository.findAll();

        departments.forEach(department -> {
            // Use String.format() or concatenation to build the URL with the department ID
            String serviceUrl = "http://localhost:8082/employee/department/" + department.getId();

            System.out.println(serviceUrl);

            // Ensure getForObject returns an Employee array, and convert it to a list
            Employee[] employeesArray = restTemplate.getForObject(serviceUrl, Employee[].class);
            if (employeesArray != null) {
                department.setEmployees(Arrays.asList(employeesArray));
            } else {
                department.setEmployees(new ArrayList<>()); // Set to empty list if no employees found
            }
        });

        return departments;
    }



}
