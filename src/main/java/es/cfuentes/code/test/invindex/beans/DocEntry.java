package es.cfuentes.code.test.invindex.beans;

public class DocEntry {

	private String name;
	private Long frec;
	
	/**
	 * @param name
	 * @param frec
	 */
	public DocEntry(String name, Long frec) {
		super();
		this.name = name;
		this.frec = frec;
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
	 * @return the frec
	 */
	public Long getFrec() {
		return frec;
	}

	/**
	 * @param frec the frec to set
	 */
	public void setFrec(Long frec) {
		this.frec = frec;
	}
	
	
}
