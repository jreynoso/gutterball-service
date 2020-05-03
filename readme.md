# Gutterball! Service

### About
This service handles the backend for the *Gutterball!* app.

### Background
This coding challenge is designed to take between 4-8 hours to complete.

The challenge is to implement a bowling scoring REST API (and optional front end). Basically, something that takes in pins knocked down for players and keeps track of them and calculates and provides their scores. If you were in a bowling alley and looked at the display, it would be the service that powered the numbers behind the display.

For this, you don’t have to implement a ton of bells and whistles. Instead, we’re looking for production-quality code that’s well-tested and well-documented. The scoring part of it is the main thing we’re looking for and how you model the data, etc. It would be best to show your usage of a REST framework of sorts and you can use any modern language that you’d like. Additionally, the solution should score as the game progresses rather than just at the end.

Here’s some information on bowling scoring for reference:
http://bowling.about.com/od/rulesofthegame/a/bowlingscoring.htm

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
# Setup
POST /game
GET  /game/{id}
POST /game/{id}/player
GET  /game/{id}/player/{id}

# Start
POST /game/{id}/start

# Play
POST /game/{id}/player/{id}/roll
```

### Roadmap
- implement bowling game
- add error handling
- refactor to service
- add persistence
- add logging
- separate configuration for integration tests
