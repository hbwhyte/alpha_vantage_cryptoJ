# Alpha Vantage Cryptocurrency Sandbox

Uses Alpha Vantage's 3rd party API connection to collect, analyze, and persist data about cryptocurrencies. 

## Tech Stack
 - Java
 - Spring Boot
 - Spring Security + JWT User Authentication
 - MyBatis & Hibernate
 - MySQL
 - AWS EC2 & RDS

## Functionality
 - **searchDigital30()** calls the Alpha Vantage API's Digital Currency Daily function, and returns an ArrayList of
the previous 30 days of daily data for the selected cryptocurrency symbol. It also checks if the search items have 
been added to the database yet, and if not, persists them.
 - **findMax()** finds and returns the highest price of a selected digital currency (in USD) over a certain number of days. Query
params can define which symbol to look for, and how many days to compare against (e.g. find the highest price of 
Ethereum over the past 90 days)
 - Basic CRUD functionality
 - User registers with email and password at users/registration to get a 7 day JWT. 

## Get Authorization

The API is protected by 7 day JWT, so you first need to register:

#### curl Registration:
```
curl -i -H "Content-Type: application/json" -X POST -d '{ "email": "{your_email}", "password": "{your_password}" }' http://localhost:8080/users/registration
```

#### curl Login (generates JWT)
```
curl -i -H "Content-Type: application/json" -X POST -d '{ "email": "{your_email}", "password": "{your_password}" }' http://localhost:8080/login

```
Please make note of your bearer token, so you can access the `/digitaldaily/` endpoints.

## Cryptocurrency Key Endpoints

#### Daily 30 Search

GET request that searches for the daily market data for the previous 30 days
for the entered symbol. If no symbol is entered as a search parameter, the 
default search is for ETH (Ethereum). 

```
/digitaldaily/search?symbol={SYMBOL}
```

#### Find Max 
GET request that finds the max value of a digital currency over the past user defined number of days.
The default symbol is BTC, and the default number of days is 30. 

```
/digitaldaily/max?symbol={SYMBOL}&days={int DAYS}
```

#### Persist to Database

POST request that searches every Alpha Vantage crypto symbol by enum, asynchronously, and 
persists any non-duplicate ata to the database.
```
/digitaldaily/persistall
```

#### Clear the Cache
DELETE request that clears the cache of the database
```
/digitaldaily/cache
```

## More Info
For more information on the Alpha Vantage API, check out https://www.alphavantage.co/documentation/

