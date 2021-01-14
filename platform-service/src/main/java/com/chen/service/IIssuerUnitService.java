package com.chen.service;

import com.chen.entity.IssuerUnit;

import java.util.List;

public interface IIssuerUnitService {

    IssuerUnit findIssuerUnitByName(String name);

    boolean VerifyCert(String certBase64);

    IssuerUnit findIssuerUnitByRSA();

    IssuerUnit findIssuerUnitBySM2();
}
