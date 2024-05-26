package com.veterinerklinik.Service;


import com.veterinerklinik.entity.Doctor;
import org.springframework.data.domain.Page;

public interface DoctorService {
    Doctor save(Doctor doctor);
    Doctor get(Long id);
    Page<Doctor> cursor(int page, int pageSize);
    Doctor update (Doctor doctor);
    boolean delete (long id);
}