package dev.jd.multihardcore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppTest {

    @Test
    public void testAddition() {

        assertEquals(10, App.add(5, 5));
        assertEquals(13, App.add(5, 8));
        // This test would fail
        //assertEquals(10, App.add(2, 7));
        assertEquals(7, App.add(2, 5));

    }
}
