package com.bk.ms.redis.service;

import com.bk.ms.redis.model.Country;
import com.bk.ms.redis.repos.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bk.ms.redis.util.Constants.CACHE_NAME_COUNTRIES;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService  {

    private final CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        var countries = countryRepository.findAll();
        log.info("Countries: {}", countries);
        return countries;
    }

    @Cacheable(CACHE_NAME_COUNTRIES)
    public List<String> getAllCountryNames() {
        log.info("Database Accessed! Cache Mechanism Was Not Used.");
        return countryRepository.findAllCountryNames();
    }

    @Caching(evict = {
        @CacheEvict(value = CACHE_NAME_COUNTRIES, allEntries = true, condition = "#country.code != null")
    })
    public Country insertCountry(Country country) {
        return countryRepository.save(country);
    }

    @Caching(evict = {@CacheEvict(value = CACHE_NAME_COUNTRIES, allEntries = true)})
    public List<Country> insertCountries(List<Country> countries) {
        return countryRepository.saveAll(countries);
    }


    @Caching(evict = {@CacheEvict(value = CACHE_NAME_COUNTRIES, allEntries = true)})
    public void clearCountries() {
        countryRepository.removeAll();
    }

}