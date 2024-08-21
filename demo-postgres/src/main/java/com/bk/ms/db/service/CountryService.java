package com.bk.ms.db.service;

import com.bk.ms.db.entity.Country;
import com.bk.ms.db.repo.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        var countries = countryRepository.findAll();
        log.info("Countries: {}", countries);
        return countries;
    }

    public List<String> getAllCountryNames() {
        return countryRepository.findAllCountryNames();
    }

    public Country insertCountry(Country country) {
        return countryRepository.save(country);
    }

    public List<Country> insertCountries(List<Country> countries) {
        return countryRepository.saveAll(countries);
    }


}