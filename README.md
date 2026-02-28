# Absolute Cinema (Reviews)

An EECS 4314 project.

## Note: "This project uses the TMDB API but is not endorsed or certified by TMDB."

### TMDB website: https://www.themoviedb.org/
![](https://www.themoviedb.org/assets/2/v4/logos/v2/blue_square_2-d537fb228cf3ded904ef09b136fe3fec72548ebc1fea3fbbd1ad9e36364db38b.svg)

## Running Movie Service (For Developers)

### Requirements
- Java 21
- TMDB API token

### Clone the repo then follow the steps for either using an IDE or Terminal.

### Steps (IDE)
1. Set your API token as an environment variable called TDMB_TOKEN in the IDE, terminal, or if in Windows, the Edit Environment Variables window.

2. Run the MovieServiceApplication.java found in the movie-service folder as a spring boot application.

3. Go to ```http://localhost:8080/movie/{id}``` in your browser, where {id} is a valid movie id from TMDB.

4. See the results.

### Steps (Terminal)
1. Set your API token as an environment variable called TDMB_TOKEN in the terminal, or if in Windows, the Edit Environment Variables window.

2. Open the terminal in the repository directory.

3. Change directory
    ```
    cd movie-service
    ```

4. Run  
    ### Windows
    ```./mvnw.cmd spring-boot:run```

    ### Mac/Linux
    ```./mvnw spring-boot:run```

5. Go to ```http://localhost:8080/movie/{id}``` in your browser, where {id} is a valid movie id from TMDB.

6. See the results.