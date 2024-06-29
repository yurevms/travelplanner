package ru.travelplanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.travelplanner.model.TourPackage;
import ru.travelplanner.model.BookingForm;
import ru.travelplanner.service.ServiceInterface;
import ru.travelplanner.service.TourPackageService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourPackageController {
    private final TourPackageService tourPackageService;

    @GetMapping("/suitable")
    public ResponseEntity<List<TourPackage>> getSuitableTourPackages(
            @RequestParam(name = "budget")int budget,
            @RequestParam(name = "cntPeople") int cntPeople,
            @RequestParam(name = "durationInWeeks") int durationInWeeks) {
        List<TourPackage> packages = tourPackageService.getSuitableTourPackages(budget, cntPeople, durationInWeeks);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackage> getTourPackageById(@PathVariable Long id) {
        Optional<TourPackage> tourPackage = tourPackageService.getTourPackageById(id);
        return tourPackage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/booking")
    public ResponseEntity<BookingForm> saveBookingForm(@RequestBody BookingForm bookingForm) {
        BookingForm savedForm = tourPackageService.saveBookingForm(bookingForm);
        return ResponseEntity.ok(savedForm);
    }

    @GetMapping("/booking")
    public ResponseEntity<List<BookingForm>> getBookingForms(){
        List<BookingForm> packages = tourPackageService.getBookingForms();
        return ResponseEntity.ok(packages);
    }

    @PostMapping()
    public ResponseEntity<TourPackage> saveTourPackage(@RequestBody TourPackage tourPackage) {
        TourPackage savedTour = tourPackageService.saveTourPackage(tourPackage);
        return ResponseEntity.ok(savedTour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourPackage(@PathVariable Long id) {
        Optional<TourPackage> tourPackageOptional = tourPackageService.getTourPackageById(id);
        if(tourPackageOptional.isPresent()){
            tourPackageService.deleteTourPackageById(id);
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

}
