package es.cfuentes.code.text.invindex.index;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.DocEntry;
import es.cfuentes.code.text.invindex.beans.ScoredDocument;
import es.cfuentes.code.text.invindex.ifaces.DocumentIndex;
import es.cfuentes.code.text.invindex.ifaces.DocumentScorer;

/*
 * Inverted index implementation
 */
@Component
public class InvertedIndex implements DocumentIndex {

	// Document scorer instance used for getting document score
	@Autowired
	private DocumentScorer scorer;

	// Store the index for term -> document + frecuency
	private Map<String, Set<DocEntry>> ie = new HashMap<String, Set<DocEntry>>();

	private Logger log = Logger.getLogger(this.getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cfuentes.code.text.invindex.index.DocumentIndex#indexDocument(java.io.
	 * File)
	 */
	@Override
	@Async
	public void indexDocument(File file) throws InterruptedException, ExecutionException {

		// Get term decomposition for the document
		Map<String, Long> terms = scorer.processDocument(file).get();
		// Updates the document index
		updateIndex(terms, file.getAbsolutePath());
	}

	private synchronized void updateIndex(Map<String, Long> terms, String fileName) {
		// Build proper index based on term and document
		for (Entry<String, Long> entry : terms.entrySet()) {
			if (!ie.containsKey(entry.getKey()))
				ie.put(entry.getKey(), new HashSet<DocEntry>());

			ie.get(entry.getKey()).add(new DocEntry(fileName, entry.getValue()));
		}
		logIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.cfuentes.code.text.invindex.index.DocumentIndex#getTerm(java.lang.String)
	 */
	@Override
	public synchronized Set<ScoredDocument> getTerm(String term) {
		// If there is an entry for given term, we return the scored documents for it
		if (ie.containsKey(term))
			return ie.get(term)
					// Iterate over the list of doc entries
					.stream()
					// Call score method in scorer
					.map(de -> scorer.scoreDocument(de, term))
					// Get results in given collection
					.collect(Collectors.toSet());
		else
			// If there is no entry, return an empty set
			return Collections.emptySet();
	}

	private void logIndex() {
		for (Entry<String, Set<DocEntry>> entry : ie.entrySet()) {
			log.fine(entry.getKey());
			for (DocEntry de : entry.getValue()) {
				log.fine("\t" + de.getName() + " " + de.getFrec());
			}
		}
	}
}
