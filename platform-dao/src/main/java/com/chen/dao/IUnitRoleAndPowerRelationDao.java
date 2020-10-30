package com.chen.dao;


import com.chen.entity.UnitRoleAndPowerRelation;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnitRoleAndPowerRelationDao {

    UnitRoleAndPowerRelation findUnitRoleAndPowerRelation(UnitRoleAndPowerRelation unitRoleAndPowerRelation);
}
