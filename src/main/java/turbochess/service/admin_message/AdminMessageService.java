package turbochess.service.admin_message;

import org.springframework.stereotype.Service;
import turbochess.model.AdminMessage;

import java.util.List;

@Service
public interface AdminMessageService{
    List<AdminMessage> getAll();
    AdminMessage save(AdminMessage message);
}
