package com.LocaDj.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class StatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Status status;
    private LocalDateTime date;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
