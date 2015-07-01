package confify.repositories;

import confify.models.Attend;
import confify.models.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Dennis on 4/20/2015.
 */
@Service
@Transactional
public interface AttendRepository extends JpaRepository<Attend, Long> {
    @Query("update Attend attend set status = :status where attend.conference.id = :conferId and attend.attendee.id = :userId")
    void updateStatus(@Param("status") int status, @Param("conferId") long conferId, @Param("userId") long userId);

    @Query("select attend from Attend attend where attend.conference.id = :conferId and attend.attendee.id = :userId and attend.status = :status")
    Attend getAttendByUserIdAndConferIdAndStatus(@Param("conferId") long conferId, @Param("userId") long userId, @Param("status") int status);

    @Query("select attend from Attend attend where attend.conference.id = :conferId and attend.attendee.id = :userId")
    Attend getAttendByUserIdAndConferId(@Param("conferId") long conferId, @Param("userId") long userId);

    @Query("select attend from Attend attend where attend.conference.id = :conferId and attend.attendee.email = :email")
    Attend getAttendByConferIdAndUserEmail(@Param("conferId") long conferId, @Param("email") String email);

    @Modifying
    @Query(value = "insert into attend(confer_id, user_id, status) values(:conferId, :userId, :status)", nativeQuery=true)
    void insert(@Param("conferId") long conferId, @Param("userId") long userId, @Param("status") int status);

    @Query("select attend from Attend attend where attend.conference.id = :conferId")
    List<Attend> getAttendsByConferId(@Param("conferId") long conferId);

    @Query("select attend from Attend attend where attend.attendee.id = :userId")
    List<Attend> getAttendsByUserId(@Param("userId") long userId);

    @Query("select attend from Attend attend where attend.conference.id = :conferId and attend.status = :status")
    List<Attend> getAttendByConferIdAndStatus(@Param("conferId") long conferId, @Param("status") int status);

    @Query("select attend from Attend attend where attend.attendee.email = :email")
    List<Attend> getAttendByEmail(@Param("email") String email);

    @Query("select attend from Attend attend where attend.attendee.email = :email and attend.status > 0")
    List<Attend> getNonrejectedAttendByEmail(@Param("email") String email);

    @Query("select attend.conference from Attend attend where attend.attendee.email = :email")
    List<Conference> getConferenceByEmail(@Param("email") String email);

    @Query("select attend.conference from Attend attend where attend.attendee.email = :email and attend.conference.id = :conferId")
    Conference getConferenceByConferIdAndAttendEmail(@Param("conferId") long conferId, @Param("email") String email);

}
