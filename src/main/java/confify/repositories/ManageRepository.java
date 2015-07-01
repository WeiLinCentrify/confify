package confify.repositories;

import confify.models.Conference;
import confify.models.Manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Dennis on 4/24/2015.
 */
public interface ManageRepository extends JpaRepository<Manage, Long> {
    /*@Query("select manage from Manage manage JOIN FETCH manage.conference JOIN FETCH manage.admin where manage.conference.id = :conferId and manage.admin.id = :adminId")
    Manage getManageByIdAndAdminId(@Param("conferId") long conferId, @Param("adminId") long adminId);*/

    @Query("select manage from Manage manage where manage.conference.id = :conferId and manage.admin.id = :adminId")
    Manage getManageByIdAndAdminId(@Param("conferId") long conferId, @Param("adminId") long adminId);

    @Query("select manage from Manage manage where manage.admin.id = :adminId")
    List<Manage> getManageByAdminId(@Param("adminId") long adminId);

    @Query("select manage.conference from Manage manage where manage.admin.email = :email")
    List<Conference> getConferenceByEmail(@Param("email") String email);

    @Query("select manage.conference from Manage manage where manage.admin.email = :email and manage.conference.id = :conferId")
    Conference getConferenceByConferIdAndAdminEmail(@Param("conferId") long conferId, @Param("email") String email);

    @Query("select manage from Manage manage where manage.admin.email = :email and manage.conference.id = :conferId")
    Manage getManageByConferIdAndAdminEmail(@Param("conferId") long conferId, @Param("email") String email);

    @Modifying
    @Query("delete from Manage manage where manage.conference.id = :conferId")
    void deleteManageByConferenceId(@Param("conferId") long conferId);
}
