package org.redrock.springbootdemo.dao;

import org.redrock.springbootdemo.entity.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MappingRepository extends JpaRepository<Mapping,Integer> {
    List<Mapping> findByUserId(Integer userId);
}
