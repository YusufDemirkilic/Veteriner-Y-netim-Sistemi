package com.veterinerklinik.Controller;


import com.veterinerklinik.Service.AnimalService;
import com.veterinerklinik.Service.VaccineService;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.Dto.request.Vaccine.VaccineSaveRequest;
import com.veterinerklinik.Dto.request.Vaccine.VaccineUpdateRequest;
import com.veterinerklinik.Dto.response.VaccineResponse;
import com.veterinerklinik.entity.Animal;
import com.veterinerklinik.entity.Vaccine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/vaccine")
@RequiredArgsConstructor
public class VaccineController {
    private final VaccineService vaccineService;
    private final IModelMapperService modelMapper;
    private final AnimalService animalService;


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<VaccineResponse> save(@Valid @RequestBody VaccineSaveRequest vaccineSaveRequest ){
        Vaccine saveVaccine = this.modelMapper.forRequest().map(vaccineSaveRequest, Vaccine.class);
        Long animalId = vaccineSaveRequest.getAnimalId();
        if (vaccineService.existsActiveVaccineByAnimalIdAndVaccineCode(animalId, vaccineSaveRequest.getCode())) {
            return ResultHelper.errorWithData("Bu hayvan için koruma süresi dolmamış bir aşı zaten mevcut.",null,HttpStatus.BAD_REQUEST);
        }
        Vaccine savedVaccine = this.vaccineService.save(saveVaccine, animalId);
        return ResultHelper.created(this.modelMapper.forResponse().map(savedVaccine, VaccineResponse.class));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<VaccineResponse> get (@PathVariable("id") Long id) {
        Vaccine vaccine = this.vaccineService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(vaccine,VaccineResponse.class));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") Long id) {
        this.vaccineService.delete(id);
        return ResultHelper.Ok();
    }
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<VaccineResponse> update(@PathVariable Long id, @Valid @RequestBody VaccineUpdateRequest vaccineUpdateRequest) {
        Vaccine updateVaccine = this.modelMapper.forRequest().map(vaccineUpdateRequest, Vaccine.class);

        Animal animal = this.animalService.get(vaccineUpdateRequest.getAnimalId());
        updateVaccine.setAnimal(animal);

        updateVaccine.setId(id);

        this.vaccineService.update(updateVaccine);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateVaccine, VaccineResponse.class));
    }
    @GetMapping("/animal/{animalId}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<VaccineResponse>> getVaccinesByAnimalId(@PathVariable("animalId") Long animalId) {
        List<Vaccine> vaccines = this.vaccineService.getVaccinesByAnimalId(animalId);

        List<VaccineResponse> vaccineResponses = vaccines.stream()
                .map(vaccine -> this.modelMapper.forResponse().map(vaccine, VaccineResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(vaccineResponses);
    }

    @GetMapping("/date-range")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<VaccineResponse>> getVaccinesByDateRange(
            @RequestParam("start-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Vaccine> vaccines = vaccineService.getByProtectionStartDateBetween(startDate, endDate);
        List<VaccineResponse> vaccineResponses = vaccines.stream()
                .map(vaccine -> modelMapper.forResponse().map(vaccine, VaccineResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(vaccineResponses);
    }
    @GetMapping("/animal/filter/date")
    public ResultData<List<VaccineResponse>> getVaccinesByAnimalAndDateRange(
            @RequestParam("animalId") Long animalId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Vaccine> vaccines = vaccineService.findByAnimalIdAndProtectionStartDateBetween(animalId, startDate, endDate);
        List<VaccineResponse> vaccineResponses = vaccines.stream()
                .map(vaccine -> modelMapper.forResponse().map(vaccine, VaccineResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(vaccineResponses);
    }

}