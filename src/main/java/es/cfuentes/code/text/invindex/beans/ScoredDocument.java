package es.cfuentes.code.text.invindex.beans;
/*
 * Generic class for a document with its score
 */
public class ScoredDocument {
	
	private String name;
	private Double score;
	
	/**
	 * @param name
	 * @param score
	 */
	public ScoredDocument(String name, Double score) {
		super();
		this.name = name;
		this.score = score;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}
}
