package com.veterinerklinik.Controller;

import com.veterinerklinik.Service.DoctorService;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.Dto.request.Doctor.DoctorSaveRequest;
import com.veterinerklinik.Dto.request.Doctor.DoctorUpdateRequest;
import com.veterinerklinik.Dto.response.DoctorResponse;
import com.veterinerklinik.entity.Doctor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/doctor")
@RequiredArgsConstructor
public class DoctorConroller {

    // DoctorService ve IModelMapperService bağımlılıkları için final değişkenler
    private final DoctorService doctorService;
    private final IModelMapperService modelMapper;

    // Belirli bir doktoru ID ile getirme işlemini gerçekleştiren endpoint
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<DoctorResponse> get(@PathVariable("id") Long id) {
        Doctor doctor = this.doctorService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(doctor, DoctorResponse.class));
    }

    // Yeni bir doktor oluşturma işlemini gerçekleştiren endpoint
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<DoctorResponse> save(@Valid @RequestBody DoctorSaveRequest doctorSaveRequest) {
        Doctor saveDoctor = this.modelMapper.forRequest().map(doctorSaveRequest, Doctor.class);
        this.doctorService.save(saveDoctor);
        return ResultHelper.created(this.modelMapper.forResponse().map(saveDoctor, DoctorResponse.class));
    }

    // Belirli bir doktoru güncelleme işlemini gerçekleştiren endpoint
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<DoctorResponse> update(@Valid @RequestBody DoctorUpdateRequest doctorUpdateRequest) {
        Doctor updateDoctor = this.modelMapper.forRequest().map(doctorUpdateRequest, Doctor.class);
        this.doctorService.update(updateDoctor);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateDoctor, DoctorResponse.class));
    }

    // Belirli bir doktoru silme işlemini gerçekleştiren endpoint
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") Long id) {
        this.doctorService.delete(id);
        return ResultHelper.Ok();
    }
}
