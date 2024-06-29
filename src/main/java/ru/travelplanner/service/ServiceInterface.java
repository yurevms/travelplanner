package ru.travelplanner.service;

import org.springframework.data.domain.Page;
import ru.travelplanner.model.TourPackage;
import ru.travelplanner.model.BookingForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ServiceInterface {
    List<TourPackage> getSuitableTourPackages(int budget, int numberOfPeople, int durationInWeeks);

    Optional<TourPackage> getTourPackageById(Long id);

    BookingForm saveBookingForm(BookingForm bookingForm);

    TourPackage saveTourPackage(TourPackage tourPackage);

    void deleteTourPackageById(Long id);

    List<BookingForm> getBookingForms();
}
