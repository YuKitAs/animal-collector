# Animal Collector Server

### Local dev

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

2. Run application:

    ```console
    $ ./gradlew bootRun
    ```
    
    For initializing the schema as defined in `src/main/resources/schema.sql`, set `spring.jpa.hibernate.ddl-auto` to `none`.

For running tests, create database `ani_co_test` and run application with profile `test`.