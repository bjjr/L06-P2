/* ProfileCustomerController.java
 *
 * Copyright (C) 2016 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 * 
 */

package controllers.customer;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CustomerService;
import services.ProfileService;
import controllers.AbstractController;
import domain.Profile;

@Controller
@RequestMapping("/profile/customer")
public class ProfileCustomerController extends AbstractController {
	
	// Services ----------------------------------------------
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private CustomerService customerService;
	
	// Constructors ------------------------------------------
	
	public ProfileCustomerController() {
		super();
	}
	
	// Listing -----------------------------------------------
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView res;
		Collection<Profile> profiles;
		Collection<Profile> likes;
		
		profiles = profileService.findAll();
		likes = customerService.findByPrincipal().getLikes();
		
		res = new ModelAndView("profile/list");
		res.addObject("profiles", profiles);
		res.addObject("likes", likes);
		
		return res;
	}
	
	// Editing -----------------------------------------------
	
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView res;
		Profile profile;
		
		profile = profileService.findByPrincipal();
		Assert.notNull(profile);
		res = createEditModelAndView(profile);
		
		return res;
	}
	
	// Saving ------------------------------------------------
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid Profile profile, BindingResult binding) {
		ModelAndView res;
		
		if(binding.hasErrors()) {
			res = createEditModelAndView(profile);
		} else {
			try {
				profileService.save(profile); 
				res = new ModelAndView("redirect:list.do");
			} catch(Throwable th) {
				res = createEditModelAndView(profile, "profile.commit.error");
			}
		}
		
		return res;
	}
	
	// Like ---------------------------------------------------
	
	@RequestMapping(value = "/like", method = RequestMethod.GET)
	public ModelAndView like(@RequestParam int profileId) {
		ModelAndView res;
		
		profileService.addLiker(profileId);
		
		res = new ModelAndView("redirect:list.do");
		
		return res;
	}
	
	// Dislike ------------------------------------------------
	
	@RequestMapping(value = "/dislike", method = RequestMethod.GET)
	public ModelAndView dislike(@RequestParam int profileId) {
		ModelAndView res;
		
		profileService.removeLiker(profileId);
		
		res = new ModelAndView("redirect:list.do");
		
		return res;
	}
	
	// Ancillary methods --------------------------------------
	
	protected ModelAndView createEditModelAndView(Profile profile) {
		ModelAndView res;
		
		res = createEditModelAndView(profile, null);
		
		return res;
	}
	
	protected ModelAndView createEditModelAndView(Profile profile, String message) {
		ModelAndView res;
		
		res = new ModelAndView("profile/edit");
		res.addObject("profile", profile);
		res.addObject("message", message);
		
		return res;
	}

}