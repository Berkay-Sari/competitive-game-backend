package com.dreamgames.backendengineeringcasestudy.exception;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long groupId) {
        super("Group with id " + groupId + " not found");
    }
}
