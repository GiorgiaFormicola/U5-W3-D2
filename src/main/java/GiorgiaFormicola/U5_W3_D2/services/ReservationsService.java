package GiorgiaFormicola.U5_W3_D2.services;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.entities.Reservation;
import GiorgiaFormicola.U5_W3_D2.entities.Trip;
import GiorgiaFormicola.U5_W3_D2.enums.TripStatus;
import GiorgiaFormicola.U5_W3_D2.exceptions.BadRequestException;
import GiorgiaFormicola.U5_W3_D2.exceptions.NotFoundException;
import GiorgiaFormicola.U5_W3_D2.payloads.MyReservationDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.ReservationDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.ReservationNotesDTO;
import GiorgiaFormicola.U5_W3_D2.repositories.ReservationsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationsService {
    private ReservationsRepository reservationsRepository;
    private EmployeesService employeesService;
    private TripsService tripsService;

    public Reservation save(ReservationDTO body) {
        Employee employeeFound = employeesService.findById(body.employeeId());
        Trip tripFound = tripsService.findById(body.tripId());
        if (tripFound.getStatus().equals(TripStatus.COMPLETED))
            throw new BadRequestException("Trip has already taken place on " + tripFound.getDate());
        if (reservationsRepository.existsByTrip(tripFound))
            throw new BadRequestException("Trip for " + tripFound.getDestination() + " on " + tripFound.getDate() + " already reserved by another employee");
        if (reservationsRepository.existsByEmployeeAndTrip_Date(employeeFound, tripFound.getDate()))
            throw new BadRequestException("Employee has already a trip reserved on " + tripFound.getDate());
        Reservation newReservation = new Reservation(body.notes(), employeeFound, tripFound);
        Reservation savedReservation = this.reservationsRepository.save(newReservation);
        log.info("Reservation with id " + savedReservation.getId() + " successfully saved!");
        return savedReservation;
    }

    public Reservation saveByEmployee(Employee currentAutheticatedEmployee, MyReservationDTO body) {
        Trip tripFound = tripsService.findById(body.tripId());
        if (tripFound.getStatus().equals(TripStatus.COMPLETED))
            throw new BadRequestException("Trip has already taken place on " + tripFound.getDate());
        if (reservationsRepository.existsByTrip(tripFound))
            throw new BadRequestException("Trip for " + tripFound.getDestination() + " on " + tripFound.getDate() + " already reserved by another employee");
        if (reservationsRepository.existsByEmployeeAndTrip_Date(currentAutheticatedEmployee, tripFound.getDate()))
            throw new BadRequestException("Employee has already a trip reserved on " + tripFound.getDate());
        Reservation newReservation = new Reservation(body.notes(), currentAutheticatedEmployee, tripFound);
        Reservation savedReservation = this.reservationsRepository.save(newReservation);
        log.info("Reservation with id " + savedReservation.getId() + " successfully saved!");
        return savedReservation;
    }

    public Page<Reservation> findAll(int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 5;
        Pageable pageable;
        if (sortBy.equals("trip.date") || sortBy.equals("requestDate"))
            pageable = PageRequest.of(page, size, Sort.by(sortBy).reverse());
        else pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.reservationsRepository.findAll(pageable);
    }

    public Page<Reservation> findAllByEmployee(Employee currentAutheticatedEmployee, int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 5;
        Pageable pageable;
        if (sortBy.equals("trip.date") || sortBy.equals("requestDate"))
            pageable = PageRequest.of(page, size, Sort.by(sortBy).reverse());
        else pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.reservationsRepository.findAllByEmployee(currentAutheticatedEmployee, pageable);
    }

    public Reservation findById(UUID reservationId) {
        return this.reservationsRepository.findById(reservationId).orElseThrow(() -> new NotFoundException("reservation", reservationId));
    }

    public Reservation findMyReservationById(Employee currentAuthenticatedEmployee, UUID reservationId) {
        Reservation found = this.findById(reservationId);
        if (!found.getEmployee().getId().equals(currentAuthenticatedEmployee.getId()))
            throw new AuthorizationDeniedException("");
        return found;
    }

    /*public Reservation findByIdAndUpdate(UUID reservationId, ReservationDTO body) {
        Reservation found = this.findById(reservationId);
        Trip tripFound = tripsService.findById(body.tripId());
        Employee employeeFound = employeesService.findById(body.employeeId());
        if (!found.getTrip().getId().equals(body.tripId())) {
            if (tripFound.getStatus().equals(TripStatus.COMPLETED))
                throw new BadRequestException("Trip has already taken place on " + tripFound.getDate());
            if (reservationsRepository.existsByTrip(tripFound))
                throw new BadRequestException("Trip for " + tripFound.getDestination() + " on " + tripFound.getDate() + " already reserved by another employee");
            if (reservationsRepository.existsByEmployeeAndTrip_Date(found.getEmployee(), tripFound.getDate()))
                throw new BadRequestException("Employee has already a trip reserved on " + tripFound.getDate());
        }
        if (!found.getEmployee().getId().equals(body.employeeId())) {
            if (reservationsRepository.existsByEmployeeAndTrip_Date(employeeFound, found.getTrip().getDate()))
                throw new BadRequestException("Employee has already a trip reserved on " + found.getTrip().getDate());
        }
        found.setEmployee(employeeFound);
        found.setTrip(tripFound);
        found.setNotes(body.notes());
        Reservation updatedReservation = this.reservationsRepository.save(found);
        log.info("Reservation with id " + updatedReservation.getId() + " successfully modified");
        return updatedReservation;
    }*/

    public Reservation findByIdAndUpdate(UUID reservationId, ReservationNotesDTO body) {
        Reservation found = this.findById(reservationId);
        if (found.getTrip().getStatus().equals(TripStatus.COMPLETED))
            throw new BadRequestException("Trip has already taken place on " + found.getTrip().getDate());
        found.setNotes(body.notes());
        Reservation updatedReservation = this.reservationsRepository.save(found);
        log.info("Reservation with id " + updatedReservation.getId() + " successfully modified");
        return updatedReservation;
    }

    public Reservation findMyReservationByIdAndUpdate(Employee currentAuthenticatedEmployee, UUID reservationId, ReservationNotesDTO body) {
        Reservation found = this.findMyReservationById(currentAuthenticatedEmployee, reservationId);
        if (found.getTrip().getStatus().equals(TripStatus.COMPLETED))
            throw new BadRequestException("Trip has already taken place on " + found.getTrip().getDate());
        found.setNotes(body.notes());
        Reservation updatedReservation = this.reservationsRepository.save(found);
        log.info("Reservation with id " + updatedReservation.getId() + " successfully modified");
        return updatedReservation;
    }

    public void findByIdAndDelete(UUID reservationId) {
        Reservation found = this.findById(reservationId);
        this.reservationsRepository.delete(found);
    }

    public void findMyReservationByIdAndDelete(Employee currentAuthenticatedEmployee, UUID reservationId) {
        Reservation found = this.findMyReservationById(currentAuthenticatedEmployee, reservationId);
        this.reservationsRepository.delete(found);
    }
}
