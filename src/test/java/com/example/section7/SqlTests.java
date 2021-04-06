package com.example.section7;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.section7.annotations.DependsOn;
import com.example.section7.annotations.FinalResult;
import com.example.section7.annotations.Input;
import com.example.section7.annotations.Operation;
import com.example.section7.lecture29.SqlQueryBuilder;

@SpringBootTest
public class SqlTests {

	@Test
	void temp() {

		try {
			SqlQueryBuilder sqlQueryBuilder = new SqlQueryBuilder(Arrays.asList("1", "2", "3"), 10, "Movies", Arrays.asList("Id", "Name"));

			String sqlQuery = execute(sqlQueryBuilder);
			System.err.println(sqlQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, Field> getInputToField(Class<?> clazz) {

		Map<String, Field> inputToField = new HashMap<>();

		for (Field field : clazz.getDeclaredFields()) {

			if (!field.isAnnotationPresent(Input.class))
				continue;

			Input input = field.getAnnotation(Input.class);

			inputToField.put(input.value(), field);
		}

		return inputToField;
	}

	private <T> T execute(Object instance) throws InvocationTargetException, IllegalAccessException {

		Class<?> clazz = instance.getClass();

		Map<String, Method> operationToMethod = getOperationToMethod(clazz);
		Map<String, Field> inputToField = getInputToField(clazz);
		Method finalResultMethod = findFinalResultMethod(clazz);

		return (T) executeWithDependencies(instance, finalResultMethod, operationToMethod, inputToField);
	}

	private Object executeWithDependencies(Object instance, Method currentMethod, Map<String, Method> operationToMethod, Map<String, Field> inputToField)
	throws InvocationTargetException, IllegalAccessException {

		List<Object> parameterValues = new ArrayList<>(currentMethod.getParameterCount());

		for (Parameter parameter : currentMethod.getParameters()) {

			Object value = null;

			if (parameter.isAnnotationPresent(DependsOn.class)) {

				String dependencyOperationName = parameter.getAnnotation(DependsOn.class).value();
				Method dependencyMethod = operationToMethod.get(dependencyOperationName);

				value = executeWithDependencies(instance, dependencyMethod, operationToMethod, inputToField);

			} else if (parameter.isAnnotationPresent(Input.class)) {

				String inputName = parameter.getAnnotation(Input.class).value();

				System.err.println("inputname test: " + inputName);
				Field inputField = inputToField.get(inputName);
				inputField.setAccessible(true);

				value = inputField.get(instance);
			}

			parameterValues.add(value);
		}

		return currentMethod.invoke(instance, parameterValues.toArray());
	}

	private Map<String, Method> getOperationToMethod(Class<?> clazz) {

		Map<String, Method> operationNameToMethod = new HashMap<>();

		for (Method method : clazz.getDeclaredMethods()) {

			if (!method.isAnnotationPresent(Operation.class))
				continue;

			Operation operation = method.getAnnotation(Operation.class);
			operationNameToMethod.put(operation.value(), method);

		}
		return operationNameToMethod;
	}

	private Method findFinalResultMethod(Class<?> clazz) {

		for (Method method : clazz.getDeclaredMethods()) {

			if (method.isAnnotationPresent(FinalResult.class))
				return method;

		}
		throw new RuntimeException("No method found with FinalResult annotation.");
	}
}
