package com.veterinerklinik.Service.Impl;

import com.veterinerklinik.Service.DoctorService;
import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.utiles.Msg;
import com.veterinerklinik.Repository.DoctorRepository;
import com.veterinerklinik.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Doktor servis uygulama sınıfı
@Service
public class DoctorServiceImpl implements DoctorService {

    // DoctorRepository bağımlılığı
    private final DoctorRepository doctorRepo;

    // Constructor
    public DoctorServiceImpl(DoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    // Doktor kaydetme işlemi
    @Override
    public Doctor save(Doctor doctor) {
        return this.doctorRepo.save(doctor);
    }

    // Belirli bir ID'ye göre doktor getirme işlemi
    @Override
    public Doctor get(Long id) {
        return this.doctorRepo.findById(id).orElseThrow(()-> new NotFoundException(Msg.NOT_FOUND));
    }

    // Sayfalı doktor listesi alma işlemi
    @Override
    public Page<Doctor> cursor(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        return this.doctorRepo.findAll(pageable);
    }

    // Doktor güncelleme işlemi
    @Override
    public Doctor update(Doctor doctor) {
        this.get(doctor.getId());
        return this.doctorRepo.save(doctor);
    }

    // Doktor silme işlemi
    @Override
    public boolean delete(long id) {
        Doctor doctor=this.get(id);
        this.doctorRepo.delete(doctor);
        return true;
    }
}
