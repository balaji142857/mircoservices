package com.bk.ms.db.web;

import com.bk.ms.db.entity.Country;
import com.bk.ms.db.service.CountryService;
import com.bk.ms.db.utils.CountryInitializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/countries")
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public List<Country> getAllCountries(){
        return countryService.getAllCountries();
    }

    @GetMapping("/names")
    public List<String> getAllCountryNames() {
        log.info("Received request for country names");
        return countryService.getAllCountryNames();
    }

    @PutMapping
    public ResponseEntity<Country> insertCountry(@RequestBody Country countryDto){
        Country country = countryService.insertCountry(countryDto);
        return ResponseEntity.ok(country);
    }


    @PostMapping
    public String loadCountries() {
        var countriesList = CountryInitializer.readCountries();
        countryService.insertCountries(countriesList);
        return "OK";
    }
}
