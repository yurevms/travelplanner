package ru.travelplanner.repository;

import org.springframework.data.repository.CrudRepository;
import ru.travelplanner.model.BookingForm;

public interface BookingFormRepository extends CrudRepository<BookingForm, Long> {
}
