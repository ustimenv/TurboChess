package turbochess.configurations;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import turbochess.model.User;
import turbochess.service.user.UserException;
import turbochess.service.user.UserService;

public class IwUserDetailsService implements UserDetailsService {

	private static Logger log = LogManager.getLogger(IwUserDetailsService.class);

	@Autowired
	private UserService userService;

    public UserDetails loadUserByUsername(String username){
    	try {
			User u = userService.getUserByUsername(username);

	        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
	        for (String r : u.getRoles().split("[,]")) {
	        	roles.add(new SimpleGrantedAuthority("ROLE_" + r));
		        log.info("Roles for " + username + " include " + roles.get(roles.size()-1));
	        }
	        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), roles);
	    } catch (UserException e) {
    		log.info("No such user: " + username + "(e = " + e.getMessage() + ")");
    		throw new UsernameNotFoundException(username);
    	}
    }
}