package com.chen.service.impl;

import com.chen.dao.DepartmentMapper;
import com.chen.entity.Department;
import com.chen.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements IDepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> findDepartmentList(Department department) {

        return departmentMapper.findDepartmentList(department);
    }


}
