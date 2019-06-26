# Invertedindex
Inverted index implementation

# Running instructions
Project can be run either using maven or java.

For maven execution use:
```
   mvn spring-boot:run -Dspring-boot.run.arguments=\
      --monitor.terms=term1,\
      --monitor.terms=term2,\
      --result.print.rate=rate,\
      --num.docs=docs
 ```
 For java execution use:
 ```
    java -jar target\inverted-index-app-0.0.1-SNAPSHOT.jar \
       --monitor.terms=term1,term2 \
       --result.print.rate=rate \
       --num.docs=docs
```       
In both cases, inverted index takes the following arguments:
 - monitor.terms: terms that we use to score the documents. e.j: here,now
 - result.print.rate: time in millis to show results. e.j: 10000
 - num.docs: number of documents to show
    
# Performance
In order to evaluate system performance we should focus in two main operations: indexing documents and retrieving / calculating results.

Indexing document comprises the analysis of a given document in order to extract its terms and their frequency. This operation involves two main tasks: split the document in tokens and count token frequency. In both cases, we can compare the performance of repeatedly search for a given term (white space or term) over a set. Assuming that java is using binary search for that, we can assume that computation cost increases linearly with number of terms. As we are using an asyncrhonous method for document indexing, processing a batch of documents (given that we have enought threads / CPUs) should not dramaticaly multiply computation costs.

Retrieving / calculating results comprises the process of getting relevant documents for a given set of terms, calculate and average for each document and sort the final results. Computational cost of this operation will depend on the number of terms and retrieved documents, as we will introduce now. 

First thing is retrieving documents for a given term. In this case we are using a hashmap to store the documents, so retrieving cost per term should be O(1). However, as we are scoring documents on retrieval, we should need to score every document, so computational cost should be something like O(1) + O(number of documents).

Once we have the documents, we will need to make a sum whith their scores and wight it per number of terms. This will need at least another visit to every element on the list. After that we will just need to sort the list. Assuming that java uses a quicksort algorithm, its performance would be O(n * log n). So final performance should be O(n) + O(n * log n). Again, linear with the number of documents.
