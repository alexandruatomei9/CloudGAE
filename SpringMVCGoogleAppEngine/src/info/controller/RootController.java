package info.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import info.services.GoogleCalendar;

@Controller
@RequestMapping("/")
public class RootController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user == null) {
			return new ModelAndView("redirect:"
					+ userService.createLoginURL("/"));
		} else {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("index");
			mav.addObject("userName", user.getNickname());
			mav.addObject("logoutUrl", userService.createLogoutURL("/"));
			return mav;
		}
	}
	
	@RequestMapping(value = "calendar", method = RequestMethod.GET)
	public ModelAndView getCalendar() {
		GoogleCalendar calendar = new GoogleCalendar();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("calendar");
		try {
			mav.addObject("events", calendar.getEvents(10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
}
