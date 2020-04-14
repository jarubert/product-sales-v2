
 #### Assignment description
 Application Assignment
Web service
Create a tiny REST / JSON web service in Java using Spring Boot (RestController) with an API that supports basic products CRUD:
- Create a new product
- Get a list of all products
- Update a product
The API should also support:
- Placing an order
- Retrieving all orders within a given time period
A product should have a name and some representation of its price.
Each order should be recorded and have a list of products. It should also have the buyer’s e-mail, and the time the order was placed. The total value of the order should always be calculated, based on the prices of the products in it.
It should be possible to change the product’s price, but this shouldn’t affect the total value of orders which have already been placed.
Requirements
- Implement your solution according to the above specification.
- Provide unit tests.
- Document your REST-API.
- Provide a storage solution for persisting the web service’s state.
- Have a way to run the service with its dependencies (database etc) locally. You can use either a
simple script or docker or something else. It’s up to you.
Submitting
Upload your code to a public git repository (eg. GitHub, GitLab) and send us the link. Considerations
- You do not need to add authentication to your web service, but propose a protocol / method and
justify your choice.*
- How can you make the service redundant? What considerations should you do?*



#### Considerations

- API documentation can be found on [index.html](index.html) (clone/downloand repo and open for a nice view) file generated using redoc-cli fetched by the api.json file on an OpenApi (swagger) format
- Its assumed that all products have enought stock and it wont be threated whatsoever
- Post /products is used for both Insert and Update, if you include a 'id' paramenter it will assume the update operation, I chose to do it this way instead of the PUT for update because thats how most of the APIs I have worked with do it. Also it added some challenge with having different cases when validating for required fields.
- A product name is unique and validation was added for that
- Its assumed that order were payed separately using another system
- For this version product prices where treated on its own Price table where validTo = null means that its the current price, and price for a given order is calculated using the order date
- For the running application I chose to use a Mysql database, please follow the instructions on how to get it running with Docker
- For Integration tests I opted to use a h2 in memory database so that it runs faster and doesnt do operations on the production database, also this means that you dont need a mysql running for testing

#### Running Instructions
 - Run database on docker (note that the command has a horizontal scroll):
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
 - **How can you make the service redundant?**
 
 This could be achieved by having multiple instances of the service deployed to different cloud regions eg: us-central, us-east, europe, asia. We could have multiple instances of the service running simultaneously and having a load balancer distribute the traffic to the healthy regions or even having only one region serving traffic and in case of an incident re-route the traffic to a healthy region.
 
 
