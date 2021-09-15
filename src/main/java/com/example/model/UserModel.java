package com.example.model;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
	
	
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String gender;
	
	private String ipAddress;
	
	private String streetNumber;
	
}
