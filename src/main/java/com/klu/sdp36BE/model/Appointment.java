package com.klu.sdp36BE.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String status; // Pending, Confirmed, Completed

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Appointment() {}

    public Appointment(String studentName, String doctorName, LocalDateTime appointmentDate, String status, User user) {
        this.studentName = studentName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
