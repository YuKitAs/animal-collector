# Animal Collector Server

### Local development

1. Setup database:

    * Run Postgres image and create database with default user `postgres`:
      ```console
      $ docker run --name postgres-dev -p 5432:5432 -e POSTGRES_DB=ani_co_dev -e POSTGRES_PASSWORD=<password> -d postgres
      ```

      `POSTGRES_PASSWORD` is required for the default user, which needs to be configured as `spring.datasource.password`.
    
    * Enter database as user `postgres`:
      ```console
      $ docker exec -it postgres-dev su postgres
      $ psql
      postgres=# \c ani_co_dev
      ```

2. Make sure the pre-trained model zip file is placed under `src/main/resources`

3. Run application with profile `ide`:

    ```console
    $ ./gradlew clean bootRun --args='--spring.profiles.active=ide'
    ```
    
    or (skipping tests)
    
    ```console
    $ ./gradlew clean build [-x test]
    $ java -jar -Dspring.profiles.active=ide build/libs/animal-collector-0.1.0-SNAPSHOT.jar
    ```
    
    or run as docker image:
    
    ```console
    $ docker build -t ani-co-server .
    $ docker run --network="host" ani-co-server
    ```
    
For initializing the schema as defined in `src/main/resources/schema.sql`, set `spring.jpa.hibernate.ddl-auto` to `none`.

For running tests, create database `ani_co_test` and run application with profile `test`.