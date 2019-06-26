Feature: Inverted index implementation
	Starting from a controled set of documents, check that we can process them and get proper scores for specific terms

Scenario: Document score check
 	Given a default set of documents and application.properties
 	When we process the documents
 	Then we get "documents\FileTest6.txt" and "documents\FileTest1.txt"
 	And they have scores 0.625 and 0.375
