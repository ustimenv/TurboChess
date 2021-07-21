package turbochess.service.admin_message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import turbochess.model.AdminMessage;
import turbochess.repository.AdminMessageRepository;

import java.util.List;

@Service
public class AdminMessageServiceImp implements AdminMessageService{

    @Autowired
    private AdminMessageRepository repository;

    @Override
    public AdminMessage save(AdminMessage message){
        return repository.save(message);
    }


    @Override
    public List<AdminMessage> getAll(){
        return repository.getAll();
    }

}
