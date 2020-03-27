[TOC]

#### Considerations

- API documentation can be found on **index.html** file generated using redoc-cli fetched by the api.json file on a OpenApi (swagger) format
- Its assumed that all products have enought stock and it wont be threated whatsoever
- A product name is unique and validation was added for that
- Its assumed that order where payed separately using another system
- For this version product prices where treated on its own Price table where validTo = null means that its the current price, and price for a given order is calculated using the order date
- For the running application I choose to use a Mysql database, please follow the instructions on how to get it running with Docker
- For Integration tests I opted to use a h2 in memory database so that it runs faster and doesnt do operations on the production database

#### Running Instructions
 - Run database on docker:
 ```bash
 docker run -d -p 6033:3306 --name=docker-mysql --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_PASSWORD=root" --env="MYSQL_DATABASE=api_database" mysql:5.7
 ```
 
 - Run the api:
  ```
  ./mvnw clean spring-boot:run
  ```
Application will run under localhost:8080
 - Run tests:
   ```bash
   ./mvnw clean test
   ```

#### Examples of Post/Get

Post  /products
```json
curl --request POST \
  --url http://localhost:8080/products \
  --header 'content-type: application/json' \
  --data '
  {
    "name": "pipe",
    "description": "wood pipe",
    "price": 10.0
}'
```

Get /products
```bash
curl --request GET \
  --url http://localhost:8080/products
```

Post  /orders
```json
curl --request POST \
  --url http://localhost:8080/orders \
  --header 'content-type: application/json' \
  --data '{
    "buyer": "teste@este.com",
    "entries": [
      {
        "productId": 1,
        "quantity": 10
      }
    ],
    "status": "PLACED"
}'
```

Get /orders
```json
localhost:8080/orders?startDate=02-10-2010 01:00:00&endDate=22-10-2023 00:00:00

curl --request GET \
  --url 'http://localhost:8080/orders?startDate=02-10-2010%2001%3A00%3A00&endDate=22-10-2023%2000%3A00%3A00'
  ```


#### Answer to questions
 - **authenticationauthentication proposal?**
 I recommend using Oauth 2 as the authentication, so that it makes possible to generate a token and reutilize it for the transactions. It would be possible to have Oauth2 with scopes so that we could block certain endpoints eg. place order and post product and have get products open.
 - **How can you make the service redundant? **
 This could be achieved by having multiple instances of the service deployed to different cloud regions eg: us-central, us-east, europe, asia. We could have multiple instances of the service running simultaneously and having a load balancer distribute the traffic to the healthy regions or even having only one region serving traffic and in case of an incident re-route the traffic to a healthy region.
