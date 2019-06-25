package es.cfuentes.code.text.invindex.result;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.cfuentes.code.text.invindex.beans.ScoredDocument;
import es.cfuentes.code.text.invindex.index.InvertedIndex;

@Component
public class ResultGenerator {
	
	@Value("${monitor.terms}")
	private String[] terms;
	
	@Autowired
	private InvertedIndex ie;

	@Scheduled(fixedRateString = "${result.print.rate}")
	public void printResults() {
		System.out.println("Imprimo resultados");
		
		Set<ScoredDocument> combinedSet = new HashSet<ScoredDocument>();
		
		for(String term : terms)
			combinedSet.addAll(ie.getTerm(term));
		
		combinedSet.stream().collect(Collectors.groupingByConcurrent(
				ScoredDocument::getName,
				Collectors.summingDouble(ScoredDocument::getScore));
		// make a stream
		// group by document
		// average
		// sort
		// filter
	}
}
