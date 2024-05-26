package com.veterinerklinik.Repository;




import com.veterinerklinik.entity.AvailableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailableDateRepository extends JpaRepository<AvailableDate,Long> {
    List<AvailableDate> findByDoctorIdAndAvailableDate(Long doctorId, LocalDate availableDate);
}
