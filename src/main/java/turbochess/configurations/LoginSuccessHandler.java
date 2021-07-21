package turbochess.configurations;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import turbochess.model.User;
import turbochess.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Called when a user is first authenticated (via login).
 * Called from SecurityConfig; see https://stackoverflow.com/a/53353324
 * 
 * Adds a "u" variable to the session when a user is first authenticated.
 * Important: the user is retrieved from the database, but is not refreshed at each request. 
 * You should refresh the user's information if anything important changes; for example, after
 * updating the user's profile.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired 
    private HttpSession session;

    @Autowired
	private UserService userService;

	private static Logger log = LogManager.getLogger(LoginSuccessHandler.class);
	
    /**
     * Called whenever a user authenticates correctly.
     */
    @SneakyThrows
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){

	    String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
		// add a 'u' session variable, accessible from thymeleaf via ${session.u}
	    User u = userService.getUserByUsername(username);
		session.setAttribute("u", u);

		// add a 'ws' session variable
		String ws = request.getRequestURL().toString()
				.replaceFirst("[^:]*", "ws")		// http[s]://... => ws://...
				.replaceFirst("/[^/]*$", "/ws");	// .../foo		 => .../ws
		session.setAttribute("ws", ws);
		
		// redirects to 'admin' or 'user/{id}', depending on the user
		//String nextUrl = u.hasRole(User.Role.ADMIN) ?
		//	"admin/" :
		//	"user/" + u.getId();
		String nextUrl ="user/" + u.getId();
		log.info("LOG IN: {} (id {}) -- session is {}, websocket is {} -- redirected to {}",
			u.getUsername(), u.getId(), session.getId(), ws, nextUrl);

		// note that this is a 302, and will result in a new request
		response.sendRedirect(nextUrl);
	}
}
