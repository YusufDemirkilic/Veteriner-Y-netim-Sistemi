package com.veterinerklinik.Controller;

import com.veterinerklinik.Service.CustomerService;
import com.veterinerklinik.config.EmailAlreadyRegisteredException;
import com.veterinerklinik.config.modelMapper.IModelMapperService;
import com.veterinerklinik.config.result.Result;
import com.veterinerklinik.config.result.ResultData;
import com.veterinerklinik.config.utiles.ResultHelper;
import com.veterinerklinik.Dto.CursorResponse;
import com.veterinerklinik.Dto.request.Customer.CustomerSaveRequest;
import com.veterinerklinik.Dto.request.Customer.CustomerUpdateRequest;
import com.veterinerklinik.Dto.response.CustomerResponse;
import com.veterinerklinik.entity.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final IModelMapperService modelMapper;

    /**
     * Yeni bir müşteri oluşturur.
     *
     * @param customerSaveRequest Müşteri oluşturma isteği
     * @return Oluşturulan müşterinin detaylarını içeren Response
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultData<CustomerResponse> save(@Valid @RequestBody CustomerSaveRequest customerSaveRequest) {
        // E-posta adresi zaten kayıtlıysa istisna fırlatır
        if (customerService.existsByMail(customerSaveRequest.getMail())) {
            throw new EmailAlreadyRegisteredException("Email is already registered");
        }
        // Müşteri bilgilerini Customer nesnesine dönüştürüp kaydeder
        Customer saveCustomer = this.modelMapper.forRequest().map(customerSaveRequest, Customer.class);
        this.customerService.save(saveCustomer);
        return ResultHelper.created(this.modelMapper.forResponse().map(saveCustomer, CustomerResponse.class));
    }

    /**
     * Belirli bir müşteriyi ID ile getirir.
     *
     * @param id Müşterinin ID'si
     * @return Müşterinin detaylarını içeren Response
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<CustomerResponse> get(@PathVariable("id") long id) {
        // ID'ye göre müşteri bilgisini getirir
        Customer customer = this.customerService.get(id);
        return ResultHelper.success(this.modelMapper.forResponse().map(customer, CustomerResponse.class));
    }

    /**
     * Belirli bir müşteriyi günceller.
     *
     * @param customerUpdateRequest Müşteri güncelleme isteği
     * @return Güncellenen müşterinin detaylarını içeren Response
     */
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<CustomerResponse> update(@Valid @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        // Müşteri bilgilerini Customer nesnesine dönüştürüp günceller
        Customer updateCustomer = this.modelMapper.forRequest().map(customerUpdateRequest, Customer.class);
        this.customerService.update(updateCustomer);
        return ResultHelper.success(this.modelMapper.forResponse().map(updateCustomer, CustomerResponse.class));
    }

    /**
     * Belirli bir müşteriyi siler.
     *
     * @param id Müşterinin ID'si
     * @return Silme işleminin sonucunu içeren Response
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Result delete(@PathVariable("id") long id) {
        // ID'ye göre müşteriyi siler
        this.customerService.delete(id);
        return ResultHelper.Ok();
    }

    /**
     * Müşteri listesine sayfalama ile erişir.
     *
     * @param page     Sayfa numarası (varsayılan: 0)
     * @param pageSize Sayfa boyutu (varsayılan: 5)
     * @return Müşteri listesi ve sayfalama bilgilerini içeren Response
     */
    @GetMapping("/customerList")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<CursorResponse<CustomerResponse>> cursor(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize) {
        // Sayfalama ile müşteri listesini getirir
        Page<Customer> customerPage = this.customerService.cursor(page, pageSize);
        Page<CustomerResponse> customerResponsePage = customerPage
                .map(customer -> this.modelMapper.forResponse().map(customer, CustomerResponse.class));
        return ResultHelper.cursor(customerResponsePage);
    }

    /**
     * Belirli bir isme göre müşterileri getirir.
     *
     * @param name Müşteri ismi
     * @return İsme göre bulunan müşterilerin listesini içeren Response
     */
    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public ResultData<List<CustomerResponse>> getCustomersByName(@RequestParam("name") String name) {
        // İsmine göre müşterileri getirir
        List<Customer> customers = this.customerService.getCustomersByName(name);
        List<CustomerResponse> customerResponses = customers.stream()
                .map(customer -> this.modelMapper.forResponse().map(customer, CustomerResponse.class))
                .collect(Collectors.toList());
        return ResultHelper.success(customerResponses);
    }
}
