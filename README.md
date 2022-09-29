# Library App - Tech Assignment for Hertz

A RESTful library system that is capable of adding new books, removing books 
and allowing members to loan/return a book.
## Technologies Used:
- Java 11
- spring-boot 2.6.4
- spring-boot-starter-web
- Gradle 7.x
- Junit 5
- lombok

### Structure of REST Calls
- ADD Book(s)
<pre>
    <code>
POST RQ http://localhost:8080/library/addBooks
[{
  "title": "Title3",
  "author": "Author1",
  "categories": [
  "POETRY",
  "MYSTERY"
  ]

  }, {
  "title": "Title4",
  "author": "Author2",
  "categories": [
  "THRILLER",
  "SCIENCE_FICTION"
  ]
  }]</code>
</pre>

- REMOVE Book(s) 
<pre>
    <code>DELETE RQ http://localhost:8080/library/removeBooks
[{
"title": "Title5",
"author": "Author1",
"categories": [
"POETRY",
"MYSTERY"
]
}, {
"title": "Title6",
"author": "Author2",
"categories": [
    "THRILLER",
    "SCIENCE_FICTION"
]
}]</code></pre>

- LOAN Book(s)
<pre>
    <code>GET RQ http://localhost:8080/library/loanBooks?name=John&title=Title1&title=Title2
    </code>
</pre>
- Return Book(s)
<pre>
    <code>PUT RQ http://localhost:8080/library/returnBooks?name=John&title=Title3
    </code>
</pre>

## Build the application
This is a standard gradle project that can be built and run by using the gradle wrapper:
- `./gradlew clean build`

To skip the unit and integration tests
- `./gradlew clean build -x test -x integrationTest`

It can also be built and started with the IDE of your choice, providing the required configuration.