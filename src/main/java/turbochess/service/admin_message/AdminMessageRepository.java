package turbochess.service.admin_message;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import turbochess.model.AdminMessage;

import java.util.List;

public interface AdminMessageRepository extends CrudRepository<AdminMessage, Long>{

    @Query("SELECT m FROM AdminMessage m")
    List<AdminMessage> getAll();
}
