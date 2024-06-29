package ru.travelplanner.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.travelplanner.model.TourPackage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TourPackageRepository extends PagingAndSortingRepository<TourPackage, Long> {
    List<TourPackage> findByPriceLessThanEqualAndCntPeopleAndDurationInWeeks(int price, int numberOfPeople, int durationInWeeks);
    TourPackage save(TourPackage tourPackage);
    Optional<TourPackage> findById(Long id);
    void deleteById(Long id);
}
