package com.jens.ToDo.model;

import java.util.logging.Logger;

public class RemoteUserOperationsImpl implements IUserOperations {

    public RemoteUserOperationsImpl() {

    }

    public boolean authenticateUser(User user) {
        return "s@bht.de".equals(user.getEmail()) && "000000".equals(user.getPwd());
    }
}
