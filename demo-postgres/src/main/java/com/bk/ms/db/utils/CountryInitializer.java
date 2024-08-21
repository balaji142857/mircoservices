package com.bk.ms.db.utils;

import com.bk.ms.db.entity.Country;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;

@Slf4j
@UtilityClass
public class CountryInitializer {


    public static List<Country> readCountries() {
        List<Country> countryList = new ArrayList<>();
        try {
            InputStream s = CountryInitializer.class.getResourceAsStream("/countries.json");
            Map<String,Map<String, Object>> result = new ObjectMapper().readValue(s, HashMap.class);
            for (Map.Entry<String,Map<String,Object>> entry : result.entrySet()) {
                Map<String,Object> valueMap = entry.getValue();
                String name = valueMap.get("name").toString();
                String nativeName = valueMap.get("native").toString();
                String continent = valueMap.get("continent").toString();
                String capital = valueMap.get("capital").toString();
                String currency = valueMap.get("currency").toString();
                String languages = valueMap.get("languages").toString();
                int phone;
                try{
                    phone = Integer.parseInt(valueMap.get("phone").toString());
                }catch (NumberFormatException exception){
                    phone = -1;
                }
                Country c = Country.builder()
                        .code(entry.getKey())
                        .name(name)
                        .nativeName(nativeName)
                        .continent(continent)
                        .capital(capital)
                        .currency(currency)
                        .language(languages)
                        .phone(phone)
                        .build();
                countryList.add(c);
            }
        } catch(Exception exception) {
            log.error("An error occurred in file operations." ,exception );
        }
        return countryList;
    }

}