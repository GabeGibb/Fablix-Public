VIDEO URL:
https://docs.google.com/document/d/1iMbMX81w6zsiVN6KwBFXnymgiwuTtRmIrKa7c5Mn3Ws/edit?usp=sharing

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
