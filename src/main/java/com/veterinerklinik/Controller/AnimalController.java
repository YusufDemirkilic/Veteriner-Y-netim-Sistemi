package com.veterinerklinik.Controller;
import com.veterinerklinik.Service.AnimalService;
import com.veterinerklinik.Service.CustomerService;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.Dto.request.Animal.AnimalSaveRequest;
import com.veterinerklinik.Dto.request.Animal.AnimalUpdateRequest;
import com.veterinerklinik.Dto.response.AnimalResponse;
import com.veterinerklinik.Dto.response.VaccineResponse;
import com.veterinerklinik.entity.Animal;
import com.veterinerklinik.entity.Customer;
import com.veterinerklinik.entity.Vaccine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/animal")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;
    private final CustomerService customerService;
    private final IModelMapperService modelMapper;

    // Belirli bir hayvanın detaylarını getiren endpoint
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AnimalResponse> get (@PathVariable("id") Long id) {
        Animal animal = this.animalService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(animal, AnimalResponse.class));
    }

    // Yeni hayvan ekleme endpoint'i
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<AnimalResponse> save(@Valid @RequestBody AnimalSaveRequest animalSaveRequest ){
        // AnimalSaveRequest nesnesini Animal nesnesine dönüştür
        Animal saveAnimal = this.modelMapper.forRequest().map(animalSaveRequest, Animal.class);

        // İlgili müşteri bilgisini al ve hayvana ata
        Customer customer = this.customerService.get(animalSaveRequest.getCustomerId());
        saveAnimal.setCustomer(customer);

        // Hayvanı kaydet
        this.animalService.save(saveAnimal);
        return ResultHelper.created(this.modelMapper.forResponse().map(saveAnimal, AnimalResponse.class));
    }

    // Hayvan güncelleme endpoint'i
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<AnimalResponse> update(@PathVariable Long id, @Valid @RequestBody AnimalUpdateRequest animalUpdateRequest) {
        // AnimalUpdateRequest nesnesini Animal nesnesine dönüştür
        Animal updateAnimal = this.modelMapper.forRequest().map(animalUpdateRequest, Animal.class);

        // Yeni müşteri kimliğini al ve hayvanın müşteri bilgilerini güncelle
        Customer customer = this.customerService.get(animalUpdateRequest.getCustomerId());
        updateAnimal.setCustomer(customer);

        // Hayvanın ID'sini ayarla
        updateAnimal.setId(id);

        // Hayvanı güncelle
        this.animalService.update(updateAnimal);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateAnimal, AnimalResponse.class));
    }

    // Hayvan silme endpoint'i
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") Long id) {
        this.animalService.delete(id);
        return ResultHelper.Ok();
    }

    // Hayvana ait aşıları getiren endpoint
    @GetMapping("/{id}/vaccine")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<VaccineResponse>> getVaccinesForAnimal(@PathVariable("id") Long id) {
        Animal animal = this.animalService.get(id);

        // Hayvana ait tüm aşı kayıtlarını al
        List<Vaccine> vaccines = animal.getVaccines();

        // Bu aşı kayıtlarını VaccineResponse'a dönüştürerek döndür
        List<VaccineResponse> vaccineResponses = vaccines.stream()
                .map(vaccine -> this.modelMapper.forResponse().map(vaccine, VaccineResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(vaccineResponses);
    }

    // Hayvan ismine göre filtreleme endpoint'i
    @GetMapping("/FilterAnimalName")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<AnimalResponse>> getAnimalsByName(@RequestParam("name") String name) {
        List<Animal> animals = this.animalService.getAnimalsByName(name);

        // İsmine göre filtrelenmiş hayvanları AnimalResponse formatına dönüştür
        List<AnimalResponse> animalResponses = animals.stream()
                .map(animal -> this.modelMapper.forResponse().map(animal, AnimalResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(animalResponses);
    }

    // Müşteri kimliğine göre hayvanları filtreleme endpoint'i
    @GetMapping("/customer/filterId{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<AnimalResponse>> getAnimalsByCustomerId(@PathVariable("customerId") Long customerId) {
        List<Animal> animals = this.animalService.getAnimalsByCustomerId(customerId);

        // Sahibinin hayvanlarını AnimalResponse formatına dönüştür
        List<AnimalResponse> animalResponses = animals.stream()
                .map(animal -> this.modelMapper.forResponse().map(animal, AnimalResponse.class))
                .collect(Collectors.toList());

        return ResultHelper.success(animalResponses);
    }

    // Müşteri ismine göre hayvanları filtreleme endpoint'i
    @GetMapping("/customer/filterCustomerName/{customerName}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<AnimalResponse>> getAnimalsByCustomerName(@PathVariable("customerName") String customerName) {
        Optional<Customer> customerOptional = this.customerService.getCustomerByName(customerName);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            List<Animal> animals = this.animalService.getAnimalsByCustomerId(customer.getId());

            List<AnimalResponse> animalResponses = animals.stream()
                    .map(animal -> this.modelMapper.forResponse().map(animal, AnimalResponse.class))
                    .collect(Collectors.toList());

            return ResultHelper.success(animalResponses);
        } else {
            return ResultHelper.errorWithData("Kullanıcı bulunamadı.", null, HttpStatus.NOT_FOUND);
        }
    }
}
