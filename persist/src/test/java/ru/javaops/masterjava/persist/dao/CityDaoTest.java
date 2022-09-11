package ru.javaops.masterjava.persist.dao;

import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.persist.CityTestData;
import ru.javaops.masterjava.persist.model.City;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.javaops.masterjava.persist.CityTestData.CITY_COMPARATOR;
import static ru.javaops.masterjava.persist.CityTestData.getCities;
import static ru.javaops.masterjava.persist.CityTestData.getCityToSave;

public class CityDaoTest extends AbstractDaoTest<CityDao> {
    public CityDaoTest() {
        super(CityDao.class);
    }

    private static void assertCitiesAreEqual(List<City> expectedCities, List<City> actualCities) {
        assertNotNull(actualCities);
        assertEquals(expectedCities.size(), actualCities.size());

        actualCities.sort(CITY_COMPARATOR);
        expectedCities.sort(CITY_COMPARATOR);

        for (int i = 0; i < expectedCities.size(); i++) {
            City actualCity = actualCities.get(i);
            City expectedCity = expectedCities.get(i);

            assertNotNull(actualCity);
            assertEquals(expectedCity, actualCity);
        }
    }

    @Before
    public void setUp() {
        CityTestData.setUp();
    }

    @Test
    public void getWithLimitTest() {
        assertCitiesAreEqual(getCities(), dao.getWithLimit(5));
    }

    @Test
    public void insertTest() {
        dao.insert(getCityToSave());

        List<City> expectedCities = getCities();
        expectedCities.add(getCityToSave());

        assertCitiesAreEqual(expectedCities, dao.getWithLimit(5));
    }
}
