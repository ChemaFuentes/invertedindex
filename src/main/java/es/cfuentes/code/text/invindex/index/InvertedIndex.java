package es.cfuentes.code.text.invindex.index;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.DocEntry;

@Component
public class InvertedIndex {

	@Autowired
	private TfIdfScorer scorer;

	private Map<String, Set<DocEntry>> ie = new HashMap<String, Set<DocEntry>>();

	private Logger log = Logger.getLogger(this.getClass().getName());

	@Async
	public void indexDocument(File file) throws InterruptedException, ExecutionException {

		// Get term decomposition for the document
		Map<String, Long> terms = scorer.processDocument(file).get();

		updateIndex(terms, file.getAbsolutePath());
	}

	private synchronized void updateIndex(Map<String, Long> terms, String fileName) {
		// Build proper index based on term and document
		for (Entry<String, Long> entry : terms.entrySet()) {
			if (!ie.containsKey(entry.getKey()))
				ie.put(entry.getKey(), new HashSet<DocEntry>());

			ie.get(entry.getKey()).add(new DocEntry(fileName, entry.getValue()));

		}
		printIndex();
	}

	public synchronized Set<DocEntry> getTerm(String term) {
		// Return a copy of DocEntry set, to avoid concurrent modification problems
		Set<DocEntry> ret = new HashSet<DocEntry>();
		ret.addAll(ie.get(term));
		return ret;
	}

	private void printIndex() {
		log.info("===========================================================");
		for (Entry<String, Set<DocEntry>> entry : ie.entrySet()) {
			log.info(entry.getKey());
			for (DocEntry de : entry.getValue()) {
				log.info("\t" + de.getName() + " " + de.getFrec());
			}
		}
		log.info("===========================================================");
	}
}
