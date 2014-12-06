package fr.imlovinit.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Demo Unit Test class using @Before, @Test, @Ignore and having passing and
 * failing tests.
 */
public class ArrayListTest {

    private ArrayList<String> instance;

    @Before
    public void setUp() {
	instance = new ArrayList<String>();
    }

    @Test
    public void newArrayListsHaveNoElements() {
	assertThat(instance.size(), is(0));
    }

    @Test
    public void sizeReturnsNumberOfElements() {
	instance.add("Item 1");
	instance.add("Item 2");
	assertThat(instance.size(), is(2));
    }

    @Test
    @Ignore
    public void removeDeletesTheGivenElement() {
	instance.remove("Item 1"); // FIXME
	assertThat(instance.size(), is(0));
    }

    @Test
    public void duplicateElementsAreNotAllowed() {
	instance.add("Item 1");
	instance.add("Item 1");
	assertThat(instance.size(), is(1));
    }

}
