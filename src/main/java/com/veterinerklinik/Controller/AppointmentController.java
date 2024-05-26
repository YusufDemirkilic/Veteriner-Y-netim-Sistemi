package com.veterinerklinik.Controller;

import com.veterinerklinik.Dto.request.Appointment.AppointmentSaveRequest;
import com.veterinerklinik.Dto.request.Appointment.AppointmentUpdateRequest;
import com.veterinerklinik.Dto.response.AppointmentResponse;
import com.veterinerklinik.Service.AnimalService;
import com.veterinerklinik.Service.AppointmentService;
import com.veterinerklinik.Service.AvailableDateService;
import com.veterinerklinik.Service.DoctorService;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.entity.Animal;
import com.veterinerklinik.entity.Appointment;
import com.veterinerklinik.entity.Doctor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final IModelMapperService modelMapper;
    private final DoctorService doctorService;
    private final AnimalService animalService;
    private final AvailableDateService availableDateService;

    // Belirli bir doktorun randevularını getiren endpoint
    @GetMapping("/filter/doctor/{doctorId}")
    public ResultData<List<AppointmentResponse>> getAppointmentsByDoctorId(@PathVariable("doctorId") long doctorId) {
        List<Appointment> appointments = appointmentService.getByDoctorId(doctorId);
        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(appointment -> modelMapper.forResponse().map(appointment, AppointmentResponse.class))
                .collect(Collectors.toList());
        return ResultHelper.success(appointmentResponses);
    }

    // Belirli bir hayvanın randevularını getiren endpoint
    @GetMapping("/filter/animal/{animalId}")
    public ResultData<List<AppointmentResponse>> getAppointmentsByAnimalId(@PathVariable("animalId") long animalId) {
        List<Appointment> appointments = appointmentService.getByAnimalId(animalId);
        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(appointment -> modelMapper.forResponse().map(appointment, AppointmentResponse.class))
                .collect(Collectors.toList());
        return ResultHelper.success(appointmentResponses);
    }

    // Belirli bir randevunun detaylarını getiren endpoint
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AppointmentResponse> get(@PathVariable("id") Long id) {
        Appointment appointment = this.appointmentService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(appointment, AppointmentResponse.class));
    }

    // Belirli bir tarihte ve doktorda randevuları getiren endpoint
    @GetMapping("/filter/dateANDdoctor/appointment")
    public ResultData<List<AppointmentResponse>> getAppointmentsByDateRangeDoctor(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "doctorId", required = false) Long doctorId) {
        List<Appointment> appointments;
        if (doctorId != null) {
            appointments = appointmentService.getAppointmentsByDateRangeAndDoctorId(startDate, endDate, doctorId);
        } else {
            appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        }

        if (appointments.isEmpty()) {
            return ResultHelper.errorWithData("Belirtilen tarih aralığında randevu bulunamadı.", null, HttpStatus.NOT_FOUND);
        }

        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(appointment -> modelMapper.forResponse().map(appointment, AppointmentResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(appointmentResponses);
    }

    // Belirli bir tarihte ve hayvanda randevuları getiren endpoint
    @GetMapping("/filter/dateAnimal")
    public ResultData<List<AppointmentResponse>> getAppointmentsByDateRangeAnimal(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "animalId", required = false) Long animalId) {
        List<Appointment> appointments;
        if (animalId != null) {
            appointments = appointmentService.getAppointmentsByDateRangeAndAnimalId(startDate, endDate, animalId);
        } else {
            appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        }

        if (appointments.isEmpty()) {
            return ResultHelper.errorWithData("Belirtilen tarih aralığında randevu bulunamadı.", null, HttpStatus.NOT_FOUND);
        }

        List<AppointmentResponse> appointmentResponses = appointments.stream()
                .map(appointment -> modelMapper.forResponse().map(appointment, AppointmentResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(appointmentResponses);
    }

    // Hayvan ismine göre randevuları getiren endpoint
    @GetMapping("/filterAnimalName/{animalName}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<AppointmentResponse>> getAnimalsByCustomerName(@PathVariable("animalName") String animalName) {
        Optional<Animal> animalOptional = this.animalService.getAnimalByName(animalName);

        if (animalOptional.isPresent()) {
            Animal animal = animalOptional.get();
            List<Appointment> appointments = appointmentService.getByAnimalId(animal.getId());
            List<AppointmentResponse> appointmentResponses = appointments.stream()
                    .map(appointment -> modelMapper.forResponse().map(appointment, AppointmentResponse.class))
                    .collect(Collectors.toList());

            return ResultHelper.success(appointmentResponses);

        } else {
            return ResultHelper.errorWithData("Hayvan bulunamadı.", null, HttpStatus.NOT_FOUND);
        }
    }

    // Yeni randevu oluşturma endpoint'i
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<AppointmentResponse> save(@Valid @RequestBody AppointmentSaveRequest appointmentSaveRequest) {
        Appointment saveAppointment = modelMapper.forResponse().map(appointmentSaveRequest, Appointment.class);

        // Doktorun müsait günlerini kontrol etme
        Doctor doctor = doctorService.get(appointmentSaveRequest.getDoctorId());
        if (!availableDateService.isDoctorAvailableOnDate(doctor.getId(), appointmentSaveRequest.getAppointmentDateTime().toLocalDate())) {
            return ResultHelper.errorWithData("Doktor müsait değil", null, HttpStatus.BAD_REQUEST);
        }
        if (appointmentService.isAppointmentConflict(doctor.getId(), appointmentSaveRequest.getAppointmentDateTime())) {
            return ResultHelper.errorWithData("Doktorun bu saatte başka bir randevusu var", null, HttpStatus.BAD_REQUEST);
        }

        saveAppointment.setDoctor(doctor);
        Animal animal = animalService.get(appointmentSaveRequest.getAnimalId());
        saveAppointment.setAnimal(animal);

        appointmentService.save(saveAppointment);
        return ResultHelper.created(modelMapper.forResponse().map(saveAppointment, AppointmentResponse.class));
    }

    // Randevu güncelleme endpoint'i
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AppointmentResponse> update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateRequest appointmentUpdateRequest) {
        Appointment updateAppointment = this.modelMapper.forRequest().map(appointmentUpdateRequest, Appointment.class);

        Doctor doctor = this.doctorService.get(appointmentUpdateRequest.getDoctorId());
        Animal animal = this.animalService.get(appointmentUpdateRequest.getAnimalId());
        updateAppointment.setDoctor(doctor);
        updateAppointment.setAnimal(animal);
        updateAppointment.setId(id);

        this.appointmentService.update(updateAppointment);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateAppointment, AppointmentResponse.class));
    }

    // Randevu silme endpoint'i
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") Long id) {
        this.availableDateService.delete(id);
        return ResultHelper.Ok();
    }
}
