package GiorgiaFormicola.U5_W3_D2.repositories;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.entities.Reservation;
import GiorgiaFormicola.U5_W3_D2.entities.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, UUID> {
    boolean existsByTrip(Trip trip);

    boolean existsByEmployeeAndTrip_Date(Employee employee, LocalDate tripDate);

    Page<Reservation> findAllByEmployee(Employee employee, Pageable pageable);

}
