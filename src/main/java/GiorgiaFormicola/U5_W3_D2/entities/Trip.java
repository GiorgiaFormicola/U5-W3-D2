package GiorgiaFormicola.U5_W3_D2.entities;

import GiorgiaFormicola.U5_W3_D2.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "trips", uniqueConstraints = {@UniqueConstraint(columnNames = {"destination", "date"})})
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TripStatus status;

    protected Trip() {
    }

    public Trip(String destination, LocalDate date) {
        this.destination = destination;
        this.date = date;
        this.status = TripStatus.SCHEDULED;
    }
}
