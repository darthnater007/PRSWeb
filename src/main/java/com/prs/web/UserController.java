package com.prs.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prs.business.user.User;
import com.prs.business.user.UserRepository;
import com.prs.util.PRSMaintenanceReturn;

@CrossOrigin
@Controller    
@RequestMapping(path="/Users")
public class UserController extends BaseController{
	@Autowired 
	private UserRepository userRepository;

	@GetMapping(path="/List")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
	
	@GetMapping(path="/Get")
	public @ResponseBody List<User> getUser(@RequestParam int id) {
		// This returns a JSON or XML with one specific user
		Optional<User> u = userRepository.findById(id);
		return getReturnArray(u);
	}
	
	@PostMapping(path="/Add") 
	public @ResponseBody PRSMaintenanceReturn addNewUser (@RequestBody User user) {
		try {
			userRepository.save(user);
			return PRSMaintenanceReturn.getMaintReturn(user);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(user, dive.getRootCause().toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return PRSMaintenanceReturn.getMaintReturnError(user, e.getMessage());
		}
	}
	
	@GetMapping(path="/Remove") // Map ONLY GET Requests
	public @ResponseBody PRSMaintenanceReturn deleteUser (@RequestParam int id) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		
		Optional<User> user = userRepository.findById(id);
		try {
			userRepository.delete(user.get());
			return PRSMaintenanceReturn.getMaintReturn(user.get());
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(user, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(user, e.toString());
		}
		
	}

	@PostMapping(path="/Change") 
	public @ResponseBody PRSMaintenanceReturn updateUser (@RequestBody User user) {
		try {
			userRepository.save(user);
			return PRSMaintenanceReturn.getMaintReturn(user);
		}
		catch (DataIntegrityViolationException dive) {
			return PRSMaintenanceReturn.getMaintReturnError(user, dive.getRootCause().toString());
		}
		catch (Exception e) {
			return PRSMaintenanceReturn.getMaintReturnError(user, e.toString());
		}
		
	}
	
	@GetMapping(path = "/Authenticate")
	public @ResponseBody List<User> authenticate(@RequestParam String uname, @RequestParam String pwd) {
		User user = new User();
		user = userRepository.findByUserNameAndPassword(uname, pwd);
		return BaseController.getReturnArray(user);
	}
	
}