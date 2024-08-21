package com.bk.ms.redis.repos;

import com.bk.ms.redis.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class CountryRepository {

    private final List<Country> countries = new CopyOnWriteArrayList<>();

    public List<Country> findAll() {
        return Collections.unmodifiableList(countries);
    }

    public List<String> findAllCountryNames() {
        return countries.stream().map(Country::getName).toList();
    }

    public Country save(Country country) {
        countries.add(country);
        return country;
    }

    public List<Country> saveAll(List<Country> countriesToSave) {
        this.countries.addAll(countriesToSave);
        return countriesToSave;
    }

    public void removeAll() {
        this.countries.clear();
    }
}
