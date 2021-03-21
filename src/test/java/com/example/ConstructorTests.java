package com.example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConstructorTests {

	@Test
	void test() {

		try {
			printConstructorsData(Person.class);

			Address address = createInstanceWithArguments(Address.class, "First Street", 10);

			Person person = createInstanceWithArguments(Person.class, address, "John", 20);
			System.err.println(person);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private static <T> T createInstanceWithArguments(Class<T> clazz, Object... args)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {

		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getParameterTypes().length == args.length) {

				return (T) constructor.newInstance(args);
			}
		}
		System.err.println("An appropriate constructor was not found");
		return null;
	}

	private static void printConstructorsData(Class<?> clazz) {
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		System.err.println(
				String.format("class %s has %d declared constructors", clazz.getSimpleName(), constructors.length));

		for (int i = 0; i < constructors.length; i++) {
			Class<?>[] parameterTypes = constructors[i].getParameterTypes();

			List<String> parameterTypeNames = Arrays.stream(parameterTypes).map(type -> type.getSimpleName())
					.collect(Collectors.toList());

			System.err.println(parameterTypeNames);
		}
	}

	public static class Person {
		private final Address address;
		private final String name;
		private final int age;

		public Person() {
			this.name = "anonymous";
			this.age = 0;
			this.address = null;
		}

		public Person(String name) {
			this.name = name;
			this.age = 0;
			this.address = null;
		}

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
			this.address = null;
		}

		public Person(Address address, String name, int age) {
			this.address = address;
			this.name = name;
			this.age = age;
		}

		@Override
		public String toString() {
			return "Person{" + "address=" + address + ", name='" + name + '\'' + ", age=" + age + '}';
		}
	}

	public static class Address {
		private String street;
		private int number;

		public Address(String street, int number) {
			this.street = street;
			this.number = number;
		}

		@Override
		public String toString() {
			return "Address{" + "street='" + street + '\'' + ", number=" + number + '}';
		}
	}
}
