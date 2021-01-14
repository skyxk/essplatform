package com.chen.dao;

import com.chen.entity.Algorithm;
import com.chen.entity.CertSource;
import com.chen.entity.Certificate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateMapper {
    Certificate findCertificateById(String cert_id);

    int addCertificate(Certificate certificate);
    List<Algorithm> findAlgorithm();

    List<CertSource> findCertSource();

    int updateCertificate(Certificate certificate);
}
