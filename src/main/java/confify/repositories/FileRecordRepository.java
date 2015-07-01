package confify.repositories;

import confify.models.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Dennis on 5/2/2015.
 */
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {

}
