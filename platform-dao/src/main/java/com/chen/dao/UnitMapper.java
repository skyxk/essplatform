package com.chen.dao;

import com.chen.entity.Unit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitMapper {

    Unit findUnitById(@Param("unit_id")	String unitId);

    List<Unit> findUnitByParentUnitId(@Param("unit_id")	String unitId);

    List<Unit> findUnitList();

    Unit findUnitByName(@Param("unitName")String unitName);
}
