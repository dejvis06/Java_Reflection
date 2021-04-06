package com.example.section7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.section7.annotations.DependsOn;
import com.example.section7.annotations.FinalResult;
import com.example.section7.annotations.Operation;
import com.example.section7.lecture28.BestGamesFinder;

@SpringBootTest
public class ParameterAnnotations {

	@Test
	void parameterAnnotation() {

		try {
			BestGamesFinder bestGamesFinder = new BestGamesFinder();
			List<String> bestGamesInDescOrder = execute(bestGamesFinder);

			System.err.println(bestGamesInDescOrder);

			/* BestGamesFinder bestGamesFinder = new BestGamesFinder();
			 * 
			 * Set<String> games = bestGamesFinder.getAllGames();
			 * 
			 * Map<String, Float> gameToRating =
			 * bestGamesFinder.getGameToRating(games);
			 * Map<String, Float> gameToPrice =
			 * bestGamesFinder.getGameToPrice(games);
			 * 
			 * SortedMap<Double, String> scoreToGame =
			 * bestGamesFinder.scoreGames(gameToPrice, gameToRating);
			 * List<String> bestGamesInDescendingOrder =
			 * bestGamesFinder.getTopGames(scoreToGame);
			 * 
			 * System.err.println(bestGamesInDescendingOrder); */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T> T execute(Object instance) throws InvocationTargetException, IllegalAccessException {

		Class<?> clazz = instance.getClass();

		Map<String, Method> operationToMethod = getOperationToMethod(clazz);
		Method finalResultMethod = findFinalResultMethod(clazz);

		return (T) executeWithDependencies(instance, finalResultMethod, operationToMethod);
	}

	private Object executeWithDependencies(Object instance, Method currentMethod, Map<String, Method> operationToMethod)
	throws InvocationTargetException, IllegalAccessException {

		List<Object> parameterValues = new ArrayList<>(currentMethod.getParameterCount());

		for (Parameter parameter : currentMethod.getParameters()) {

			System.err.println("entered getParameters loop: ");
			Object value = null;

			if (parameter.isAnnotationPresent(DependsOn.class)) {

				System.err.println("entered isAnnotationPresent");

				String dependencyOperationName = parameter.getAnnotation(DependsOn.class).value();
				Method dependencyMethod = operationToMethod.get(dependencyOperationName);

				System.err.println(String.format("dependencyOperationName %s, dependencyMethod: %s", dependencyOperationName, dependencyMethod));
				value = executeWithDependencies(instance, dependencyMethod, operationToMethod);
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
