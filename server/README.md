# Animal Collector Server

### Local dev

1. Setup database:

    ```console
    $ docker run --rm --name postgres-dev -p 5432:5432 -d postgres
    $ docker exec -it postgres-dev su - postgres
    ```
    
    ```
    $ psql
    postgres=# CREATE DATABASE ani_co_dev;
    postgres=# \c ani_co_dev
    postgres=# \dt
    ```

2. For the first time, change the property value of `spring.jpa.hibernate.ddl-auto` to `create` to initialize the tables.

3. Run application:

    ```console
    $ ./gradlew bootRun
    ```

For running tests, create database `ani_co_test` and run with profile `test`.