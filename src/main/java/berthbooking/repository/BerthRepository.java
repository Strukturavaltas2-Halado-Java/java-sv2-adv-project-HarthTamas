package berthbooking.repository;

import berthbooking.model.Berth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BerthRepository extends JpaRepository<Berth, Long> {

    @Query("select b from Berth b where (:width is null or b.width >= :width) and (:portName is null or b.port.portName = :portName) order by b.code ASC")
    List<Berth> findAllBerthsByPortNameAndWidth(Optional<Integer> width, Optional<String> portName);
}
