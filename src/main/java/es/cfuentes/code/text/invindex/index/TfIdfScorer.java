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

@Component
public class TfIdfScorer {

	private Logger log = Logger.getLogger(this.getClass().toString());
	
	private Map<String, Long> termOccurence = new HashMap<String, Long>();
	
	@Async
	public Future<Map<String, Long>> processDocument(File file) {
		Map<String, Long> ret = new HashMap<String, Long>();
		Stream<String> stream = null;
		try {
			stream = Files.lines(Paths.get(file.toURI()));
			
			ret.putAll(stream.flatMap(Pattern.compile(" ")::splitAsStream)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));

			updateStats(ret);
			
		} catch (IOException e) {
			log.severe("Error while processing file: " + e);
		} finally {
			if (stream != null)
				stream.close();
		}
		
		
		return new AsyncResult<Map<String, Long>>(ret);
	}
	
	private synchronized void updateStats(Map<String, Long> stats) {		
			for (String term : stats.keySet()) {
				if (termOccurence.containsKey(term))
					termOccurence.put(term, termOccurence.get(term) + 1);
				else
					termOccurence.put(term, 1L);
			}
			printFrec();
	}
	
	public float scoreDocument(DocEntry doc, String term) {
		return doc.getFrec() / termOccurence.get(term);
	}
	
	private void printFrec() {
		log.info("===========================================================");
		for(Entry<String, Long> et : termOccurence.entrySet())
			log.info(et.getKey() + " " + et.getValue());
		log.info("===========================================================");
	}
}
