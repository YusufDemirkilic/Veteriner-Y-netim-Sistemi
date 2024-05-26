package com.veterinerklinik.Service.Impl;

import com.veterinerklinik.Service.VaccineService;
import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.utiles.Msg;
import com.veterinerklinik.Repository.AnimalRepository;
import com.veterinerklinik.Repository.VaccineRepository;
import com.veterinerklinik.entity.Animal;
import com.veterinerklinik.entity.Vaccine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// Aşı servis uygulama sınıfı
@Service
@RequiredArgsConstructor
@Slf4j
public class VaccineServiceImpl implements VaccineService {

    // VaccineRepository ve AnimalRepository bağımlılıkları
    private final VaccineRepository vaccineRepo;
    private final AnimalRepository animalRepo;

    // Aşıyı kaydetme işlemi
    @Override
    public Vaccine save(Vaccine vaccine, Long animalId) {
        log.info("vaccine : {},animalId : {}", vaccine, animalId);
        Animal animal = animalRepo.findById(animalId)
                .orElseThrow(() -> new NotFoundException(Msg.NOT_FOUND));
        vaccine.setAnimal(animal);
        log.info("animal : {}", animal);
        return vaccineRepo.save(vaccine);
    }

    // Belirli bir ID'ye göre aşı getirme işlemi
    @Override
    public Vaccine get(Long id) {
        return this.vaccineRepo.findById(id).orElseThrow(() -> new NotFoundException(Msg.NOT_FOUND));
    }

    // Sayfalı aşı listesi alma işlemi
    @Override
    public Page<Vaccine> cursor(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.vaccineRepo.findAll(pageable);
    }

    // Aşı güncelleme işlemi
    @Override
    public Vaccine update(Vaccine vaccine) {
        this.get(vaccine.getId());
        return this.vaccineRepo.save(vaccine);
    }

    // Aşı silme işlemi
    @Override
    public boolean delete(long id) {
        Vaccine vaccine = this.get(id);
        this.vaccineRepo.delete(vaccine);
        return true;
    }

    // Belirli bir hayvan ID'sine göre aşıları getirme işlemi
    @Override
    public List<Vaccine> getVaccinesByAnimalId(Long animalId) {
        return vaccineRepo.findByAnimalId(animalId);
    }

    // Belirli bir hayvan ve tarih aralığına göre aşıları getirme işlemi
    @Override
    public List<Vaccine> getVaccinesByDateRangeForAnimal(Long animalId, LocalDate startDate, LocalDate endDate) {
        return vaccineRepo.findByAnimalIdAndProtectionStartDateBetween(animalId, startDate, endDate);
    }

    // Tarih aralığına göre aşıları getirme işlemi
    @Override
    public List<Vaccine> getVaccinesByDateRange(LocalDate startDate, LocalDate endDate) {
        return vaccineRepo.findByProtectionStartDateBetween(startDate, endDate);
    }

    // Tarih aralığına göre koruma başlangıç tarihine sahip aşıları getirme işlemi
    @Override
    public List<Vaccine> getByProtectionStartDateBetween(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, Msg.NOT_FOUND);
        Objects.requireNonNull(endDate, Msg.NOT_FOUND);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(Msg.HISTORY_CONTROLLER);
        }

        return vaccineRepo.findByProtectionStartDateBetween(startDate, endDate);

    }

    // Belirli bir hayvan ve tarih aralığına göre aşıları getirme işlemi
    @Override
    public List<Vaccine> findByAnimalIdAndProtectionStartDateBetween(Long animalId, LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(animalId, Msg.NOT_FOUND);
        Objects.requireNonNull(startDate, Msg.NOT_FOUND);
        Objects.requireNonNull(endDate, Msg.NOT_FOUND);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(Msg.HISTORY_CONTROLLER);
        }
        return vaccineRepo.findByAnimalIdAndProtectionStartDateBetween(animalId, startDate, endDate);
    }

    // Belirli bir hayvan ID'sine ve aşı koduna göre aktif aşının varlığını kontrol etme işlemi
    @Override
    public boolean existsActiveVaccineByAnimalIdAndVaccineCode(Long animalId, String vaccineCode) {
        return vaccineRepo.existsByAnimalIdAndCodeAndProtectionFinishDateAfter(animalId, vaccineCode, LocalDate.now());
    }
}
