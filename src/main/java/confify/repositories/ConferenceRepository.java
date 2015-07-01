package confify.repositories;

import confify.models.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Dennis on 4/23/2015.
 */
public interface ConferenceRepository extends JpaRepository<Conference, Long>{
    @Query("select conference from Conference conference where conference.id = :id")
    Conference getConferenceById(@Param("id") long id);
}
