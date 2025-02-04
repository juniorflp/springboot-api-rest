package com.example.curso_api_rest_java.repositories;

import com.example.curso_api_rest_java.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);
}
