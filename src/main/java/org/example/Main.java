package org.example;

import java.sql.*;
import java.util.UUID;


public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306"; // строка подключения
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) { // создаём соединение
            createSchema(connection);  // создаём БД в соединении
            addTableGroup(connection);
            addGroups(connection);
            addTable(connection);
            addStudents(connection);
            searchStudents(connection);
            searchStudentsByGroup(connection, "1");
        } catch (SQLException e) {
            System.err.println("Соединение не удалось " + e.getMessage());
        }

    }

    private static void createSchema(Connection connection){
        try (Statement statement = connection.createStatement()){
            statement.execute("DROP SCHEMA GB_students;");
            boolean res = statement.execute("CREATE SCHEMA GB_students;");
            System.out.println(res);

            // Statement - интерфейс, описывающий конкретный запрос в БД
            // execute() - принимает sql-запрос, возвр. boolean (true - изменения применились, в противном случае false)

        } catch (SQLException e) {
            System.err.println("База данных не создана" + e);
        }
    }

    static void addTable(Connection connection) throws SQLException{
        // Statement - интерфейс, описывающий конкретный запрос в БД
        try(Statement statement = connection.createStatement()) {
            // execute() - принимает sql-запрос, возвр. boolean (true - изменения применились, в противном случае false)
            statement.execute("""
                    create table GB_students.students(
                        `id` varchar(36) PRIMARY KEY,
                        `first_name` varchar(256),
                        `second_name` varchar(256),
                        `group` varchar(36),
                         FOREIGN KEY (`group`) REFERENCES `groups` (id))
                    """);
        }
    }

    static String getIdGroup(Connection connection, String group_n) throws SQLException{
        String id = null;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select id from `GB_students`.`groups`
                    WHERE `group_name` = '""" + group_n + "';");

            if (resultSet.next()){
                id =  resultSet.getString("id");
            }

        }
        return id;
    }





    static void addStudents(Connection connection) throws SQLException{
        try(Statement statement = connection.createStatement()){
            // .executeUpdate() количество строк, на которые повлиял запрос
            statement.execute("INSERT INTO `GB_students`.`students`(`id`,`first_name`, `second_name`,`group`) VALUES" +
                "(UUID(), 'Victoria', 'Soloveva','" + getIdGroup(connection,"group1")+ "')," +
                "(UUID(), 'Vladimir', 'Solovev','" + getIdGroup(connection,"group1")+ "')," +
                "(UUID(), 'David', 'Solovev','" + getIdGroup(connection,"group1")+ "')," +
                "(UUID(), 'Mia', 'Boyka','" + getIdGroup(connection,"group1")+ "');");

    }
    }

    static void searchStudents(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select id, first_name, second_name, `group`
                    from GB_students.students
                    """);
            System.out.println("Все строки БД: ");
            while (resultSet.next()) {
                UUID id =  UUID.fromString(resultSet.getString("id"));
                String first_name = resultSet.getString("first_name");
                String second_name = resultSet.getString("second_name");
                String group = resultSet.getString("group");
                System.out.println("Прочитана строка: " + String.format("%s, %s, %s", id, first_name, second_name, group));
            }

        }
    }

    static void searchStudentsByGroup(Connection connection, String groupName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                    select id, first_name, second_name, `group`
                    from GB_students.students WHERE `group` = """ + groupName + ";");
            System.out.println("Студенты группы: " + groupName);
            while (resultSet.next()) {
                UUID id =  UUID.fromString(resultSet.getString("id"));
                String first_name = resultSet.getString("first_name");
                String second_name = resultSet.getString("second_name");
                String group = resultSet.getString("group");
                System.out.println("Прочитана строка: " + String.format("%s, %s, %s", id, first_name, second_name, group));
            }

        }
    }
    static void addTableGroup(Connection connection) throws SQLException{
        // Statement - интерфейс, описывающий конкретный запрос в БД
        try(Statement statement = connection.createStatement()) {
            // execute() - принимает sql-запрос, возвр. boolean (true - изменения применились, в противном случае false)
            statement.execute("""
                    create table GB_students.groups(
                        `id` varchar(36) PRIMARY KEY,
                        `group_name` varchar(256)
                    )
                    """);
        }
    }

    static void addGroups(Connection connection) throws SQLException{
        try(Statement statement = connection.createStatement()){
            // .executeUpdate() количество строк, на которые повлиял запрос
            int count = statement.executeUpdate("""
                insert into GB_students.groups(id, group_name) values
                 (UUID(), 'group1'),
                 (UUID(), 'group2')
                """);
            System.out.println("Количество вставленных строк: " + count);
        }}


}
