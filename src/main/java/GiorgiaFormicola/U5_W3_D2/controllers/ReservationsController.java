package GiorgiaFormicola.U5_W3_D2.controllers;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.entities.Reservation;
import GiorgiaFormicola.U5_W3_D2.exceptions.PayloadValidationException;
import GiorgiaFormicola.U5_W3_D2.payloads.MyReservationDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.ReservationDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.ReservationNotesDTO;
import GiorgiaFormicola.U5_W3_D2.services.ReservationsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("reservations")
@AllArgsConstructor
public class ReservationsController {
    private ReservationsService reservationsService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation saveNewReservation(@RequestBody @Validated ReservationDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.reservationsService.save(body);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Reservation> getReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "trip.date") String sortBy) {
        return this.reservationsService.findAll(page, size, sortBy);
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Reservation getReservationById(@PathVariable UUID reservationId) {
        return this.reservationsService.findById(reservationId);
    }

    @PutMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Reservation getReservationByIdAndUpdate(@PathVariable UUID reservationId, @RequestBody @Validated ReservationNotesDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errorsList);
        }
        return this.reservationsService.findByIdAndUpdate(reservationId, body);
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getReservationByIdAndDelete(@PathVariable UUID reservationId) {
        this.reservationsService.findByIdAndDelete(reservationId);
    }

    //ADDS "/me" ENDPOINTS
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation saveMyReservation(@AuthenticationPrincipal Employee currentAuthenticatedEmployee, @RequestBody @Validated MyReservationDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getAllErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new PayloadValidationException(errors);
        }
        return this.reservationsService.saveByEmployee(currentAuthenticatedEmployee, body);
    }

    @GetMapping("/me")
    public Page<Reservation> getMyReservations(
            @AuthenticationPrincipal Employee currentAuthenticatedEmployee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "trip.date") String sortBy) {
        return this.reservationsService.findAllByEmployee(currentAuthenticatedEmployee, page, size, sortBy);
    }

    @GetMapping("/me/{reservationId}")
    public Reservation getMyReservationById(@AuthenticationPrincipal Employee currentAuthenticatedEmployee, @PathVariable UUID reservationId) {
        return this.reservationsService.findMyReservationById(currentAuthenticatedEmployee, reservationId);
    }


}
