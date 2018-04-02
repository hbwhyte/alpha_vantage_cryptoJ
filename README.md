# Alpha Vantage Crypto Currency Sandbox

Uses Alpha Vantage's 3rd party API connection to collect, analyze, and persist data about cryptocurrencies. 

## Tech Stack
 - Java
 - Spring
 - MyBatis
 - MySQL
 - AWS EC2
 - AWS RDS

## Functionality
 - **searchDigital30()** calls the Alpha Vantage API's Digital Currency Daily function, and returns an ArrayList of
the previous 30 days of daily data for the selected cryptocurrency symbol. It also checks if the search items have 
been added to the database yet, and if not, persists them.
 - **findMax()** finds and returns the highest price of a selected digital currency (in USD) over a certain number of days. Query
params can define which symbol to look for, and how many days to compare against (e.g. find the highest price of 
Ethereum over the past 90 days)
 - Basic CRUD functionality

## More Info
For more information on the Alpha Vantage API, check out https://www.alphavantage.co/documentation/

