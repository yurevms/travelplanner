package ru.travelplanner.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "booking_forms")
@Table(name = "booking_forms")
public class BookingForm {
    @Id
    @GeneratedValue(generator = "booking_form_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "booking_form_id_seq", sequenceName = "booking_form_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "tour_package_id")
    private TourPackage tourPackage;
}
