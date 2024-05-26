package com.veterinerklinik.Service;


import com.veterinerklinik.entity.Animal;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AnimalService {
    Animal save(Animal animal);
    Animal get(Long id);
    Page<Animal> cursor(int page, int pageSize);
    Animal update (Animal animal);
    boolean delete (long id);
    List<Animal> getAnimalsByName(String name);
    List<Animal> getAnimalsByCustomerId(Long customerId);
    Optional<Animal> getAnimalByName(String name);
}
