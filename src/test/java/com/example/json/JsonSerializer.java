package com.example.json;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.json.entity.Address;

@SpringBootTest
public class JsonSerializer {

	@Test
	void test() {

		try {
			Address address = new Address("Main Street", (short) 1);

			String json = objectToJson(address);
			System.err.println(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String objectToJson(Object instance) throws IllegalAccessException {

		Field[] fields = instance.getClass().getDeclaredFields();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("{");

		for (int i = 0; i < fields.length; i++) {

			Field field = fields[i];

			field.setAccessible(true);
			if (field.isSynthetic())
				continue;

			stringBuilder.append(formatStringValue(field.getName()));
			stringBuilder.append(":");

			if (field.getType().isPrimitive()) {
				stringBuilder.append(formatPrimitiveValue(field, instance));

			} else if (field.getType().equals(String.class)) {
				stringBuilder.append(formatStringValue(field.get(instance).toString()));
			}

			if (i != fields.length - 1) {
				stringBuilder.append(",");
			}
		}

		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	private static String formatPrimitiveValue(Field field, Object parentInstance) throws IllegalAccessException {

		if (field.getType().equals(boolean.class) || field.getType().equals(int.class) || field.getType().equals(long.class)
		|| field.getType().equals(short.class)) {

			return field.get(parentInstance).toString();
		} else if (field.getType().equals(double.class) || field.getType().equals(float.class)) {
			// limit the number of digits after the decimal point:
			return String.format("%.02f", field.get(parentInstance));
		}

		throw new RuntimeException(String.format("Type : %s is unsupported", field.getType().getName()));
	}

	private static String formatStringValue(String value) {
		return String.format("\"%s\"", value);
	}
}
