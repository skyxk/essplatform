package com.chen.service;


import com.chen.entity.UnitRoleAndPowerRelation;

public interface IRoleAndPowerService {

    UnitRoleAndPowerRelation queryByRoleIdAndPowerIdAndTopUnitId(String roleId, String powerId, String unitId);
}
