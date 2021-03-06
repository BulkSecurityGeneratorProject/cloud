package cloud.repository;

import cloud.domain.Upazila;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Upazila entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UpazilaRepository extends JpaRepository<Upazila, Long> {

}
