package com.ssafy.los.backend.domain.repository.alert;

import com.ssafy.los.backend.domain.entity.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertTypeRepository extends JpaRepository<AlertType, Long> {

}