package com.mcit.AdmissionSystem.repository;

import com.mcit.AdmissionSystem.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CSRepository extends JpaRepository <CS,CSKey> {

}
