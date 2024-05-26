package com.veterinerklinik.Service.Impl;

import com.veterinerklinik.Service.AvailableDateService;
import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.utiles.Msg;
import com.veterinerklinik.Repository.AvailableDateRepository;
import com.veterinerklinik.entity.AvailableDate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// AvailableDateService arayüzünün uygulama sınıfı
@Service
public class AvailableDateServiceImpl implements AvailableDateService {

    // AvailableDateRepository bağımlılığı
    private final AvailableDateRepository availableDateRepo;

    // Constructor
    public AvailableDateServiceImpl(AvailableDateRepository availableDateRepo) {
        this.availableDateRepo = availableDateRepo;
    }

    // AvailableDate kaydetme işlemi
    @Override
    public AvailableDate save(AvailableDate availableDate) {
        return availableDateRepo.save(availableDate);
    }

    // Belirli bir ID'ye göre AvailableDate getirme işlemi
    @Override
    public AvailableDate get(Long id) {
        return this.availableDateRepo.findById(id).orElseThrow(() -> new NotFoundException(Msg.NOT_FOUND));
    }

    // AvailableDate silme işlemi
    @Override
    public boolean delete(long id) {
        AvailableDate availableDate = this.get(id);
        this.availableDateRepo.delete(availableDate);
        return true;
    }

    // AvailableDate güncelleme işlemi
    @Override
    public AvailableDate update(AvailableDate availableDate) {
        return null;
    }

    // Belirli bir doktorun belirli bir tarihte müsait olup olmadığını kontrol etme işlemi
    @Override
    public boolean isDoctorAvailableOnDate(Long doctorId, LocalDate date) {
        List<AvailableDate> availableDates = availableDateRepo.findByDoctorIdAndAvailableDate(doctorId, date);
        return !availableDates.isEmpty();
    }
}
