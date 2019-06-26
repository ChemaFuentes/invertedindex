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
 - numb.docs: number of documents to show
    
# Performance
