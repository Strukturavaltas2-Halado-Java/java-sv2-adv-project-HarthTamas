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

    @Query("select p from Port p where (:name is null or p.portName =:name) and (:value is null or p.numberOfGuestBerths >= :value)")
    List<Port> findAllByOptionalOfNameAndNumberOfBerths(@Param("name") Optional<String> name, @Param("value") Optional<Integer> value);

}
