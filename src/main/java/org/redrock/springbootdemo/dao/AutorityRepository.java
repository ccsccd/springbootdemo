package org.redrock.springbootdemo.dao;

import org.redrock.springbootdemo.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AutorityRepository extends JpaRepository<Authority,Integer> {
    Optional<Authority> findById(Integer id);
}
