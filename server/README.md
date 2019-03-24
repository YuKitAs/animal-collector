# Animal Collector Server

### Local dev

1. Setup database:

    1.1 Run Postgres image and enter container with default user `postgres`:
    ```console
    $ docker run --name postgres-dev -p 5432:5432 -d postgres
    $ docker exec -it postgres-dev su - postgres
    ```
    
    1.2 In container, create database `ani_co_dev`:
    ```
    $ psql
    postgres=# CREATE DATABASE ani_co_dev;
    ```
    

2. Set the property value of `spring.jpa.hibernate.ddl-auto` to `create` in order to initialize the tables.

3. Run application:

    ```console
    $ ./gradlew bootRun
    ```

For running tests, create database `ani_co_test` and run application with profile `test`.