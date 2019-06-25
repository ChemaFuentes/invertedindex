package es.cfuentes.code.text.invindex.result;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.ScoredDocument;
import es.cfuentes.code.text.invindex.ifaces.DocumentIndex;

/*
 * Component that prints results at a fixed rate
 */
@Component
public class ResultGenerator {

	@Value("${monitor.terms}")
	private String[] terms;

	@Value("${num.docs}")
	private int limit;

	@Autowired
	private DocumentIndex ie;

	@Scheduled(fixedRateString = "${result.print.rate}")
	public void printResults() {
		System.out.println("Results for " + Arrays.toString(terms));
		getResults().stream().forEach(sd -> System.out.println(sd.getName() + " " + sd.getScore()));
	}

	List<ScoredDocument> getResults() {
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
