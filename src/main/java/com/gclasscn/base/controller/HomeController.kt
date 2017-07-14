package com.gclasscn.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
open class HomeController {

	@RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
	open fun home(): String{
		return "home";
	}
	
}
