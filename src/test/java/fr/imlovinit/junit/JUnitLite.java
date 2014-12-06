package fr.imlovinit.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** Main class of this project. See the blog post for more information. */
public class JUnitLite {

    public static void main(String[] args) throws Exception {

	Result result = JUnitCore.runClass(ArrayListTest.class);
	System.out.println(result);

    }

    public static class JUnitCore {

	private RunNotifier notifier = new RunNotifier();

	public static Result runClass(Class<?> testClass) {
	    return new JUnitCore().run(new OurSimpleClassRunner(testClass));
	}

	private Result run(Runner runner) {
	    Result result = new Result();
	    RunListener listener = result.createListener();
	    notifier.addListener(listener);
	    runner.run(notifier);
	    return result;
	}

    }

    public interface Runner {

	/** Run the tests for this runner. */
	void run(RunNotifier notifier);
    }

    public static class OurSimpleClassRunner implements Runner {

	private final Class<?> testClass;
	private final TestIntrospector introspector;
	private final List<Method> beforeMethods;
	private final List<Method> afterMethods;

	public OurSimpleClassRunner(Class<?> testClass) {
	    this.testClass = testClass;
	    this.introspector = new TestIntrospector(testClass);
	    this.beforeMethods = introspector.getTestMethods(Before.class);
	    this.afterMethods = introspector.getTestMethods(After.class);
	}

	public void run(RunNotifier notifier) {
	    List<Method> testMethods = introspector.getTestMethods(Test.class);

	    for (Method eachTestMethod : testMethods) {
		invokeTestMethod(eachTestMethod, notifier);
	    }
	}

	private void invokeTestMethod(Method method, RunNotifier notifier) {
	    Description description = Description.createTestDescription(
		    testClass, method.getName());

	    try {
		Object test = createTest();
		notifier.fireTestStarted(description);

		invokeBeforeMethods(test);
		method.invoke(test);
		invokeAfterMethods(test); // should be run in finally

	    } catch (Throwable t) {
		Failure failure = new Failure(description, t);
		notifier.fireTestFailure(failure);
	    } finally {
		notifier.fireTestFinished(description);
	    }
	}

	private Object createTest() throws Exception {
	    return testClass.getConstructor().newInstance();
	}

	private void invokeBeforeMethods(Object test) throws Exception {
	    for (Method eachBeforeMethod : beforeMethods) {
		eachBeforeMethod.invoke(test);
	    }
	}

	private void invokeAfterMethods(Object test) throws Exception {
	    for (Method eachAfterMethod : afterMethods) {
		eachAfterMethod.invoke(test);
	    }
	}

    }

    public static class TestIntrospector {

	private final Class<?> testClass;

	public TestIntrospector(Class<?> testClass) {
	    this.testClass = testClass;
	}

	public List<Method> getTestMethods(
		Class<? extends Annotation> annotationClass) {
	    List<Method> results = new ArrayList<Method>();
	    Method[] methods = testClass.getDeclaredMethods();
	    for (Method eachMethod : methods) {
		Annotation annotation = eachMethod
			.getAnnotation(annotationClass);
		if (annotation != null && !isIgnored(eachMethod)) {
		    results.add(eachMethod);
		}
	    }
	    return results;
	}

	private boolean isIgnored(Method eachMethod) {
	    return eachMethod.getAnnotation(Ignore.class) != null;
	}

    }

    public static class Result {
	private int count;
	private List<Failure> failures = new ArrayList<Failure>();

	public int getCount() {
	    return count;
	}

	public List<Failure> getFailures() {
	    return failures;
	}

	private class Listener extends RunListener {

	    @Override
	    public void testStarted(Description description) {
	    }

	    @Override
	    public void testFinished(Description description) {
		count++;
	    }

	    @Override
	    public void testFailure(Failure failure) {
		failures.add(failure);
	    }

	}

	public RunListener createListener() {
	    return new Listener();
	}

    }

    public abstract static class RunListener {

	/** Called when an atomic test is about to be started. */
	public void testStarted(Description description) {
	}

	/**
	 * Called when an atomic test has finished, whether the test succeeds or
	 * fails.
	 */
	public void testFinished(Description description) {
	}

	/**
	 * Called when an atomic test fails, or when a listener throws an
	 * exception.
	 */
	public void testFailure(Failure failure) {
	}

    }

    public static class Failure {
	private final Description description;
	private final Throwable thrownException;

	public Failure(Description description, Throwable thrownException) {
	    this.description = description;
	    this.thrownException = thrownException;
	}

	public Description getDescription() {
	    return description;
	}

	public Throwable getThrownException() {
	    return thrownException;
	}

    }

    public static class Description {

	private final String displayName;

	public Description(String displayName) {
	    this.displayName = displayName;
	}

	public String getDisplayName() {
	    return displayName;
	}

	public static Description createTestDescription(Class<?> clazz,
		String name) {
	    return new Description(String.format("%s(%s)", name,
		    clazz.getName()));
	}

    }

    public static class RunNotifier {
	private List<RunListener> listeners = new ArrayList<RunListener>();

	public void addListener(RunListener listener) {
	    listeners.add(listener);
	}

	/** Invoke to tell listeners that an atomic test is about to start. */
	public void fireTestStarted(final Description description) {
	    for (RunListener eachListener : listeners) {
		eachListener.testStarted(description);
	    }
	}

	/** Invoke to tell listeners that an atomic test failed. */
	public void fireTestFailure(Failure failure) {
	    for (RunListener eachListener : listeners) {
		eachListener.testFailure(failure);
	    }
	}

	/** Invoke to tell listeners that an atomic test finished. */
	public void fireTestFinished(final Description description) {
	    for (RunListener eachListener : listeners) {
		eachListener.testFinished(description);
	    }
	}
    }

}
