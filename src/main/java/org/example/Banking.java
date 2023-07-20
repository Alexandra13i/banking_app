package org.example;

import java.sql.*;
import java.util.Scanner;
import java.util.Date;

public class Banking {
    public static String jdbcURL = "jdbc:postgresql://localhost:5432/banking";
    public static String username = "vladelec";
    public static String password = "alexandra1308";
    public static int pin=0;
    public static long card_number=0L;
    public static long card=0L;
    public static boolean block=false;

    public static void back() throws SQLException {
        System.out.println("Чтобы вернуться, нажмите 1");
        Scanner scanner5= new Scanner(System.in);
        int back= scanner5.nextInt();
        innerMenu();
    }

    public static void innerMenu() throws SQLException {
        System.out.println("0.Выйти \n1.Запросить баланс\n2.Внести средства\n3.Сделать перевод\n4.Закрыть аккаунт\n5.Вернуться в главное меню\n6.Посмотреть историю операций");
        Scanner scanner=new Scanner(System.in);
        int number= scanner.nextInt();
        long balance=0;
        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            String sql = "SELECT balance FROM banking WHERE pin_code=" + pin;
            Statement statement = connection.createStatement();
            ResultSet result= statement.executeQuery(sql);
            while (result.next()){
                balance=result.getLong("balance");
            }} catch (SQLException e) {
            throw new RuntimeException(e);
        }
        switch (number){
            case 0:
                System.out.println("Выход выполнен");
                break;
            case 1:
                System.out.println("Баланс карты: "+balance);
                back();
                break;
            case 2:
                System.out.println("Внесите средства");
                Scanner scanner1= new Scanner(System.in);
            long income=scanner1.nextLong();
            while (income%50!=0 || income<50 || income>1000000){
                System.out.println("Некорректная сумма");
                income=scanner1.nextInt();
            }
            balance+=income;
                Connection connection= DriverManager.getConnection(jdbcURL, username, password);
                String sql = "UPDATE banking SET balance="+balance+"WHERE pin_code="+pin;
                Statement statement= connection.createStatement();
                int rows=statement.executeUpdate(sql);
                Date date = new Date();
                if (rows>0){
                    System.out.println("Деньги внесены\nБаланс карты: "+balance);
                    System.out.println(date.toString());
                }
                String sql5 = "INSERT INTO deposits (card_id, deposit, operation_time)"+ "VALUES ("+ card_number+",'+"+ income+"','"+date+"')";
                Statement statement5= connection.createStatement();
                int rows5=statement5.executeUpdate(sql5);
                if (rows5>0){
                    back();
                }
                break;
            case 3:
                System.out.println("Введите номер счета для перевода");
                Scanner scanner2= new Scanner(System.in);
                long card1 =scanner2.nextLong();
                connection = DriverManager.getConnection(jdbcURL, username, password);
                String sql11 = "SELECT * FROM banking WHERE card_id=" + card1;
                Statement statement11 = connection.createStatement();
                ResultSet result11= statement11.executeQuery(sql11);
                long balance1=0L;
                while (result11.next()){
                    card= result11.getLong("card_id");
                   balance1 =result11.getLong("balance");
                   block = result11.getBoolean("is_block");
                }
                if (block!=false || card!=card1){
                    System.out.println("Счета не существует");
                    back();
                    break;
                    }
                System.out.println("Введите сумму для перевода");
                Scanner scanner3= new Scanner(System.in);
                int transfer =scanner3.nextInt();
                while (transfer>balance){
                    System.out.println("Недостаточно средств на счете");
                    transfer=scanner3.nextInt();
                }
                balance-=transfer;
                balance1+=transfer;
                String sql2 = "UPDATE banking SET balance="+balance+"WHERE pin_code="+pin+";"+"UPDATE banking SET balance="+balance1+"WHERE card_id="+ card;
                Statement statement2= connection.createStatement();
                int rows2=statement2.executeUpdate(sql2);
                Date date1 = new Date();
                if (rows2>0){
                    System.out.println("Перевод выполнен\nБаланс карты: "+balance);
                    System.out.println(date1.toString());
                }
                String sql6 = "INSERT INTO operations_history (card_id, transactions, operation_time)"+ "VALUES ("+ card_number+",'-"+ transfer+"','"+date1+"')";
                Statement statement6= connection.createStatement();
                int rows6=statement6.executeUpdate(sql6);

                String sql9="INSERT INTO operations_history (card_id, transactions,operation_time)"+ "VALUES ("+ card+",'+"+ transfer+"','"+date1+"')";
                Statement statement9= connection.createStatement();
                int rows9=statement9.executeUpdate(sql9);
                if (rows9>0 && rows6>0){
                    back();
                }
                break;
            case 4: connection = DriverManager.getConnection(jdbcURL, username, password);
                String sql3 = "UPDATE banking SET is_block=true WHERE pin_code="+pin;
                Statement statement3= connection.createStatement();
                int rows3=statement3.executeUpdate(sql3);
                if (rows3>0){
                    System.out.println("Счет заблокирован");
                    break;
                }
            case 5: menu();
            break;
            case 6:
                Connection connection6 = DriverManager.getConnection(jdbcURL, username, password);
                String sql8 = "SELECT * FROM operations_history WHERE card_id=" + card_number ;
                Statement statement8 = connection6.createStatement();
                ResultSet result8= statement8.executeQuery(sql8);
                while (result8.next()) {
                    String transaction = result8.getString("transactions");
                    String date2= result8.getString("operation_time");
                    System.out.print("Переводы: ");
                    System.out.printf("%s- %s\n",transaction,date2);
                }
                Connection connection8 = DriverManager.getConnection(jdbcURL, username, password);
                String sql10 = "SELECT * FROM deposits WHERE card_id=" + card_number ;
                Statement statement10 = connection8.createStatement();
                ResultSet result10= statement10.executeQuery(sql10);
                while (result10.next()) {
                    String deposit = result10.getString("deposit");
                    String date2= result10.getString("operation_time");
                    System.out.print("Пополнения: ");
                    System.out.printf("%s- %s\n",deposit,date2);
                }
                back();
                break;
        }
    }

    public static void menu(){
        System.out.println("1.Войти в аккаунт\n2.Создать аккаунт");
        Scanner scanner1 = new Scanner(System.in);
        int number = scanner1.nextInt();
        if (number == 2) {
            Creation();
        }
        if (number == 1) {
            Authorization();
        }
        else{
            System.out.println("Некорректный номер");
            menu();
        }
    }
    public static void main(String[] args) {
        menu();

    }
    public static void Creation(){
        System.out.println("Введите логин");
        Scanner scanner = new Scanner(System.in);
        card_number = scanner.nextLong();
        while (card_number<1000000000000000L || card_number>=10000000000000000L){
            System.out.println("Некорректный номер");
            card_number=scanner.nextLong();
        }
        try {
            Connection connection= DriverManager.getConnection(jdbcURL, username, password);
            int pin= (int) (Math.random()*9000+1000);
            System.out.println("Пароль: "+pin);
            String sql = "INSERT INTO banking (card_id, pin_code)"+ "VALUES ("+ card_number+","+ pin+")";
            Statement statement= connection.createStatement();
            int rows=statement.executeUpdate(sql);
            if (rows>0){
                System.out.println("Аккаунт создан");
            }
            menu();
        }

        catch (SQLException e) {
            System.out.println("Аккаунт уже создан");
            Authorization();
            throw new RuntimeException(e);}
    }

    public static void Authorization(){
        System.out.println("Введите логин");
        Scanner scanner = new Scanner(System.in);
        card_number = scanner.nextLong();

        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            String sql = "SELECT * FROM banking WHERE card_id=" + card_number;
            Statement statement = connection.createStatement();
            ResultSet result= statement.executeQuery(sql);
            while (result.next()){
                card =result.getLong("card_id");
                pin=result.getInt("pin_code");
                block=result.getBoolean("is_block");
            }
                while (block==true){
                System.out.println("Счет заблокирован");
                menu();
                }
            if (card!=card_number){
                System.out.println("Создайте аккаунт");
                Creation();
            }
            else {
                System.out.println("Введите пароль");
                Scanner scanner1 = new Scanner(System.in);
                int pin_code= scanner1.nextInt();
                if(pin==pin_code){
                    System.out.println("Авторизация прошла успешно");
                    innerMenu();
                }
                else {
                    System.out.println("Пароль неверный");
                    menu();
                }
            }

        } catch (SQLException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
    }
}
