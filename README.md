# Journey API

This little service implements the details described in the assignment.
As the details of the Journey data object was not important to the task at hand, I kept it as slim as possible.

## Cache
I chose to implement and test both an 'in memory'-cache and a Redis cache to challenge myself, and learn more about Redis.
To run the Redis tests one should run Docker as testcontainers spin up a container with Redis using docker.


## Admin privileges
As it wasn't described in the assignment, I didn't do anything in particular to ensure that admin endpoints were only accessed by admin users.
I assumed the API gateway handles the security aspect, and focused on the cache part of the assignment. 


## Running the application

The default settings are set to use Redis as cache. 
By spinning up a Redis container you can test it using curl, using the commands described below. 

### Start Redis with Docker

``` 
> docker run --name journey-redis -p 6379:6379 -d redis:7
```

### Test the API with curl

#### 1. Create a journey
```
curl -v -X POST \
  -H "api-user-id: 1" \
  http://localhost:8080/app/journey
```

#### 2. Fetch a journey
```
curl -v \
  -H "api-user-id: 1" \
  http://localhost:8080/app/journey/1
```

#### 3. Fetch all journeys for a user
```
curl -v \
  -H "api-user-id: 1" \
  http://localhost:8080/app/journey
```

#### 4. Fetch a journey  as admin
```
  curl -v http://localhost:8080/admin/journey/1
```

#### 5. Fetch all journeys for a user as admin
```
  curl -v http://localhost:8080/admin/user/1/journeys
```

#### 5. Delete a journey
```
  curl -v -X DELETE \
  -H "api-user-id: 1" \
  http://localhost:8080/app/journey/1
```
