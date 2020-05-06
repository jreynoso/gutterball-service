# Gutterball! Service

### About
This service handles the backend for the *Gutterball!* app.

### Overview
    .
    ├── db                  # Local document store files
    ├── docs                # Rest API documentation
    ├── gradle              # Bundled gradle wrapper  
    ├── src                 # Source code
    │   ├── main            # Implementation
    |   |   └── resources   # Configuration
    │   └── test            # Tests
    ├── build.gradle        # Gradle config
    └── README.md

### Requirements
- JDK 8+

### Run Tests
```
$ gradle clean check
```

### Run Locally
```
$ gradle bootRun
```

By default, the service will run on port 8080

### API
```
# Create a new game
POST /api/v1/game

# Retrieve a game
GET /api/v1/game/{gameId}

# Add a player to a game
POST /api/v1/game/{gameId}/player

# Start a game
POST /api/v1/game/{gameId}/start

# Bowl
POST /api/v1/game/{gameId}/player/{playerId}/bowl
```
See `docs/rest_api.http` for more examples and documentation
