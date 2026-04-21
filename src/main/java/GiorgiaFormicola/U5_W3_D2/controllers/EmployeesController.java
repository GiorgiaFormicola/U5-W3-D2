package GiorgiaFormicola.U5_W3_D2.controllers;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.PayloadValidationException;
import GiorgiaFormicola.U5_W3_D2.payloads.EmployeeDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.RoleDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.SignInDTO;
import GiorgiaFormicola.U5_W3_D2.services.EmployeesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public Employee saveNewEmployee(@RequestBody @Validated SignInDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.employeesService.save(body);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public Page<Employee> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sortBy) {
        return this.employeesService.findAll(page, size, sortBy);
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public Employee getEmployeeById(@PathVariable UUID employeeId) {
        return this.employeesService.findById(employeeId);
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public Employee getEmployeeByIdAndUpdate(@PathVariable UUID employeeId, @RequestBody @Validated EmployeeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errorsList);
        }
        return this.employeesService.findByIdAndUpdate(employeeId, body);
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getEmployeeByIdAndDelete(@PathVariable UUID employeeId) {
        this.employeesService.findByIdAndDelete(employeeId);
    }

    @PatchMapping("/{employeeId}/picture")
    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    public Employee getEmployeeByIdAndUploadProfilePicture(@PathVariable UUID employeeId, @RequestParam("profile_picture") MultipartFile file) {
        return this.employeesService.findByIdAndUploadProfilePicture(employeeId, file);
    }

    @PatchMapping("/{employeeId}/role")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public Employee getEmployeeByIdAndUpdateRole(@PathVariable UUID employeeId, @RequestBody @Validated RoleDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errorsList);
        }
        return this.employeesService.findByIdAndUpdateRole(employeeId, body);
    }

    //ADDS "/me" ENDPOINTS
    @GetMapping("/me")
    public Employee getMyProfile(@AuthenticationPrincipal Employee currentAuthenticatedEmployee) {
        return currentAuthenticatedEmployee;
    }

    @PutMapping("/me")
    public Employee updateMyProfile(@AuthenticationPrincipal Employee currentAuthenticatedEmployee, @RequestBody @Validated EmployeeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errorsList);
        }
        return this.employeesService.findByIdAndUpdate(currentAuthenticatedEmployee.getId(), body);
    }

    @PatchMapping("/me/picture")
    public Employee updateMyProfilePicture(@AuthenticationPrincipal Employee currentAuthenticatedEmployee, @RequestParam("profile_picture") MultipartFile file) {
        return this.employeesService.findByIdAndUploadProfilePicture(currentAuthenticatedEmployee.getId(), file);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyProfile(@AuthenticationPrincipal Employee currentAuthenticatedEmployee) {
        this.employeesService.findByIdAndDelete(currentAuthenticatedEmployee.getId());
    }

}
