package fr.imlovinit.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/** Useful to compare with JUnit. */
public class TestJUnit {

    public static void main(String[] args) throws Exception {

        Result result = JUnitCore.runClasses(ArrayListTest.class);
        System.out.println(result);
        
    }

}
