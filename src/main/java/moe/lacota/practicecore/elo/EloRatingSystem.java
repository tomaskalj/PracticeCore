package moe.lacota.practicecore.elo;

public class EloRatingSystem {
	private static final int DEFAULT_KFACTOR = 25;

	private static final int WIN = 1;
	private static final int LOSS = 0;

	private KFactor[] kFactors;

	public EloRatingSystem(KFactor... kFactors) {
		this.kFactors = kFactors;
	}

	public int getNewRating(int rating, int opponentRating, boolean won) {
		if (won) {
			return getNewRating(rating, opponentRating, WIN);
		} else {
			return getNewRating(rating, opponentRating, LOSS);
		}
	}

	public int getNewRating(int rating, int opponentRating, int score) {
		double kFactor = getKFactor(rating);
		double expectedScore = getExpectedScore(rating, opponentRating);

		return calculateNewRating(rating, score, expectedScore, kFactor); // The new rating
	}

	private int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
		return oldRating + (int) (kFactor * (score - expectedScore));
	}

	private double getKFactor(int rating) {
		for (int i = 0; i < kFactors.length; i++) {
			if (rating >= kFactors[i].getStartIndex() && rating <= kFactors[i].getEndIndex()) {
				return kFactors[i].getValue();
			}
		}
		return DEFAULT_KFACTOR;
	}

	private double getExpectedScore(int rating, int opponentRating) {
		return 1 / (1 + Math.pow(10, ((double) (opponentRating - rating) / 400)));
	}
}
