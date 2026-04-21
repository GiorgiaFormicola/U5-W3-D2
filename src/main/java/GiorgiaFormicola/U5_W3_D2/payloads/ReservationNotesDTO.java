package GiorgiaFormicola.U5_W3_D2.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReservationNotesDTO(
        @NotNull(message = "Additional notes are mandatory")
        @Size(max = 500, message = "Additional notes must contain maximum 500 characters")
        @Pattern(regexp = "^$|.*\\S.*", message = "Additional notes can't contain only blank spaces")
        String notes
) {
}
