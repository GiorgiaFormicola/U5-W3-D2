package GiorgiaFormicola.U5_W3_D2.payloads;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TripDateDTO(
        @NotNull(message = "Date is mandatory")
        @Future(message = "Date must be in the future")
        LocalDate date
) {

}
