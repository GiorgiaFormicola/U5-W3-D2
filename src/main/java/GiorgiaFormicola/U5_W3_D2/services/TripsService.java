package GiorgiaFormicola.U5_W3_D2.services;

import GiorgiaFormicola.U5_W3_D2.entities.Trip;
import GiorgiaFormicola.U5_W3_D2.enums.TripStatus;
import GiorgiaFormicola.U5_W3_D2.exceptions.BadRequestException;
import GiorgiaFormicola.U5_W3_D2.exceptions.NotFoundException;
import GiorgiaFormicola.U5_W3_D2.payloads.TripDTO;
import GiorgiaFormicola.U5_W3_D2.payloads.TripDateDTO;
import GiorgiaFormicola.U5_W3_D2.repositories.TripsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class TripsService {
    private TripsRepository tripsRepository;

    public Trip save(TripDTO body) {
        if (tripsRepository.existsByDestinationAndDate(body.destination(), body.date()))
            throw new BadRequestException("Trip for " + body.destination() + " on date " + body.date() + " already planned!");
        Trip newTrip = new Trip(body.destination(), body.date());
        Trip savedTrip = this.tripsRepository.save(newTrip);
        log.info("Trip with id " + savedTrip.getId() + " successfully saved!");
        return savedTrip;
    }

    public Page<Trip> findAll(int page, int size, String sortBy) {
        if (page < 0) page = 0;
        if (size < 0 || size > 100) size = 5;
        Pageable pageable;
        if (sortBy.equals("date")) pageable = PageRequest.of(page, size, Sort.by(sortBy).reverse());
        else pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.tripsRepository.findAll(pageable);
    }

    public Trip findById(UUID tripId) {
        return this.tripsRepository.findById(tripId).orElseThrow(() -> new NotFoundException("trip", tripId));
    }

    public Trip findByIdAndUpdate(UUID tripId, TripDateDTO body) {
        Trip found = this.findById(tripId);
        if (found.getStatus().equals(TripStatus.COMPLETED)) throw new BadRequestException("Trip already completed");
        if (!found.getDate().equals(body.date())) {
            if (tripsRepository.existsByDestinationAndDate(found.getDestination(), body.date()))
                throw new BadRequestException("Trip for " + found.getDestination() + " on date " + body.date() + " already planned!");
        }
        found.setDate(body.date());
        Trip updatedTrip = this.tripsRepository.save(found);
        log.info("Trip with id " + updatedTrip.getId() + " successfully modified");
        return updatedTrip;
    }

    public void findByIdAndDelete(UUID tripId) {
        Trip found = this.findById(tripId);
        //TODO:delete reservations linked to trip
        this.tripsRepository.delete(found);
    }

    public Trip findByIdAndUpdateStatus(UUID tripId) {
        Trip found = this.findById(tripId);
        if (found.getStatus().equals(TripStatus.COMPLETED)) throw new BadRequestException("Trip already completed");
        if (found.getDate().isAfter(LocalDate.now()))
            throw new BadRequestException("Trip scheduled on " + found.getDate());
        found.setStatus(TripStatus.COMPLETED);
        Trip updatedTrip = this.tripsRepository.save(found);
        return updatedTrip;
    }
}
