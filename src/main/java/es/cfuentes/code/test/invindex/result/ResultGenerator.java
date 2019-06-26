package es.cfuentes.code.test.invindex.result;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.cfuentes.code.test.invindex.beans.ScoredDocument;
import es.cfuentes.code.test.invindex.ifaces.DocumentIndex;

/*
 * Component that prints results at a fixed rate
 */
@Profile("!test")
@Component
public class ResultGenerator {

	@Value("${monitor.terms}")
	private String[] terms;

	@Value("${num.docs}")
	private int limit;

	@Autowired
	private DocumentIndex ie;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Scheduled(fixedRateString = "${result.print.rate}", initialDelayString = "${result.print.rate}")
	public void printResults() {
		log.info("Results for " + Arrays.toString(terms));
		getResults(terms,limit,ie).stream().forEach(sd -> log.info(sd.getName() + " " + sd.getScore()));
	}

	public static List<ScoredDocument> getResults(String[] terms, int limit, DocumentIndex ie) {
		// We get a map containing the sum of scores per document and term
		ConcurrentMap<String, Double> comMap =
				// We iterate over the terms
				Arrays.stream(terms)
				// Get the list of documents for each term
				.map(term -> ie.getTerm(term))
				// Flat the list in a single stream
				.flatMap(Set::stream)
				// Reduce the new stream
				.collect(
						// We group by document name and sum the scores
						Collectors.groupingByConcurrent(
								ScoredDocument::getName,
								Collectors.summingDouble(ScoredDocument::getScore))
						);
		// We get the final list of scored documents
		List<ScoredDocument> ret =
				// We iterate over compMap
				comMap.entrySet().stream()
				// for each entry, we create a new scored document, 
				// weighting the score sum by the number of terms analyzed
				.map(entry -> new ScoredDocument(entry.getKey(), entry.getValue() / terms.length))
				// we sort the documents by its score
				.sorted(Comparator.comparing(ScoredDocument::getScore).reversed())
				// we limit the number of results
				.limit(limit)
				// we create the output list
				.collect(Collectors.toList());
		return ret;
	}

}
