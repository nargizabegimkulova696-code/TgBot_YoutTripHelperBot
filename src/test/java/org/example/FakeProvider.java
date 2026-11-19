package org.example;

import org.example.command.places.PlacesProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Фейковый источник мест вместо API, места не имеют отношения к выбранному городу
 * цель протестировать в TagHandlerTest и PagingTest логику обработки тегов и пейджинга
 */
public class FakeProvider implements PlacesProvider {
    @Override
    public List<Place> getPlaces(String city, String tag) throws IOException {
        if (tag.equals("tag:movie")) {
            throw new IOException("ошибка доступа к местам от API");
        }

        if (tag.equals("tag:bakeries")) {
            List<Place> places = new ArrayList<>();

            places.add(new Place("ДжоВан, голландская пекарня", "ул. «Ангел Кънчев» 37"));
            places.add(new Place("Предимно Пекарна", "ул. «Княз Александър Дондуков» 49"));
            places.add(new Place("СинаМомс", ""));
            places.add(new Place("Фурна", "«Стефан Стамболов» 3"));
            places.add(new Place("хляб", "ул. «Екзарх Йосиф» 69"));
            places.add(new Place("Тита и Йойо", "ул. «Цар Шишман» 2"));
            places.add(new Place("Галета, пекарня и кафе", "ул. «Екзарх Йосиф» 36"));
            places.add(new Place("Уан Бейкери", "ул. «Черковна» 68"));
            places.add(new Place("Пура Вита, бельгийская пекарня", "ул. «Цар Освободител» 27"));
            places.add(new Place("Кейк Лаб", "ул. «Карнеградска» 14"));
            places.add(new Place("Дом выпечки", "ул. Гагарина 27а"));
            places.add(new Place("Французский пекарь", "ул. 8 марта"));
            places.add(new Place("Дом хлеба", "ул. Ленина"));

            return places;
        }

        if (!tag.equals("tag:cafe")) {
            return List.of();
        }

        return List.of(
                new PlacesProvider.Place("Кафе 1", "ул. Ленина, 1"),
                new PlacesProvider.Place("Кафе 2", "ул. Ленина, 2"),
                new PlacesProvider.Place("Кафе 3", ""),
                new PlacesProvider.Place("Кафетерий 4", "ул. Тургенева, 4"),
                new PlacesProvider.Place("Кафе 5", "ул. Ленина, 5"),
                new PlacesProvider.Place("Кафе 6", "ул. Московская, 6"),
                new PlacesProvider.Place("Кафе 7", "ул. 8 марта, 8"));
    }
}
