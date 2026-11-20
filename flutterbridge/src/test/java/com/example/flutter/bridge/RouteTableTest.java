package com.example.flutter.bridge;

import org.junit.Test;
import static org.junit.Assert.*;

public class RouteTableTest {
    private final RouteTable routes = new RouteTable()
            .flutter("/detail/{id}", route -> route.positiveInt("id"))
            .flutter("/communications", route -> {});

    @Test
    public void decodesEncodedQueryAndMatchesTemplate() {
        RouteTable.Match match = routes.resolve(
                "samples://flutter/detail/42?title=%E4%B8%AD%2B%26%2F");
        assertEquals(42, match.request.positiveInt("id"));
        assertEquals("中+&/", match.request.query.get("title"));
        assertNull(match.nativeDestination);
    }

    @Test
    public void rejectsInvalidParametersAndUrls() {
        for (String url : new String[]{
                "samples://flutter/detail/42?title=%FF", "samples://flutter/detail/0", "samples://flutter/detail/42?title=a&title=b",
                "samples://flutter/detail/a%2Fb", "samples://flutter/detail/1#fragment",
                "samples://user@flutter/communications", "https://flutter/communications"
        }) {
            assertThrows(url, IllegalArgumentException.class, () -> routes.resolve(url));
        }
    }

    @Test
    public void reportsMissingRouteAndDuplicateRegistration() {
        assertThrows(RouteTable.UnknownRoute.class,
                () -> routes.resolve("samples://flutter/missing"));
        assertThrows(IllegalArgumentException.class,
                () -> routes.flutter("/detail/{other}", route -> {}));
    }
}
