package com.example.section7.lecture28;

import java.util.*;

import com.example.section7.annotations.DependsOn;
import com.example.section7.annotations.FinalResult;
import com.example.section7.annotations.Operation;

public class BestGamesFinder {

	private Database database = new Database();

	@Operation("All-Games")
	public Set<String> getAllGames() {
		return database.readAllGames();
	}

	@Operation("Game-To-Price")
	public Map<String, Float> getGameToPrice(@DependsOn("All-Games") Set<String> games) {
		return database.readGameToPrice(games);
	}

	@Operation("Game-To-Rating")
	public Map<String, Float> getGameToRating(@DependsOn("All-Games") Set<String> games) {
		return database.readGameToRatings(games);
	}

	@Operation("Score-To-Game")
	public SortedMap<Double, String> scoreGames(@DependsOn("Game-To-Price") Map<String, Float> gameToPrice,
	@DependsOn("Game-To-Rating") Map<String, Float> gameToRating) {

		SortedMap<Double, String> gameToScore = new TreeMap<>(Collections.reverseOrder());
		for (String gameName : gameToPrice.keySet()) {
			double score = (double) gameToRating.get(gameName) / gameToPrice.get(gameName);
			gameToScore.put(score, gameName);
		}

		return gameToScore;
	}

	@FinalResult
	public List<String> getTopGames(@DependsOn("Score-To-Game") SortedMap<Double, String> gameToScore) {
		return new ArrayList<>(gameToScore.values());
	}
}