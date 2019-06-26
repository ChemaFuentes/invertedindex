package es.cfuentes.code.test.invindex.ifaces;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

import es.cfuentes.code.test.invindex.beans.DocEntry;
import es.cfuentes.code.test.invindex.beans.ScoredDocument;

/*
 * Interface for document scorer
 */
public interface DocumentScorer {

	// Method used for processing a document before getting a score
	Future<Map<String, Long>> processDocument(File file);

	// Method used for getting the document score
	ScoredDocument scoreDocument(DocEntry doc, String term);

}