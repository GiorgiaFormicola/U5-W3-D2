package GiorgiaFormicola.U5_W3_D2.payloads;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TripDTO(
        @NotBlank(message = "Destination is mandatory and it can't contain only blank spaces")
        @Size(min = 2, max = 255, message = "Destination must contain between 2 and 30 characters")
        String destination,
        @NotNull(message = "Date is mandatory")
        @Future(message = "Date must be in the future")
        LocalDate date
) {
}
