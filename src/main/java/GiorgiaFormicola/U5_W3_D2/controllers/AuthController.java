package GiorgiaFormicola.U5_W3_D2.controllers;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.PayloadValidationException;
import GiorgiaFormicola.U5_W3_D2.payloads.EmployeeDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.LoginDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.LoginResponseDTO;
import GiorgiaFormicola.U5_W3_D2.services.AuthService;
import GiorgiaFormicola.U5_W3_D2.services.EmployeesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private EmployeesService employeesService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Validated LoginDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return new LoginResponseDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee saveNewEmployee(@RequestBody @Validated EmployeeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.employeesService.save(body);
    }
}
