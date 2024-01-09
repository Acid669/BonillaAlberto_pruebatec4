package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.model.User;
import com.hackaboss.agenciaviajes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepo;


    //Método para crear un usuario
    @Override
    public User addUser(User user) {

        //Validaciones para asegurar la corrección de los datos del usuario
        if (!user.getName().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ ]+$")) {
            throw new IllegalArgumentException("The name must contain only alphabetical characters");
        }

        if (!user.getLastName().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ ]+$")) {
            throw new IllegalArgumentException("The last name must contain only alphabetical characters");
        }

        if (!user.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("The email is not valid");
        }
        if (!user.getPassPort().matches("^[A-Za-z0-9]{5,9}$")) {
            throw new IllegalArgumentException("invalid passport number");
        }
        if (user.getAge() < 0) {
            throw new IllegalArgumentException("The age must not be a negative value");
        }

        //Asignación de valores al objeto usuario
        user.setName(user.getName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        user.setPassPort(user.getPassPort());
        user.setAge(user.getAge());

        return userRepo.save(user);
    }

    //Método para buscar un usuario por su email
    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
