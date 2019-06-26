package es.cfuentes.code.test.invindex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import es.cfuentes.code.test.invindex.ifaces.DocumentIndex;

@SpringBootTest(
		  classes = InvertedIndexApp.class)
@ActiveProfiles("test")
public abstract class SpringTestConfig {
	
	@Autowired
	private DocumentIndex documentIndex;
	
	@Value("${monitor.folder:documents}")
	private String folder;
	
	@Value("${monitor.terms}")
	private String[] terms;

	@Value("${num.docs}")
	private int limit;

	/**
	 * @return the documentIndex
	 */
	public DocumentIndex getDocumentIndex() {
		return documentIndex;
	}

	/**
	 * @param documentIndex the documentIndex to set
	 */
	public void setDocumentIndex(DocumentIndex documentIndex) {
		this.documentIndex = documentIndex;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the terms
	 */
	public String[] getTerms() {
		return terms;
	}

	/**
	 * @param terms the terms to set
	 */
	public void setTerms(String[] terms) {
		this.terms = terms;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}		
}
