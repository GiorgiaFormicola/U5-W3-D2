package GiorgiaFormicola.U5_W3_D2.payloads;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorsListDTO(
        String message,
        LocalDateTime timestamp,
        List<String> errors
) {
}
