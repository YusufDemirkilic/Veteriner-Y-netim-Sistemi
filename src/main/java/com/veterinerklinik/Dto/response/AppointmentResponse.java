package com.veterinerklinik.Dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {


    private LocalDateTime appointmentDateTime;
    private long doctorId;
    private long animalId;
}
