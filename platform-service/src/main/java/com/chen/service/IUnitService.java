package com.chen.service;

import com.chen.entity.Unit;
import com.chen.entity.ZTree;

import java.util.List;

public interface IUnitService {

    Unit findUnitById(String unitId);

    List<Unit> findAllUnitByParentUnitId(String unitId);

    List<ZTree> findUnitMenu(String unitId);

    Unit queryCompanyUnitByUserParentUnitId(String parentUnitId);

    List<Unit> findUnitList();

    Unit findUnitByName(String unitName);
}
