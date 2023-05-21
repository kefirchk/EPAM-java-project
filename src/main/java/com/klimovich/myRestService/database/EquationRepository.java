package com.klimovich.myRestService.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquationRepository extends JpaRepository<DBEquation, Integer>{
}
