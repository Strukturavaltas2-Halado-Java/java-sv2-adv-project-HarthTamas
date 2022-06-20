package berthbooking.repository;

import berthbooking.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {

    @Query("select p from Port p where (:town is null or p.town =:town) and (:capacity is null or p.numberOfGuestBerths >= :capacity) order by p.town ASC")
    List<Port> findAllByOptionalOfTownAndNumberOfBerths(@Param("town") Optional<String> town, @Param("capacity") Optional<Integer> capacity);

}
