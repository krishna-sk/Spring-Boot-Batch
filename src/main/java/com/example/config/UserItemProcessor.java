package com.example.config;

import org.springframework.batch.item.ItemProcessor;

import com.example.entity.User;
import com.example.model.UserModel;

public class UserItemProcessor implements ItemProcessor<UserModel, User> {

	@Override
	public User process(final UserModel userModel) throws Exception {

		return User.builder().id(Integer.parseInt(userModel.getId())).firstName(userModel.getFirstName()).
				lastName(userModel.getLastName()).email(userModel.getEmail()).
				gender(userModel.getGender()).ipAddress(userModel.getIpAddress()).
				streetNumber(userModel.getStreetNumber()).build();
	}

}
