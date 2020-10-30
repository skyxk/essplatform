package com.chen.service;

import com.chen.entity.Algorithm;
import com.chen.entity.CertSource;
import com.chen.entity.Certificate;

import java.util.List;

public interface ICertificateService {
    boolean addCertificate(Certificate certificate);

    Certificate findCertificateById(String cert_id);

    List<Algorithm> findAlgorithm();

    List<CertSource> findCertSource();
}
