# BookStore
BookStore is a scala based e-commerce application.
## Supported Features
  - User login
  - Add address support for users
  - Add and get books
  - Add and get books from WishList
  - Add and get books from Cart
  - Place Order
  - Get Order history
 ## Installation
  - either clone the repo or download as zip
  - open with IntelliJ as it's a simple IDEA project
 ## To run
  to run this application head over to **Main.scala** and execute it.
  
  or head over to sbt console of the project and execute below command:
  > sbt ~run
    
  use **Postman** or any other api gateways to observe the working.
 ## To run tests
  head over to sbt console of the project and execute:
  > sbt clean
  
  > sbt coverage
  
  > sbt test
  
  for coverage report:
  > sbt coverageReport
## External Dependencies

#### Akka Essentials
- Akka Actors 2.5.32
- Akka Stream 2.5.32
- Akka HTTP 10.2.2

#### MongoDB Dependencies for DB Operations
- Mongo Scala Driver 2.9.0
- Alpakka MongoDB 2.0.2

#### Spray JSON Dependency for implicit data type conversions
- Akka HTTP Spray JSON 10.2.2

#### JWT dependency for token generation
- Authentikat JWT 0.4.5
- Nimbus Jose JWT 9.3

#### Scala Test Dependency
- Scala Test 3.2.2

#### SMTP dependency for sending mails
- Daddykotex Courier 3.0.0-M2

#### Scala Logging
- Scala Logging 3.9.2
- Logback Classic 1.2.3
- Logback Core 1.2.3

#### Encryption Library
- Scalacrypt 0.5-SNAPSHOT

### Plugins Applied
- SBT CPD 2.0.0
- Scala Style 1.0.0
- SBT Coverage 1.6.1
