package com.example.bmsbookticket.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
public class Show extends BaseModel{
    @ManyToOne
    private Movie movie;
    private Date startTime;
    private Date endTime;
    @OneToMany
    private List<Feature> features;
    @OneToMany
    private Screen screen;
}
