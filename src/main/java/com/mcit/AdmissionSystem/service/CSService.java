package com.mcit.AdmissionSystem.service;

import com.mcit.AdmissionSystem.model.*;
import com.mcit.AdmissionSystem.repository.CSRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CSService {

    private static final Logger log = LoggerFactory.getLogger(CSService.class);

    @Autowired
    CSRepository csRepository;

    public void add(Course course, Student student) {

        CS cps = new CS();
        CSKey csKey = new CSKey(course, student);

        cps.setCsKey(csKey);

        csRepository.save(cps);
    }

    public void delete(CS cps) {

        csRepository.delete(cps);
    }

    public void setMark(CS cps, double mark) {

        cps.setMark(mark);
        csRepository.save(cps);
    }

    public List<CS> findAll() {

        return csRepository.findAll();
    }

    public CS findById(CSKey csKey) {

        return csRepository.findOne(csKey);
    }
}
