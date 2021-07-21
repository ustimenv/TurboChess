package turbochess.service.admin_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.AdminMessage;

import java.util.List;

@Service
public class AdminMessageService{
    @Autowired
    private AdminMessageRepository repository;

    public AdminMessage save(AdminMessage message){
        return repository.save(message);
    }

    public void delete(AdminMessage message){
        repository.delete(message);
    }

    public List<AdminMessage> getAll(){
        return repository.getAll();
    }


}
