package confify.repositories;

import confify.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Dennis on 4/23/2015.
 */
public interface AdminRepository extends JpaRepository<Admin, Long>{
    @Query("select admin from Admin admin where admin.email = :email")
    Admin getAdminByEmail(@Param("email") String email);

    @Query("select admin from Admin admin where admin.id = :id")
    Admin getAdminById(@Param("id") long id);
}
