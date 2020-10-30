package com.chen.service.impl;

import com.chen.dao.IUnitRoleAndPowerRelationDao;
import com.chen.entity.UnitRoleAndPowerRelation;
import com.chen.service.IRoleAndPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleAndPowerServiceImpl implements IRoleAndPowerService {
    @Autowired
    private IUnitRoleAndPowerRelationDao unitRoleAndPowerRelationDao;
    @Override
    public UnitRoleAndPowerRelation queryByRoleIdAndPowerIdAndTopUnitId(String roleId, String powerId, String unitId) {

        UnitRoleAndPowerRelation unitRoleAndPowerRelation = new UnitRoleAndPowerRelation();
        unitRoleAndPowerRelation.setRoleId(roleId);
        unitRoleAndPowerRelation.setPowerId(powerId);
        unitRoleAndPowerRelation.setTopUnitId(unitId);

        return unitRoleAndPowerRelationDao.findUnitRoleAndPowerRelation(unitRoleAndPowerRelation);
    }
}
