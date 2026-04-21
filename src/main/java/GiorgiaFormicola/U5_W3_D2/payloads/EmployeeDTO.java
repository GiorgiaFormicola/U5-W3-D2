package GiorgiaFormicola.U5_W3_D2.payloads;

import jakarta.validation.constraints.*;

public record EmployeeDTO(
        @NotNull(message = "Username is mandatory")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username must contain only letters, numbers and '.', '_', '-' characters")
        @Size(min = 3, max = 30, message = "Username must contain between 3 and 30 characters")
        String username,
        @NotBlank(message = "Name is mandatory and it can't contain only blank spaces")
        @Size(min = 2, message = "Name must contain minimum 2 characters")
        String name,
        @NotBlank(message = "Surname is mandatory and it can't contain only blank spaces")
        @Size(min = 2, message = "Surname must contain minimum 2 characters")
        String surname,
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email must follow a valid email format")
        String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must follow a valid password format")
        String password

) {
}
