package GiorgiaFormicola.U5_W3_D2.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class PayloadValidationException extends RuntimeException {
    private List<String> errors;

    public PayloadValidationException(List<String> errors) {
        super("Some errors occurred in the validation process");
        this.errors = errors;
    }
}
