package com.veterinerklinik.Dto.request.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSaveRequest {


    @NotNull(message = "İsim alanı boş olamaz")
    @NotEmpty(message = "İsim alanı boş olamaz")
    private String name;

    @NotNull(message = "Telefon alanı boş olamaz")
    private String phone;

    @NotNull(message = "E-posta alanı boş olamaz")
    @Email(message = "Geçersiz e-posta adresi formatı")
    private String mail;

    private String address;
    private String city;
}
