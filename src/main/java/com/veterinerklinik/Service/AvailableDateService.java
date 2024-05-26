package com.veterinerklinik.Service;



import com.veterinerklinik.entity.AvailableDate;

import java.time.LocalDate;

public interface AvailableDateService {
    AvailableDate save(AvailableDate availableDate);
    AvailableDate get(Long id);

    boolean delete(long id);

    AvailableDate update(AvailableDate availableDate);

    boolean isDoctorAvailableOnDate(Long doctorId, LocalDate date);
}