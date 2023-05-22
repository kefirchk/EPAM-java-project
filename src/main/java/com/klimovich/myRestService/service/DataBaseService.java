package com.klimovich.myRestService.service;

import com.klimovich.myRestService.database.DBEquation;
import com.klimovich.myRestService.entity.Equation;
import com.klimovich.myRestService.database.EquationRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@Data
public class DataBaseService {
    @Autowired
    private EquationRepository repository;

    public void saveAllEquationsInDataBase(List<Equation> listOfEquations) {
        listOfEquations.forEach(this::saveEquationInDataBase);
    }

    public void saveEquationInDataBase(Equation equation) {
        DBEquation dbEquation = new DBEquation(equation.getNumbers().getFirst(),
                equation.getNumbers().getSecond(), equation.getSegment().getFirst(),
                equation.getSegment().getSecond(), equation.getAnswer());
        repository.save(dbEquation);
    }

    public List<DBEquation> getAllEquations() {
        return repository.findAll();
    }

    public synchronized Integer getLastId() {
        return repository.findAll().size();
    }

}
