package com.bk.ms.db.mapper;

import com.bk.ms.db.entity.Country;
import com.bk.ms.db.dto.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICountryMapper {

    Country toCountry(CountryDto countryDto);

    CountryDto fromCountry(Country country);


}