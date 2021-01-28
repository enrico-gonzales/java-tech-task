# Notes from Enrico

* sorting of recipes returned when requested by date is:
  1. recipes with no ingredient past its "best before" date first - sorted by title
  2. recipes with at least one ingredient past its "best before" date second. Among these: 
    recipes with the oldest "best before" date first, then by title
* in memory h2 to unit-test repositories
* criteria queries have been replaced with JPQL queries - which I find to be way more readable
* introduced spring data

### New endpoints:

* GET _/recipe/{title}_ to fetch a recipe by exact title (case sensitive)
* GET _/recipe?excludedIngredient=_ to fetch a list of recipes not including any of the given ingredients by name 
  (_excludedIngredient_ accepts multiple values, e.g.: _/recipe?excludedIngredient=Milk&excludedIngredient=Bread_)
  
### Assumptions: 

* the "ingredient" table was defined to allow for null dates; these will be considered as "always-valid" 
  (ingredients will never be considered past their "best before" or "use by" date if the relevant fields are set to null)
  
### Considerations on further refinements required in a real and complex scenario
* domain objects / entities should be mapped to DTOs at least before leaving the controller layer, 
even though in the current context DTOs would most likely be merely mirrors of the relevant entities. 
* there's no catering for API versioning
* no pagination in case of large datasets
* no API documentation (i.e. Swagger)
* secrets in plain text, inline (db uid/pwd in application.properties) - should be handled differently (e.g. AWS Parameter Store)

### Notes on timezones!

MySQL and the spring-boot runtime must share the same timezone configuration, since I apparently ran into LocalDate zone-sensitiveness.
For dev/test purposes I tweaked the provided docker-compose.yml. Out-of-the-box, in facts, the container runs in UTC
while my local dev environment is on Australia/Sydney. 
Differences in timezones were potentially leading to inaccurate results when querying for recipes by date.


# Lunch Microservice

The service provides an endpoint that will determine, from a set of recipes, what I can have for lunch at a given date, based on my fridge ingredient's expiry date, so that I can quickly decide what I’ll be having to eat, and the ingredients required to prepare the meal.

## Prerequisites

* [Java 11 Runtime](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [Docker](https://docs.docker.com/get-docker/) & [Docker-Compose](https://docs.docker.com/compose/install/)

*Note: Docker is used for the local MySQL database instance, feel free to use your own instance or any other SQL database and insert data from lunch-data.sql script* 


### Run

1. Start database:

    ```
    docker-compose up -d
    ```
   
2. Add test data from  `sql/lunch-data.sql` to the database. Here's a helper script if you prefer:


    ```
    CONTAINER_ID=$(docker inspect --format="{{.Id}}" lunch-db)
    ```
    
    ```
    docker cp sql/lunch-data.sql $CONTAINER_ID:/lunch-data.sql
    ```
    
    ```
    docker exec $CONTAINER_ID /bin/sh -c 'mysql -u root -prezdytechtask lunch </lunch-data.sql'
    ```
    
3. Run Springboot LunchApplication
