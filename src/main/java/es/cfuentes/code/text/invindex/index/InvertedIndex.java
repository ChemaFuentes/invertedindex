package es.cfuentes.code.text.invindex.index;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.DocEntry;

@Component
public class InvertedIndex {

	private TfIdfScorer scorer;
	
	private Map<String, Set<DocEntry>> ie = new HashMap<String, Set<DocEntry>>();

	@Async
	public void indexDocument(File file) {
		// We synchronized on ie variable
		synchronized (ie) {
			// Get term decomposition for the document
			Map<String, Long> terms = scorer.processDocument(file);
			
			// Build proper index based on term and document
			for (Entry<String, Long> entry : terms.entrySet()) {
				if (!ie.containsKey(entry.getKey()))
					ie.put(entry.getKey(), new HashSet<DocEntry>());

				ie.get(entry.getKey()).add(new DocEntry(file.getAbsolutePath(), entry.getValue()));

			}
		}
	}

	public Set<DocEntry> getTerm(String term) {
		// We synchronized on ie variable
		synchronized(ie) {
			// Return a copy of DocEntry set, to avoid concurrent modification problems
			Set <DocEntry> ret = new HashSet<DocEntry>();
			ret.addAll(ie.get(term));
			return ret;
		}
	}
}
