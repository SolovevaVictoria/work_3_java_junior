package org.example;

import java.sql.*;

public class JDBC {
    public static void main(String[] args) {
        // JDBA = Java Database Connectivity
        // Driver - интерфейс, позволяющий подключаться к конкретной БД
        // У каждой базы свой драйвер, например, PostgresDriver, OracleDriver, MySQLDriver, ...
        // h2 - база данных, которая создаётся в оперативной памяти и удаляется после завершения работы приложения
        // DriverManager - класс, который управляет драйверами
        // Connection - интерфейс, описывающий соединение с базой данных

        // строка подключения: "jdbc:h2:mem:testdb", "root", "root"
        // (<jdbc>, <h2> - база данных, <mem> - значит находится в оперативной памяти, testdb - название, root - логин, root - пароль)
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "root", "root");) {
            acceptConnection(connection);
        } catch (SQLException e) {
            System.out.println("Не удалось подключиться к БД: " + e.getMessage());

        }

    }


    // представим, что есть некий запрос, который приходит в наш сервер,попадает в метод
    // id - параметр, который прислал фронтенд
//    static void removePersonById(Connection connection, String idParametr) throws SQLException {
//        try (Statement statement = connection.createStatement()) {
//            int deletedRowsCount = statement.executeUpdate("delete from person where id = " + idParametr);
//            System.out.println("Удалено строк: " + deletedRowsCount);
//        }
//
//    }
        static void removePersonById(Connection connection, String idParametr) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from person where id = ?");
        preparedStatement.setLong(1, Integer.parseInt(idParametr));

        int deleteRowsCount = preparedStatement.executeUpdate();
        System.out.println("Удалено строк: " + deleteRowsCount);
        preparedStatement.close();

    }




//    static interface StatementExecutor {
//        void execute(Statement statement) throws SQLException;
//    }
//    static void executeStatement(Connection connection, StatementExecutor statementExecutor) {
//        try (Statement statement = connection.createStatement()) {
//            statementExecutor.execute(statement);
//        }
//        catch (SQLException e){
//            System.out.println("Ошибка при выполнении statement: " + e.getMessage());
//        }
//
//    }

    static void acceptConnection(Connection connection) throws SQLException{
        // Statement - интерфейс, описывающий конкретный запрос в БД
        try(Statement statement = connection.createStatement()) {
            // execute() - принимает sql-запрос, возвр. boolean (true - изменения применились, в противном случае false)
            statement.execute("""
                    create table person(
                        id bigint,
                        name varchar(256),
                        age smallint
                    )
                    """);
        }

    try(Statement statement = connection.createStatement()){
        // .executeUpdate() количество строк, на которые повлиял запрос
        int count = statement.executeUpdate("""
                insert into person(id, name, age) values
                 (1, 'Victoria', 29),
                (2, 'Vladimir', 41),
                (3, 'David', 1),
                (4, 'Mia', 1)
                """);
        System.out.println("Количество вставленных строк: " + count);
    }


        try(Statement statement = connection.createStatement()){
            // .executeUpdate() количество строк, на которые повлиял запрос

            int count = statement.executeUpdate("""
                update person
                set age = -1
                where id > 3
                """);
            System.out.println("Количество обноввленных строк: " + count);
        }

        // executeQuery возвращает интерфейс ResultSet (итератор), имеющий методы:
        // next() -> boolean указывает на строку с номером, равным номеру вызова
        // и возвращает true / false в зависимости от того, есть ли там что-то
        // (указатель)
        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
                select id, name, age
                from person
                """);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println("Прочитана строка: " + String.format("%s, %s, %s", id, name, age));
            }

        }


        try(Statement statement = connection.createStatement()) {
            removePersonById(connection, "4");
            // sqlInjection
            //  removePersonById(connection, "1 or 1 = 1"); прибегают злоумышленники для удаления всего из БД/прочитать ...
        }


        try (Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("""
                select id, name, age
                from person
                """);
            System.out.println("Записи после удаления ");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.println("Прочитана строка: " + String.format("%s, %s, %s", id, name, age));
            }

        }

    }
}
