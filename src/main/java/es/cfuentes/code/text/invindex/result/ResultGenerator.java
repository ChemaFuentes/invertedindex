package es.cfuentes.code.text.invindex.result;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.comparator.Comparators;

import es.cfuentes.code.text.invindex.beans.ScoredDocument;
import es.cfuentes.code.text.invindex.index.InvertedIndex;

@Component
public class ResultGenerator {
	
	@Value("${monitor.terms}")
	private String[] terms;
	
	@Value("${num.docs}")
	private int limit;
	
	@Autowired
	private InvertedIndex ie;

	@Scheduled(fixedRateString = "${result.print.rate}")
	public void printResults() {
		System.out.println("Imprimo resultados");
		
		//Set<ScoredDocument> combinedSet = new HashSet<ScoredDocument>();
		
		//for(String term : terms)
			//combinedSet.addAll(ie.getTerm(term));

		// make a stream		
		// group by document
		ConcurrentMap<String, Double> comMap = Arrays.stream(terms)
			.map(term -> ie.getTerm(term))
			.flatMap(Set::stream)			
			.collect(Collectors.groupingByConcurrent(ScoredDocument::getName,
				Collectors.summingDouble(ScoredDocument::getScore)));
			
		
		comMap.entrySet().stream()
			.map(entry -> new ScoredDocument(entry.getKey(), entry.getValue()/terms.length))
			.sorted(Comparator.comparing(ScoredDocument::getScore).reversed())
			.limit(limit)
			.forEach(sd -> System.out.println(sd.getName() + " " + sd.getScore()));
	}
}
