package com.bk.ms.db.repo;

import com.bk.ms.db.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country,String> {

    @Query("SELECT c.name FROM Country c")
    List<String> findAllCountryNames();
}