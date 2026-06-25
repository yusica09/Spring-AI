package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/")
	public String home() {
		return "home";
	}
	  
	@GetMapping("/prompt-template")
	public String promptTemplate() {
		return "prompt-template";
	}  
	
	@GetMapping("/multi-messages")
	public String multiMessages() {
	    return "multi-messages";
	}
	
	@GetMapping("/default-method")
	public String defaultMethod() {
		return "default-method";
	}
	
	@GetMapping("/zero-shot-prompt")
	public String zeroShotPrompt() {
		return "zero-shot-prompt";
	}
	
	@GetMapping("/few-shot-prompt")
	public String fewShotPrompt() {
		return "few-shot-prompt";
	}
	
	@GetMapping("/role-assignment")
	public String roleAssignment() {
		return "role-assignment";
	}
	
	
}
