package Book;

import Exceptions.*;
import Tradable.*;
import java.util.*;

public final class UserManager {
    private static UserManager instance;
    private TreeMap<String, User> users = new TreeMap<>();
    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(String[] usersIn) throws DataValidationException {
        for (String userId : usersIn) { //loop through users
            if (userId == null) {
                throw new DataValidationException("userId cannot be null");
            }
            User userObj  = new User(userId); //create new user obj
            users.put(userId, userObj); //add the obj to the treemap
        }
    }

    public void updateTradable(String userId, TradableDTO o) throws DataValidationException {
        if (userId == null | o == null | !(users.containsKey(userId))) {
            throw new DataValidationException("userId or DTO cannot be null and userId must exist");
        }
        User user = users.get(userId); //get user from treemap
        user.updateTradable(o); //update that users tradable
    }

    public User getUser(String userId) throws UnknownUserException {
        User user = users.get(userId);
        if (user == null) {
            throw new UnknownUserException("User does not exist");
        }
        return user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            sb.append(entry.getValue().toString()).append("\n...\n");
        }
        return sb.toString();
    }
}
