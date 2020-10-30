package com.chen.platformweb;

import com.chen.entity.Unit;
import com.chen.service.IUnitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {

    @Autowired
    private IUnitService unitService;


    @Test
    public void unitService() throws Exception {
        String unitId = "10110001";
        Unit unit = unitService.findUnitById(unitId);
        System.out.println(unit);
    }
}
