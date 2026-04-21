package GiorgiaFormicola.U5_W3_D2.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(name = "request_date", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDate requestDate;

    @Column(columnDefinition = "text", nullable = false)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    protected Reservation() {
    }

    public Reservation(String notes, Employee employee, Trip trip) {
        this.requestDate = LocalDate.now();
        this.notes = notes;
        this.employee = employee;
        this.trip = trip;
    }
}
