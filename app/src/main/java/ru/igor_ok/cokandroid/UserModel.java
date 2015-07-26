package ru.igor_ok.cokandroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igor on 26.07.15.
 */
public class UserModel {

    public class UserItem {
        public String _id;
        public String login;
        public String email;
    }

    public class UserList {
        public List<UserItem> users = new ArrayList<UserItem>();
    }
}
