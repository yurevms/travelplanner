package ru.travelplanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Entity(name = "tour_packages")
@Table(name = "tour_packages")
public class TourPackage {
    @Id
    @GeneratedValue(generator = "tour_package_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "tour_package_id_seq", sequenceName = "tour_package_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "durationInWeeks")
    private int durationInWeeks;

    @Column(name = "cntPeople")
    private int cntPeople;

    @Column(name = "hotelDescription")
    private String hotelDescription;

    @Column(name = "price")
    private int price;
}
