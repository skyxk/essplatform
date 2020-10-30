package com.chen.dao;

import com.chen.entity.IssuerUnit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuerUnitMapper {

    String findIssuerUnitByName(String name);

    IssuerUnit findIssuerUnitById(String unitId);

    IssuerUnit findIssuerUnitBySM2();

    IssuerUnit findIssuerUnitByRSA();

    List<String> findTrustRoot();
}
