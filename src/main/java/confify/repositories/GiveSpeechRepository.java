package confify.repositories;

import confify.models.Attend;
import confify.models.GiveSpeech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Dennis on 4/24/2015.
 */
public interface GiveSpeechRepository extends JpaRepository<GiveSpeech, Long> {
    @Query("select giveSpeech from GiveSpeech giveSpeech where giveSpeech.conference.id = :conferId")
    List<GiveSpeech> getGiveSpeechByConferId(@Param("conferId") long conferId);

    @Query("select giveSpeech from GiveSpeech giveSpeech where giveSpeech.conference.id = :conferId and giveSpeech.speaker.id = :userId")
    GiveSpeech getGiveSpeechByConferIdAndUserId(@Param("conferId") long conferId, @Param("userId") long userId);

    @Query("select giveSpeech from GiveSpeech giveSpeech where giveSpeech.conference.id = :conferId and giveSpeech.speaker.email = :email")
    GiveSpeech getGiveSpeechByConferIdAndEmail(@Param("conferId") long conferId, @Param("email") String email);
}
