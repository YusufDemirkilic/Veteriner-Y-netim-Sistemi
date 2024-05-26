package com.veterinerklinik.Service.Impl;

import com.veterinerklinik.Service.CustomerService;
import com.veterinerklinik.config.exeption.NotFoundException;
import com.veterinerklinik.config.utiles.Msg;
import com.veterinerklinik.Repository.CustomerRepository;
import com.veterinerklinik.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Müşteri servis uygulama sınıfı
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    // CustomerRepository bağımlılığı
    private final CustomerRepository customerRepo;

    // Müşteri kaydetme işlemi
    @Override
    public Customer save(Customer customer) {
        return this.customerRepo.save(customer);
    }

    // Belirli bir ID'ye göre müşteri getirme işlemi
    @Override
    public Customer get(long id) {
        return customerRepo.findById(id).orElseThrow(() -> new NotFoundException(Msg.NOT_FOUND));
    }

    // Sayfalı müşteri listesi alma işlemi
    @Override
    public Page<Customer> cursor(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return this.customerRepo.findAll(pageable);
    }

    // Müşteri güncelleme işlemi
    @Override
    public Customer update(Customer customer) {
        this.get(customer.getId());
        return this.customerRepo.save(customer);
    }

    // Müşteri silme işlemi
    @Override
    public boolean delete(long id) {
        Customer customer = this.get(id);
        this.customerRepo.delete(customer);
        return true;
    }

    // Belirli bir e-posta adresine sahip müşterinin varlığını kontrol etme işlemi
    @Override
    public boolean existsByMail(String email) {
        return customerRepo.existsByMail(email);
    }

    // İsme göre müşterileri getirme işlemi
    @Override
    public List<Customer> getCustomersByName(String name) {
        return customerRepo.findByNameContainingIgnoreCase(name); // İsim içeren müşterileri getirir
    }

    // İsme göre müşteri getirme işlemi (opsiyonel)
    @Override
    public Optional<Customer> getCustomerByName(String name) {
        return customerRepo.findByName(name);
    }
}
