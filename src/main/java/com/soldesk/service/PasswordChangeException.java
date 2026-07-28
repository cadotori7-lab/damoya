package com.soldesk.service;

/** 비밀번호 변경 실패 사유를 사용자에게 그대로 보여주기 위한 예외. */
public class PasswordChangeException extends RuntimeException {

    public PasswordChangeException(String message) {
        super(message);
    }
}
