package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Método para buscar un usuario por su email
    User findByEmail(String email);
}
