package com.example.section7;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.section7.annotations.InitializerClass;
import com.example.section7.annotations.InitializerMethod;
import com.example.section7.annotations.RetryOperation;
import com.example.section7.annotations.ScanPackages;
import com.example.section7.lecture28.BestGamesFinder;

@SpringBootTest
@ScanPackages({ "com.example.section7.lecture27" })
public class AnnotationsTest {

	// @Test
	void scanPackage() throws Throwable {

		ScanPackages scanPackages = AnnotationsTest.class.getAnnotation(ScanPackages.class);

		// "com.example.section7"
		initialize(scanPackages.value());
	}

	private void initialize(String... packageNames) throws Throwable {

		try {
			List<Class<?>> classes = getAllClasses(packageNames);

			for (Class<?> clazz : classes) {

				if (!clazz.isAnnotationPresent(InitializerClass.class))
					continue;

				List<Method> methods = getAllInitializingMethods(clazz);

				Object instance = clazz.getDeclaredConstructor().newInstance();

				// System.err.println("class: " + instance.getClass());
				for (Method method : methods) {
					// System.err.println("method: " + method.getName());
					callInitializingMethod(instance, method);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void callInitializingMethod(Object instance, Method method) throws Throwable {

		RetryOperation retryOperation = method.getAnnotation(RetryOperation.class);

		int numberOfRetries = retryOperation == null ? 0 : retryOperation.numberOfRetries();

		while (true) {

			try {
				method.invoke(instance);
				break;
			} catch (InvocationTargetException e) {

				Throwable targetException = e.getTargetException();
				System.err.println(String.format("e: %s, targetEx.getClass(): %s ", e, targetException.getClass()));

				if (numberOfRetries > 0 && Arrays.asList(retryOperation.retryExceptions()).contains(targetException.getClass())) {

					numberOfRetries--;
					System.err.println("Retrying ...");
					Thread.sleep(retryOperation.durationBetweenRetriesMs());
				} else if (retryOperation == null) {
					throw new Exception(retryOperation.failureMessage(), targetException);
				} else {
					throw targetException;
				}
			}
		}

	}

	private List<Class<?>> getAllClasses(String... packageNames) throws URISyntaxException, ClassNotFoundException, IOException {

		List<Class<?>> allClasses = new ArrayList<>();

		for (String packageName : packageNames) {

			String packageRelativePath = packageName.replace(".", "/");

			String resource = packageRelativePath.substring(packageRelativePath.lastIndexOf("/")).replace("/", "");
			System.err.println(resource);
			URI packageURI = AnnotationsTest.class.getResource(resource).toURI();

			if (packageURI.getScheme().equals("file")) {

				Path packageFullPath = Paths.get(packageURI);
				allClasses.addAll(getAllPackageClasses(packageFullPath, packageName));

			} else if (packageURI.getScheme().equals("jar")) {

				FileSystem fileSystem = FileSystems.newFileSystem(packageURI, Collections.emptyMap());

				Path packageFullPathInJar = fileSystem.getPath(packageRelativePath);
				allClasses.addAll(getAllPackageClasses(packageFullPathInJar, packageName));

				fileSystem.close();
			}
		}
		return allClasses;
	}

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

			if (fileName.endsWith(".class")) {

				System.err.println("packageName: " + packageName);
				// "com.example.section7.AutoSaver";
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
