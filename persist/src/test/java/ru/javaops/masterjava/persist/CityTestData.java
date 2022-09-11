package ru.javaops.masterjava.persist;

import lombok.experimental.UtilityClass;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class CityTestData {
    public static final int FIRST_CITY_ID = 1;
    public static final int SECOND_CITY_ID = 2;
    public static final int THIRD_CITY_ID = 3;
    public static final int FOURTH_CITY_ID = 4;
    public static final int OTHER_CITY_ID = 5;

    public static final Comparator<City> CITY_COMPARATOR = Comparator.comparing(City::getName);

    private static final City FIRST_CITY = new City(FIRST_CITY_ID, "Saint-Petersburg");
    private static final City SECOND_CITY = new City(SECOND_CITY_ID, "Moscow");
    private static final City THIRD_CITY = new City(THIRD_CITY_ID, "Gorky");
    private static final City FOURTH_CITY = new City(FOURTH_CITY_ID, "Yekaterinburg");
    private static final City OTHER_CITY = new City(OTHER_CITY_ID, "Omsk");

    public static List<City> getCities() {
        return Stream
                .of(FIRST_CITY, SECOND_CITY, THIRD_CITY, FOURTH_CITY)
                .map(city -> new City(city.getId(), city.getName()))
                .collect(Collectors.toList());
    }

    public static City getCityToSave() {
        return new City(OTHER_CITY.getId(), OTHER_CITY.getName());
    }

    public static void setUp() {
        CityDao dao = DBIProvider.getDao(CityDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((connection, status) -> getCities().forEach(dao::insert));
    }
}
