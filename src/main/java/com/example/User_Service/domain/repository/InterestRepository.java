package com.example.User_Service.domain.repository;


import com.example.User_Service.domain.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long>{
    Optional<Interest> findByName(String name);
    
}
