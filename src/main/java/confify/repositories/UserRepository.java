package confify.repositories;

import confify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface UserRepository extends JpaRepository<User, Long>{

    @Query("select user from User user where user.id = :id and user.email = :email")
    User getUserByIdAndEmail(@Param("id") long id,@Param("email") String email);

    @Query("select user from User user where user.id = :id")
    User getUserById(@Param("id") long id);

    @Query("select user from User user LEFT JOIN FETCH user.attends where user.id = :id")
    User getUserWithAttendsById(@Param("id") long id);

    @Query("select user from User user where user.email = :email")
    User getUserByEmail(@Param("email") String email);
}
