package com.klimovich.myRestService.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="equations")
public class DBEquation {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="a")
    private Integer a;

    @Column(name="b")
    private Integer b;

    @Column(name="beginOfSegment")
    private Integer begin;

    @Column(name="endOfSegment")
    private Integer end;

    @Column(name="answer")
    private Integer answer;

    public DBEquation(Integer a, Integer b, Integer begin, Integer end, Integer answer) {
        this.a = a;
        this.b = b;
        this.begin = begin;
        this.end = end;
        this.answer = answer;
    }

    public DBEquation(Integer id, Integer a, Integer b, Integer begin, Integer end, Integer answer) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.begin = begin;
        this.end = end;
        this.answer= answer;
    }

}
