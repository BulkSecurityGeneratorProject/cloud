package cloud.repository;

import cloud.domain.Institute;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Institute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {
    @Query("select institute from Institute institute where institute.user.login = ?#{principal.username}")
    Institute findOneByUserIsCurrentUser();

}
