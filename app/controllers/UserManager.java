package controllers;

import models.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserManager {
    private static Map<Integer, User> users = new HashMap<>();
    private static AtomicInteger currentId = new AtomicInteger(0);

    public static Collection<User> getUsers() {
        return users.values();
    }

    public static void addUser(User u) {
        users.put(u.getId(), u);
    }

    public static void removeUser(int id) {
        users.remove(id);
    }

    public static User getUser(int id) {
        return users.get(id);
    }

    public static int getNumUsers() {
        return users.size();
    }

    public static int getId() {
        return currentId.incrementAndGet();
    }

    public static boolean exists(int id) {
        return users.containsKey(id);
    }

    public static boolean existsUsername(String username) {
        boolean found = false;
        User[] values = users.values().toArray(new User[0]);
        for (int i = 0; i < values.length && !found; i++) {
            found = values[i].getUsername().equals(username);
        }
        return found;
    }
}
