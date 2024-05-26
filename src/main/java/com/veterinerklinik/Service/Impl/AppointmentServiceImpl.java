package com.veterinerklinik.Service.Impl;

import com.veterinerklinik.Service.AppointmentService;
import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.utiles.Msg;
import com.veterinerklinik.Repository.AppointmentRepository;
import com.veterinerklinik.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepo;

    // Constructor
    public AppointmentServiceImpl(AppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    // Randevu kaydetme işlemi
    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepo.save(appointment);
    }

    // ID'ye göre randevu getirme işlemi
    @Override
    public Appointment get(Long id) {
        return this.appointmentRepo.findById(id).orElseThrow(() -> new NotFoundException(Msg.NOT_FOUND));
    }

    // Sayfalı randevu listesi için placeholder metodu
    @Override
    public Page<Appointment> cursor(int page, int pageSize) {
        return null;
    }

    // Randevu güncelleme işlemi
    @Override
    public Appointment update(Appointment appointment) {
        return null;
    }

    // Randevu silme işlemi
    @Override
    public boolean delete(long id) {
        Appointment appointment = this.get(id);
        this.appointmentRepo.delete(appointment);
        return true;
    }

    // Doktor ID'sine göre randevuları getirme işlemi
    @Override
    public List<Appointment> getByDoctorId(Long doctorId) {
        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException(Msg.NOT_FOUND);
        }
        return this.appointmentRepo.findByDoctorId(doctorId);
    }

    // Hayvan ID'sine göre randevuları getirme işlemi
    @Override
    public List<Appointment> getByAnimalId(Long animalId) {
        if (animalId == null || animalId <= 0) {
            throw new IllegalArgumentException(Msg.NOT_FOUND);
        }
        return this.appointmentRepo.findByAnimalId(animalId);
    }

    // Belirli bir tarih aralığındaki randevuları getirme işlemi
    @Override
    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return appointmentRepo.findByAppointmentDateTimeBetween(startDateTime, endDateTime);
    }

    // Belirli bir tarih ve doktor ID'si için randevu çakışması kontrolü
    @Override
    public boolean isAppointmentConflict(Long doctorId, LocalDateTime dateTime) {
        List<Appointment> conflictingAppointments = appointmentRepo.findByDoctorIdAndAppointmentDateTime(doctorId, dateTime);
        return !conflictingAppointments.isEmpty();
    }

    // Belirli bir tarih aralığı ve doktor ID'si için randevuları getirme işlemi
    @Override
    public List<Appointment> getAppointmentsByDateRangeAndDoctorId(LocalDate startDate, LocalDate endDate, Long doctorId) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
        return appointmentRepo.findByDateRangeAndDoctorId(startDateTime, endDateTime, doctorId);
    }

    // Belirli bir tarih aralığı ve hayvan ID'si için randevuları getirme işlemi
    @Override
    public List<Appointment> getAppointmentsByDateRangeAndAnimalId(LocalDate startDate, LocalDate endDate, Long animalId) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
        return appointmentRepo.findByDateRangeAndAnimalId(startDateTime, endDateTime, animalId);
    }
}
