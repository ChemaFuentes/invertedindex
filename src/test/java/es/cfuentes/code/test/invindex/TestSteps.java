package es.cfuentes.code.test.invindex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import es.cfuentes.code.test.invindex.beans.ScoredDocument;
import es.cfuentes.code.test.invindex.result.ResultGenerator;

public class TestSteps extends SpringTestConfig {

	private List<ScoredDocument> result;

	@Given("a default set of documents and application.properties")
	public void a_default_set_of_documents_and_application_properties() {
		assertNotNull(getDocumentIndex());
		assertNotNull(getFolder());
	}

	@When("we process the documents")
	public void we_process_the_documents() throws InterruptedException, ExecutionException {
		// We read the monitor file
		File file = new File(getFolder());

		// Check that it is a valid directory
		assertTrue(file.isDirectory());

		// Index test files
		for (File indexFile : file.listFiles()) 		
			getDocumentIndex().indexDocument(indexFile);
		
		// Wait till documents are indexed
		Thread.sleep(5000);
		
		// Get results for given terms
		result = ResultGenerator.getResults(getTerms(), getLimit(), getDocumentIndex());
		
		assertNotNull(result);
	}

	@Then("we get {string} and {string}")
	public void we_get_and(String document1, String document2) {
		
		// Check that we have two results
		assertEquals(2, result.size());
		
		// Check document 1
		assertEquals(new File(document1).getAbsolutePath(), result.get(0).getName());
		// Check document 2
		assertEquals(new File(document2).getAbsolutePath(), result.get(1).getName());
		
	}

	@Then("they have scores {double} and {double}")
	public void they_have_scores_score_and_score(Double score1, Double score2) {

		// Check score 1
		assertEquals(score1, result.get(0).getScore());
		
		// Check score 2
		assertEquals(score2, result.get(1).getScore());
	}

}
