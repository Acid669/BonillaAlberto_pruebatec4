package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.model.User;

public interface IUserService {

    User addUser(User user);

    User findByEmail(String email);

}
