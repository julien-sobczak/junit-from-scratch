package fr.imlovinit.junit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/** Simple example to demonstrate why JUnit create a new instance between each test. */
public class WhyNewInstanceTest {

    private List<String> list = new ArrayList<String>();

    @Test
    public void testFirst() {
        list.add("one");
        assertEquals(1, list.size());
    }

    @Test
    public void testSecond() {
        assertEquals(0, list.size());
    }

}
