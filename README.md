# RSO: Customers microservice

## Prerequisites

```bash
docker run -d --name customers -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customers -p 5432:5432 postgres:10.5
```
## Travis status
[![Build Status](https://travis-ci.org/CarRentalNS/customers.svg?branch=master)](https://travis-ci.org/CarRentalNS/customers)