package com.example.section7.lecture28;

import java.util.*;
import java.util.stream.Collectors;

public class Database {
	/// Game Name -> (Rating, Price)
	private Map<String, List<Float>> GAME_TO_PRICE = new HashMap<String, List<Float>>() {
		{
			put("Fortnite", Arrays.asList(5f, 10f));
			put("Minecraft", Arrays.asList(4.3f, 100f));
			put("League Of Legends", Arrays.asList(4.9f, 89f));
			put("Ace Combat", Arrays.asList(4.8f, 50f));
			put("StarCraft", Arrays.asList(4.7f, 66f));
			put("Burnout", Arrays.asList(4.4f, 31f));
		}
	};

	public Set<String> readAllGames() {
		return Collections.unmodifiableSet(GAME_TO_PRICE.keySet());
	}

	public Map<String, Float> readGameToRatings(Set<String> games) {

		Map<String, Float> gameRatings = new HashMap<>();

		GAME_TO_PRICE.entrySet().stream().filter(entry -> games.contains(entry.getKey())).forEach(entry -> {
			gameRatings.put(entry.getKey(), entry.getValue().get(0));
		});

		return Collections.unmodifiableMap(gameRatings);
	}

	public Map<String, Float> readGameToPrice(Set<String> games) {

		Map<String, Float> gameRatings = new HashMap<>();

		GAME_TO_PRICE.entrySet().stream().filter(entry -> games.contains(entry.getKey())).forEach(entry -> {
			gameRatings.put(entry.getKey(), entry.getValue().get(1));
		});

		return Collections.unmodifiableMap(gameRatings);
	}
}