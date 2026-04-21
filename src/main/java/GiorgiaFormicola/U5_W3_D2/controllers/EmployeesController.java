package GiorgiaFormicola.U5_W3_D2.controllers;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.PayloadValidationException;
import GiorgiaFormicola.U5_W3_D2.payloads.EmployeeDTO;
import GiorgiaFormicola.U5_W3_D2.services.EmployeesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("employees")
@AllArgsConstructor
public class EmployeesController {
    private EmployeesService employeesService;

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee saveNewEmployee(@RequestBody @Validated EmployeeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.employeesService.save(body);
    }*/

    @GetMapping
    public Page<Employee> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sortBy) {
        return this.employeesService.findAll(page, size, sortBy);
    }

    @GetMapping("/{employeeId}")
    public Employee getEmployeeById(@PathVariable UUID employeeId) {
        return this.employeesService.findById(employeeId);
    }

    @PutMapping("/{employeeId}")
    public Employee getEmployeeByIdAndUpdate(@PathVariable UUID employeeId, @RequestBody @Validated EmployeeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errorsList);
        }
        return this.employeesService.findByIdAndUpdate(employeeId, body);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getEmployeeByIdAndDelete(@PathVariable UUID employeeId) {
        this.employeesService.findByIdAndDelete(employeeId);
    }

    @PatchMapping("/{employeeId}/picture")
    public Employee getEmployeeByIdAndUploadProfilePicture(@PathVariable UUID employeeId, @RequestParam("profile_picture") MultipartFile file) {
        return this.employeesService.findByIdAndUploadProfilePicture(employeeId, file);
    }


}
