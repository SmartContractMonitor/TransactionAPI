# TransactionAPI

An API for getting the information about smart contract method calls.

## Build
`gradle run`

## Usage
REST API request format:
**methods** - get most called smart contract methods for a certain day.

  `http://<ADDRESS>//methods?num=<NUMBER>&date=<DATE>`
  * ADDRESS - your server address
  * NUMBER - number of top results to get
  * DATE - day in YYYY-MM-DD format

Example: `http://127.0.0.1:8080//methods?num=5&date=2018-08-09` gets top 5 most called methods for August 9, 2018.

Running the server:
* Change MongoDB (and/or server) address and port in `src/resources/application.properties`, then run
