//package com.raft.server.database.database.new_db;
//
//
//import com.raft.server.context.Context;
//import com.raft.server.database.database.new_db.sql_processor.Starter;
//
//public class Main {
//    public static void main(String[] args) throws Exception {
//        Database database = new InMemoryDatabase();
//        Starter starter = new Starter(database);
//        starter.execute("CREATE table iHateDatabase");
//        Table table = database.getTable("iHateDatabase");
//
//        // Работа с записью
//        starter.execute("Insert into iHateDatabase VALUES (name, Alice), (age, 30)");
//        starter.execute("Insert into iHateDatabase VALUES (name, Bob), (age, 25)");
//
//        starter.execute("Select name FROM iHateDatabase Where name=Bob age>24");
//        starter.execute("Delete FROM iHateDatabase Where name=Bob age>24");
//        System.out.println("-".repeat(100));
//        table.createIndex("name");
//
//        System.out.println("Результаты запроса:");
//        starter.execute("Select * from iHateDatabase");
//        System.out.println("-".repeat(100));
//
//        starter.execute("Select age FROM iHateDatabase Where name=Alice");
//        System.out.println("Сумма возрастов: " + table.sumField("age"));
//        System.out.println("Средний возраст: " + table.averageField("age"));
//
//        starter.execute("Insert into iHateDatabase VALUES (name, Charlie), (age, 29)");
//        starter.execute("Insert into iHateDatabase VALUES (name, Alex), (age, 25)");
//
//        System.out.println("После транзакции:");
//        System.out.println("Сумма возрастов: " + table.sumField("age"));
//        System.out.println("Средний возраст: " + table.averageField("age"));
//        starter.execute("Select * FROM iHateDatabase");
//
//        System.out.println("/".repeat(100));
//        starter.execute("Update iHateDatabase set (name, Alex), (age, 25) where name=Bob");
//        starter.execute("Select * FROM iHateDatabase");
//        System.out.println("/".repeat(100));
//        starter.execute("CREATE table iHateDatabase2");
//        System.out.println(database.getAllTables());
//        starter.execute("Delete table iHateDatabase");
//        System.out.println(database.getAllTables().toString());
//    }
//}