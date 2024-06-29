package ru.travelplanner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.travelplanner.model.TourPackage;
import ru.travelplanner.model.BookingForm;
import ru.travelplanner.repository.TourPackageRepository;
import ru.travelplanner.repository.BookingFormRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourPackageService implements ServiceInterface {
    private final TourPackageRepository tourPackageRepository;
    private final BookingFormRepository bookingFormRepository;

    @Override
    public List<TourPackage> getSuitableTourPackages(int budget, int cntPeople, int durationInWeeks) {
        return tourPackageRepository.findByPriceLessThanEqualAndCntPeopleAndDurationInWeeks(budget, cntPeople, durationInWeeks);
    }

    @Override
    public Optional<TourPackage> getTourPackageById(Long id) {
        return tourPackageRepository.findById(id);
    }

    @Override
    public BookingForm saveBookingForm(BookingForm bookingForm) {
        return bookingFormRepository.save(bookingForm);
    }

    @Override
    public TourPackage saveTourPackage(TourPackage tourPackage) {
        return tourPackageRepository.save(tourPackage);
    }

    @Override
    public void deleteTourPackageById(Long id) {
        tourPackageRepository.deleteById(id);
    }

    @Override
    public List<BookingForm> getBookingForms(){
        return (List<BookingForm>) bookingFormRepository.findAll();
    }
}
