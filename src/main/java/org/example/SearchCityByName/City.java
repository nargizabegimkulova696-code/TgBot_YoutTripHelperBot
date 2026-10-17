package org.example.SearchCityByName;

public class City {
    private final String name;
    private final int population;

    public City(String name, int population) {
        this.name = name;
        this.population = population;
    }

    public String getCityName() {
        return name;
    }

    public int getCityPopulation() {
        return population;
    }
}
