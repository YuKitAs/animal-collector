# Animal Collector Server

### Local development

1. Setup database:

    * Run Postgres image and create database with default user `postgres`:
        ```console
        $ docker run --name postgres-dev -p 5432:5432 -e POSTGRES_DB=ani_co_dev -d postgres
        ```
    
    * Enter database as user `postgres`:
        ```
        $ docker exec -it postgres-dev su postgres
        $ psql
        postgres=# \c ani_co_dev
        ```

2. Make sure the pre-trained model zip file is placed under `src/main/resources`

3. Run application with profile `ide`:

    ```console
    $ ./gradlew clean bootRun --args='--spring.profiles.active=ide'
    ```
    
    or 
    
    ```console
    $ ./gradlew clean build
    $ java -jar -Dspring.profiles.active=ide build/libs/animal-collector.jar
    ```
    
For initializing the schema as defined in `src/main/resources/schema.sql`, set `spring.jpa.hibernate.ddl-auto` to `none`.

For running tests, create database `ani_co_test` and run application with profile `test`.