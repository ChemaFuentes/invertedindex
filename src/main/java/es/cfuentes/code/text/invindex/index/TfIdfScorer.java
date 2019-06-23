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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class TfIdfScorer {

	private Map<String, Long> termOccurence = new HashMap<String, Long>();

	public Map<String, Long> processDocument(File file) {
		Map<String, Long> ret = new HashMap<String, Long>();
		Stream<String> stream = null;
		try {
			stream = Files.lines(Paths.get(file.toURI()));
			ret.putAll(stream.flatMap(Pattern.compile(" ")::splitAsStream)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));

			synchronized (termOccurence) {
				for (String term : ret.keySet()) {
					if (termOccurence.containsKey(term))
						termOccurence.put(term, termOccurence.get(term) + 1);
					else
						termOccurence.put(term, 1L);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null)
				stream.close();
		}
		return ret;
	}
}
