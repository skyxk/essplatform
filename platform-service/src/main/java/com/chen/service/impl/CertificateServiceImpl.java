package com.chen.service.impl;

import com.chen.dao.CertificateMapper;
import com.chen.entity.Algorithm;
import com.chen.entity.CertSource;
import com.chen.entity.Certificate;
import com.chen.service.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateServiceImpl implements ICertificateService {

    @Autowired
    private CertificateMapper certificateMapper;

    @Override
    public boolean addCertificate(Certificate certificate) {
        int result = certificateMapper.addCertificate(certificate);
        if (result==1){
            return true;
        }
        return false;
    }

    @Override
    public Certificate findCertificateById(String cert_id) {
        return certificateMapper.findCertificateById(cert_id);
    }

    @Override
    public List<Algorithm> findAlgorithm() {
        return certificateMapper.findAlgorithm();
    }

    @Override
    public List<CertSource> findCertSource() {
        return certificateMapper.findCertSource();
    }
}
