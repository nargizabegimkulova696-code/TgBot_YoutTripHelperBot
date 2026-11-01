package org.example.server;

import org.example.command.places.PlacesProvider;
import org.example.command.places.PlacesProvider.Place;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервер по состоянию, в котором находится пользователь
 * после выбора одной из категорий: food, culture, attractions,
 * обрабатывает тег, выбранный пользователем
 */
public class TripServer {
    private final PlacesProvider provider;

    public TripServer(PlacesProvider provider) {
        this.provider = provider;
    }

    public String getPlaces(String city, String tag) {
        try {
            List<Place> places = provider.getPlaces(city, tag);

            if (places.isEmpty()) {
                return null;
            }

            return formatted(places);
        } catch (IOException e) {
            return null;
        }
    }

    private String formatted(List<Place> places) {
        List<String> blocks = new ArrayList<>();

        for (Place place : places) {
            blocks.add("- " + place.format());
        }

        return String.join("\n\n", blocks);
    }
}
