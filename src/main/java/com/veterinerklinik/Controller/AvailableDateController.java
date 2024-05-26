package com.veterinerklinik.Controller;

import com.veterinerklinik.Service.AvailableDateService;
import com.veterinerklinik.Service.DoctorService;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.Dto.request.Available.AvailableDataUpdateRequest;
import com.veterinerklinik.Dto.request.Available.AvailableDateSaveRequest;
import com.veterinerklinik.Dto.response.AvailableDataResponse;
import com.veterinerklinik.entity.AvailableDate;
import com.veterinerklinik.entity.Doctor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/available-date")
@RequiredArgsConstructor
public class AvailableDateController {
    private final AvailableDateService availableDateService;
    private final DoctorService doctorService;
    private final IModelMapperService modelMapper;

    /**
     * Belirli bir müsaitlik tarihini ID ile getirir.
     *
     * @param id Müsaitlik tarihinin ID'si
     * @return Müsaitlik tarihinin detaylarını içeren Response
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AvailableDataResponse> get(@PathVariable("id") Long id) {
        AvailableDate availableDate = this.availableDateService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(availableDate, AvailableDataResponse.class));
    }

    /**
     * Yeni bir müsaitlik tarihi oluşturur.
     *
     * @param availableDateSaveRequest Müsaitlik tarihi oluşturma isteği
     * @return Oluşturulan müsaitlik tarihinin detaylarını içeren Response
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<AvailableDataResponse> save(@Valid @RequestBody AvailableDateSaveRequest availableDateSaveRequest) {
        // AvailableDateSaveRequest sınıfını AvailableDate sınıfına dönüştürüp kaydeder
        AvailableDate saveAvailableDate = this.modelMapper.forRequest().map(availableDateSaveRequest, AvailableDate.class);

        // İlgili doktoru getirip müsaitlik tarihine atar
        Doctor doctor = this.doctorService.get(availableDateSaveRequest.getDoctorId());
        saveAvailableDate.setDoctor(doctor);

        this.availableDateService.save(saveAvailableDate);
        return ResultHelper.created(this.modelMapper.forResponse().map(saveAvailableDate, AvailableDataResponse.class));
    }

    /**
     * Belirli bir müsaitlik tarihini günceller.
     *
     * @param id Müsaitlik tarihinin ID'si
     * @param availableDataUpdateRequest Müsaitlik tarihi güncelleme isteği
     * @return Güncellenen müsaitlik tarihinin detaylarını içeren Response
     */
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AvailableDataResponse> update(@PathVariable Long id, @Valid @RequestBody AvailableDataUpdateRequest availableDataUpdateRequest) {
        // AvailableDataUpdateRequest sınıfını AvailableDate sınıfına dönüştürüp günceller
        AvailableDate updateAvailableDate = this.modelMapper.forRequest().map(availableDataUpdateRequest, AvailableDate.class);

        // İlgili doktoru getirip güncellenen müsaitlik tarihine atar
        Doctor doctor = this.doctorService.get(availableDataUpdateRequest.getDoctorId());
        updateAvailableDate.setDoctor(doctor);

        // ID'yi güncellenen müsaitlik tarihine atar
        updateAvailableDate.setId(id);

        this.availableDateService.update(updateAvailableDate);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateAvailableDate, AvailableDataResponse.class));
    }

    /**
     * Belirli bir müsaitlik tarihini siler.
     *
     * @param id Müsaitlik tarihinin ID'si
     * @return Silme işleminin sonucunu içeren Response
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") Long id) {
        this.availableDateService.delete(id);
        return ResultHelper.Ok();
    }
}
