package es.cfuentes.code.text.invindex.ifaces;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import es.cfuentes.code.text.invindex.beans.ScoredDocument;

/*
 * Interface for document index
 */
public interface DocumentIndex {

	// Method for indexing documents
	public void indexDocument(File file) throws InterruptedException, ExecutionException;

	// Method for getting documents relevant for a given term
	public Set<ScoredDocument> getTerm(String term);

}