package GiorgiaFormicola.U5_W3_D2.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReservationDTO(
        @NotNull(message = "Additional notes are mandatory")
        @Size(max = 500, message = "Additional notes must contain maximum 500 characters")
        @Pattern(regexp = "^$|.*\\S.*", message = "Additional notes can't contain only blank spaces")
        String notes,
        @NotNull(message = "Employee id is mandatory")
        UUID employeeId,
        @NotNull(message = "Trip id is mandatory")
        UUID tripId
) {
}
