package project.services;

import org.springframework.stereotype.Component;
import project.exceptions.TaskNotFoundException;
import project.exceptions.UserExistsException;
import project.exceptions.UserNotFoundException;
import project.models.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;


@Component
public class ConsoleIOService implements IOService {
//    abstract void output(String s);
//    abstract String readLine();

    private Model model;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            User user = null;
            try {
                while (user == null) {
                    System.out.println("Введите \"help\" для списка команд");
                    String s = bufferedReader.readLine();
                    switch (s) {
                        case "reg":
                            String name;
                            String secondName;
                            do {
                                System.out.println("Введите имя: ");
                                name = bufferedReader.readLine();
                            } while (name.trim().isEmpty());
                            do {
                                System.out.println("Введите фамилию: ");
                                secondName = bufferedReader.readLine();
                            } while (secondName.trim().isEmpty());
                            user = model.createUser(new UserData(name, secondName));
                            System.out.println(user);
                            break;
                        case "login":
                            while (true) {
                                try {
                                    System.out.println("Введите id для того чтобы залогиниться: ");
                                    long id = Long.parseLong(bufferedReader.readLine().trim());
                                    user = model.getUserById(id);
                                    System.out.println(user);
                                    break;
                                } catch (NumberFormatException | IOException e) {
                                    System.err.println("Введите целое число");
                                }
                            }
                            break;
                        case "help":
                            System.out.println("Если хотите зарегестрироваться введите: reg\n" +
                                    "Если хотите залогиниться введите: login\n" +
                                    "Если хотите выйти из программы введите: exit\n");
                            break;
                        case "exit":
                            bufferedReader.close();
                            return;
                    }
                }

                while (true) {
                    System.out.println("Введите \"help\" для списка команд");
                    String s = bufferedReader.readLine();

                    switch (s) {
                        case "create":
                            System.out.println("Введите название задачи: ");
                            String title = bufferedReader.readLine().trim();
                            System.out.println("Введите задачу целиком: ");
                            String taskText = bufferedReader.readLine().trim();
                            Task task = model.createTask(user.getId(), new TaskData(title, taskText, false));
                            System.out.println("Создана задача: ");
                            System.out.println(task.toString());
                            break;
                        case "update":
                            while (true) {
                                try {
                                    System.out.println("Введите id задачи которую хотите обновить: ");
                                    long id = Long.parseLong(bufferedReader.readLine().trim());
                                    model.updateTask(id, model.getTaskById(id).solvedTask(true));
                                    System.out.println("Задача id: " + id + " обновлена.");
                                    break;
                                } catch (NumberFormatException e) {
                                    System.err.println("Введите целое число");
                                } catch (TaskNotFoundException e) {
                                    System.err.println("Такой задачи не обнаружено");
                                }
                            }
                            break;
                        case "active":
                            if (model.getAllActiveTaskOfUser(user.getId()).isEmpty()) {
                                System.out.println("Список активных задач пуст");
                                break;
                            }
                            System.out.println("Список активных задач");
                            model.getAllActiveTaskOfUser(user.getId()).forEach(System.out::println);
                            break;
                        case "all":
                            if (model.getAllTasksOfUser(user.getId()).isEmpty()) {
                                System.out.println("Список задач пуст");
                                break;
                            }
                            System.out.println("Список всех задач");
                            model.getAllTasksOfUser(user.getId()).forEach(System.out::println);
                            break;
                        case "remove":
                            while (true) {
                                try {
                                    System.out.println("Введите id задачи которую хотите удалить: ");
                                    long id = Long.parseLong(bufferedReader.readLine().trim());
                                    model.deleteTask(id);
                                    System.out.println("Задача id: " + id + " удалена.");
                                    break;
                                } catch (NumberFormatException e) {
                                    System.err.println("Введите целое число");
                                }
                            }
                            break;
                        case "delete":
                                    model.deleteUser(user.getId());
                                    System.out.println("Пользователь " + user + " удален.");
                                    bufferedReader.close();
                                    return;
                        case "help":
                            System.out.println("Если хотите создать задачу введите: create\n" +
                                    "Если хотите обновить задачу введите: update\n" +
                                    "Если хотите просмотреть все активные задачи введите: active\n" +
                                    "Если хотите просмотреть все таски введите: all\n" +
                                    "Если хотите удалить профиль введите: delete\n" +
                                    "Если хотете удалить задание введите: remove\n"+
                                    "Если хотите выйти из программы введите: exit\n");
                            break;
                        case "exit":
                            bufferedReader.close();
                            return;
                    }
                }
            } catch (IOException | UserExistsException | UserNotFoundException | TaskNotFoundException
                    e) {
                e.printStackTrace();
            }
        }
    };

    public ConsoleIOService(Model model) {
        this.model = model;
    }

    @Override
    public void start() {
        new Thread(runnable).start();
    }

    @Override
    public void stop() {

    }
}
