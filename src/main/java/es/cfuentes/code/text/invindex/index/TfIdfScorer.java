/**
 * 
 */
package es.cfuentes.code.text.invindex.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.DocEntry;
import es.cfuentes.code.text.invindex.beans.ScoredDocument;
import es.cfuentes.code.text.invindex.ifaces.DocumentScorer;

/*
 * TfIdf scorer implementation
 */
@Component
public class TfIdfScorer implements DocumentScorer {

	private Logger log = Logger.getLogger(this.getClass().toString());
	
	// Map to store term -> document frequency
	private Map<String, Long> termOccurence = new HashMap<String, Long>();
	
	/* (non-Javadoc)
	 * @see es.cfuentes.code.text.invindex.index.DocumentScorer#processDocument(java.io.File)
	 */
	@Override
	@Async
	public Future<Map<String, Long>> processDocument(File file) {
		Map<String, Long> ret = new HashMap<String, Long>();
		Stream<String> stream = null;
		try {
			// We create a stream from file lines
			stream = Files.lines(Paths.get(file.toURI()));
			// We store results from lines analysis
			ret.putAll(stream
					// We split lines looking for blank spaces.
					// TODO line tokenization may be improved 
					.flatMap(Pattern.compile(" ")::splitAsStream)
					// We group by word and get the count for each word
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
			// We update the internal termOccurrence map
			updateStats(ret);
			
		} catch (IOException e) {
			log.severe("Error while processing file: " + e);
		} finally {
			if (stream != null)
				stream.close();
		}
		// Return the async collection
		return new AsyncResult<Map<String, Long>>(ret);
	}
	
	/*
	 * Internal method needed to update internal frecuency stats
	 */
	private synchronized void updateStats(Map<String, Long> stats) {		
			for (String term : stats.keySet()) {
				if (termOccurence.containsKey(term))
					termOccurence.put(term, termOccurence.get(term) + 1);
				else
					termOccurence.put(term, 1L);
			}
			logFreq();
	}
	
	/* (non-Javadoc)
	 * @see es.cfuentes.code.text.invindex.index.DocumentScorer#scoreDocument(es.cfuentes.code.text.invindex.beans.DocEntry, java.lang.String)
	 */
	@Override
	public ScoredDocument scoreDocument(DocEntry doc, String term) {
		// For a given doc and term, we calculate the TfIdf
		return new ScoredDocument(
				doc.getName(),
				doc.getFrec() * 1.0 / termOccurence.get(term)
		);
	}
	
	private void logFreq() {
		for(Entry<String, Long> et : termOccurence.entrySet())
			log.fine(et.getKey() + " " + et.getValue());
	}
}
