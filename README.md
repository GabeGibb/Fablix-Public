VIDEO URL:
https://clipchamp.com/watch/2YUZ8CY5hqK

- # General
    - #### Team: gibbler:

    - #### Names: Gabriel Gibb:

    - #### Project 5 Video Demo Link:
      - COMPLETE LATER

    - #### Instruction of deployment:
      - COMPLETE LATER

    - #### Collaborations and Work Distribution:
      - Solo Project (Gabriel Gibb)
    

- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling
    - CONFIG (IN WEB CONTENT)
      - context.xml
      - web.xml
    - CODE (IN SRC) (Used JDBC and prepared statements)
      - AddMovieServlet.java
      - AddStarServlet.java
      - EmployeeLoginServlet.java
      - LoginServlet.java
      - MainPageServlet.java
      - MoviesServlet.java
      - PayServlet.java
      - SingleMovieServlet.java
      - SingleStarServlet.java
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
      - When connection to SQL a connection is created based on the configuration in context.xml. 
      This configuration defines a connection pool with 100 connections possible, max of 30 idle connections, and a 10000 
      millisecond timeout is set as a maximum timeout.
      - The web.xml file registers database resources as described in context.xml
      - In every servlet that interacts with SQL, a connection is made through a datasource object, a query can be made using a 
      prepared statement, and once the servlet is done the connection can go back to the pool.
      - Also added cache prepared statements in the context.xml url
    - #### Explain how Connection Pooling works with two backend SQL.
    - When connections are pooled, any user might connect to any instance that exists in the pool.
    - For read operations, slave instances can get their own local sql data and send it to the servlets to read.
    - Any write operations are sent to master instances, changing the database of the master instance and then replicating that change
    to all slaves.
    - Connections can stay open and can be borrowed for a short time for use before being sent back to the pool.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    
    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |



PROJECT 4 ANDROID REPO:
https://github.com/GabeGibb/cs122b-project4-android-example


PROJECT 3 STUFF:

Files with prepared statements:
LoginServlet
MoviesServlet
SingleMovieServlet 
SingleStarServlet 
PayServlet


XML Optimization:
In order to optimize, incorporating executing a whole line of insert statements, as opposed to making a connection and separate execute statement
for each single insert would allow a large amount of optimization for lots of insertion statements.
Another optimization would be storing an id value for movies or stars and incrementing it and saving it, as opposed to looking up the highest
id value through a query.
These two optimization can offer significant speed advantages over a naive approach that executes multiple individual querys and execute statements.
Making sure print statements are sparring is also significant in improving speed.
