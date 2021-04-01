package com.example;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;

@SpringBootTest
public class AnnotationsTest {

	@Test
	void temp() {

	}

	public void initialize(String... packageNames) {

		List<Class<?>> classes = getAllClasses(packageNames);

		classes.stream().forEach(clazz -> {
			if (!clazz.isAnnotationPresent(InitializerClass.class))
				continue;

			List<Method> methods = getAllInitializingMethods(clazz);

			Object instance = clazz.getDeclaredConstructor().newInstance();

			methods.stream().forEach(method -> {
				method.invoke(instance);
			});
		});
	}

	/*private List<Class<?>> getAllClasses(String... packageNames) throws URISyntaxException {

		List<Class<?>> allClasses = new ArrayList<>();

		for (String packageName : packageNames) {

			String packageRelativePath = packageName.replace(".", "/");
			URI packageURI = AnnotationsTest.class.getResource(packageRelativePath).toURI();
		}
	}*/

	private List<Method> getAllInitializingMethods(Class<?> clazz) {

		List<Method> initializingMethods = new ArrayList<>();

		for (Method method : clazz.getDeclaredMethods()) {

			if (method.isAnnotationPresent(InitializerMethod.class)) {
				initializingMethods.add(method);
			}
		}
		return initializingMethods;
	}

	private List<Class<?>> getAllPackageClasses(Path packagePath, String packageName) throws IOException, ClassNotFoundException {

		if (!Files.exists(packagePath))
			return Collections.emptyList();

		List<Path> files = Files.list(packagePath).filter(Files::isRegularFile).collect(Collectors.toList());

		List<Class<?>> classes = new ArrayList<>();

		files.stream().forEach(filePath -> {

			String fileName = filePath.getFileName().toString();
			System.err.println(fileName);

			if (fileName.endsWith(".class")) {

				String classFullName = packageName + "." + fileName.replaceFirst("\\.class$", "");

				try {
					Class<?> clazz = Class.forName(classFullName);
					classes.add(clazz);
				} catch (ClassNotFoundException classNotFoundException) {
					System.err.println(classNotFoundException);
					throw new RuntimeException(classNotFoundException);
				}
			}
		});

		return classes;
	}
}
