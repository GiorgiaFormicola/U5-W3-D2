package GiorgiaFormicola.U5_W3_D2.services;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.BadRequestException;
import GiorgiaFormicola.U5_W3_D2.exceptions.NotFoundException;
import GiorgiaFormicola.U5_W3_D2.exceptions.ValidationException;
import GiorgiaFormicola.U5_W3_D2.payloads.EmployeeDTO;
import GiorgiaFormicola.U5_W3_D2.repositories.EmployeesRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class EmployeesService {
    private final EmployeesRepository employeesRepository;
    private final Cloudinary cloudinary;
    private final PasswordEncoder bCryptEncoder;

    public Employee save(EmployeeDTO body) {
        if (employeesRepository.existsByEmail(body.email()))
            throw new BadRequestException("Email " + body.email() + " already in use!");
        if (employeesRepository.existsByUsername(body.username()))
            throw new BadRequestException("Username " + body.username() + " already in use!");
        Employee newEmployee = new Employee(body.username(), body.name(), body.surname(), body.email(), this.bCryptEncoder.encode(body.password()));
        Employee savedEmployee = this.employeesRepository.save(newEmployee);
        log.info("Employee with id " + savedEmployee.getId() + " successfully saved!");
        return savedEmployee;
    }

    public Page<Employee> findAll(int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.employeesRepository.findAll(pageable);
    }


    public Employee findById(UUID employeeId) {
        return this.employeesRepository.findById(employeeId).orElseThrow(() -> new NotFoundException("employee", employeeId));
    }

    public Employee findByIdAndUpdate(UUID employeeId, EmployeeDTO body) {
        Employee found = this.findById(employeeId);
        if (!found.getEmail().equals(body.email())) {
            if (this.employeesRepository.existsByEmail(body.email()))
                throw new BadRequestException("Email " + body.email() + " already in use!");
        }
        if (!found.getUsername().equals(body.username())) {
            if (employeesRepository.existsByUsername(body.username()))
                throw new BadRequestException("Username " + body.username() + " already in use!");
        }
        found.setUsername(body.username());
        found.setName(body.name());
        found.setSurname(body.surname());
        found.setEmail(body.email());
        found.setProfilePictureURL("https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());
        Employee updatedEmployee = this.employeesRepository.save(found);
        log.info("Employee with id " + updatedEmployee.getId() + " successfully modified");
        return updatedEmployee;
    }


    public void findByIdAndDelete(UUID employeeId) {
        Employee found = this.findById(employeeId);
        //TODO:delete reservations linked to employee
        this.employeesRepository.delete(found);
    }

    public Employee findByIdAndUploadProfilePicture(UUID employeeId, MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/") || file.isEmpty())
            throw new ValidationException("Invalid type of file provided");
        if (file.getSize() > 2 * 1024 * 1024)
            throw new ValidationException("File size must be smaller than 2 MB");
        Employee found = findById(employeeId);
        try {
            Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            found.setProfilePictureURL((String) result.get("secure_url"));
            Employee updatedEmployee = this.employeesRepository.save(found);
            return updatedEmployee;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Employee findByEmail(String email) {
        return this.employeesRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(email));
    }
}
