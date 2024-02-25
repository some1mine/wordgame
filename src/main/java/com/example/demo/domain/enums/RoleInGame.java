package com.example.demo.domain.enums;


public enum RoleInGame {

    HOST(0), PARTICIPANT(1), NONE(-1);

    final int code;

    RoleInGame(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return Integer.toString(code);
    }
}
