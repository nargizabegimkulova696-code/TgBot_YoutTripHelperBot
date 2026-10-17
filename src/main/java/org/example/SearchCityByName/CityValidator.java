package org.example.SearchCityByName;

public class CityValidator {
    private static final int MIN_POPULATION = 500;
    private final CityCatalog catalog;
    //объединить классы
    private final ToNormalStr toNormal = new ToNormalStr();

    public CityValidator(CityCatalog catalog) {
        this.catalog = catalog;
    }

    public boolean contain(String cityName) {
        if (cityName == null) {
            return false;
        }

        String normalCity = toNormal.normal(cityName);
        City foundCity = catalog.findCity(normalCity);

        return foundCity != null && foundCity.getCityPopulation() >= MIN_POPULATION;
    }
}
