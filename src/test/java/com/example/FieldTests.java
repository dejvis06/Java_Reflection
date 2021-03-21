package com.example;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class FieldTests {

	@Test
	void test() {

		try {

			Movie movie = new Movie("Lord of the Rings", 2001, 12.99, true, Category.ADVENTURE);

			printDeclaredFieldsInfo(movie.getClass(), movie);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static <T> void printDeclaredFieldsInfo(Class<? extends T> clazz, T instance)
			throws IllegalAccessException, NoSuchFieldException {

		for (Field field : clazz.getDeclaredFields()) {

			field.setAccessible(true);
			System.err.println(String.format("Field name : %s, type : %s, value: %s", field.getName(),
					field.getType().getName(), field.get(instance)));

		}

		// Superclass fields:
		for (Field field : clazz.getSuperclass().getDeclaredFields()) {

			field.setAccessible(true);
			System.err.println(String.format("Field name : %s, type : %s, value: %s", field.getName(),
					field.getType().getName(), field.get(instance)));

		}
	}

	private enum Category {
		ADVENTURE, ACTION, COMEDY
	}

	public static class Movie extends Product {

		public static final double MINIMUM_PRICE = 10.99;

		private boolean isReleased;
		private Category category;
		private double actualPrice;

		public Movie(String name, int year, double price, boolean isReleased, Category category) {
			super(name, year);
			this.isReleased = isReleased;
			this.category = category;
			this.actualPrice = Math.max(price, MINIMUM_PRICE);
		}

		// Nested class
		public class MovieStats {

			private double timesWatched;

			public MovieStats(double timesWatched) {
				this.timesWatched = timesWatched;
			}

			public double getRevenue() {
				return timesWatched * actualPrice;
			}
		}
	}

	// Superclass
	public static class Product {

		protected String name;
		protected int year;

		public Product(String name, int year) {
			this.name = name;
			this.year = year;
		}
	}
}
