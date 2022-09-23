# Description

Please read the [description of the coding task here.](task-description.pdf)

# Solution overview

The service exposes two endpoints:
 * to store new url with shortcut reference
 * to get original url by shortcut reference

Assuming, that we might need to scale, architecture was adjusted with caching(Redis) of reads, 
and writes could be scaled by scaling of NoSQL db, which is MongoDB in this case.
The solution is dockerized, so could be easily deployed in multiple server nodes in case we need to scale.
Current configuration of MongoDB and Redis is very basic and almost default, 
but corresponding application properties could be easily adjusted with a specific environment configurations.
For hashing of url  **murmur3_32** algorithm was chosen because of simplicity and fixed length of hashed value.

[Local git history](.git) might help to see the development process.

## How to test

Please execute 

`mvn clean test`

## How to run
To run the solution please execute next command

`docker-compose up --build`

and to stop

`docker-compose down`

## Try out endpoints

To create url shortcut, please execute next endpoint:  

```
curl -X POST --location "http://localhost:8080/shorten" \
    -H "Content-Type: application/json" \
    -d "\"{here put your url, e.g. http://localhost:8080/shorten}\""
```

Example of successful response

```
{
  "originalUrl": "http://localhost:8080/shorten",
  "shortenedUrl": "4c329997"
}
```

To get original url by shortcut, please execute:

`curl -X GET --location "http://localhost:8080/{your shortcut for url, e.g. 4c329997}`

Example of successful response 

```
{
"originalUrl": "http://localhost:8080/shorten",
"shortenedUrl": "4c329997"
}
```

### aka P.S.

There are some points to improve in this application, e.g. to add logging, make hashing configurable etc. 
Since it is always possible to improve a solution, hopefully current implementation satisfies production ready MVP in terms of home task.