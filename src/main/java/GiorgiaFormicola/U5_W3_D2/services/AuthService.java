package GiorgiaFormicola.U5_W3_D2.services;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.NotFoundException;
import GiorgiaFormicola.U5_W3_D2.exceptions.UnauthorizedException;
import GiorgiaFormicola.U5_W3_D2.payloads.LoginDTO;
import GiorgiaFormicola.U5_W3_D2.security.TokenTools;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final EmployeesService employeesService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bCryptEncoder;

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        try {
            Employee found = this.employeesService.findByEmail(body.email());
            if (!bCryptEncoder.matches(body.password(), found.getPassword()))
                throw new UnauthorizedException("Wrong credentials supplied");
            return this.tokenTools.generateToken(found);
        } catch (NotFoundException ex) {
            throw new UnauthorizedException("Wrong credentials supplied");
        }
    }
}
