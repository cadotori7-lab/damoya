// package com.soldesk.handler;

// import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.servlet.NoHandlerFoundException;

// @ControllerAdvice // 모든 컨트롤러에서 발생하는 예외를 처리하는 클래스
// public class GlobalExceptionHandler {
//     // 에러처리 핸들러
//     // @ExceptionHandler를 사용하여 특정 예외를 처리할 수 있습니다.
//     private static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

//     @ExceptionHandler(NoHandlerFoundException.class) // 404 에러 처리
//     @ResponseStatus(HttpStatus.NOT_FOUND) // 404 상태 코드 반환
//     public String handlerNotFound(NoHandlerFoundException e, HttpServletRequest request) {
//         log.warn("404 에러 발생: 요청 URL = {}, 메서드 = {}", request.getRequestURL(), request.getMethod());
//         return "error/404"; // 존재하지 않는 요청 시, 404 에러 페이지로 이동
//     }

//     @ExceptionHandler(Exception.class) // 기타 예외 처리
//     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 상태 코드 반환
//     public String handlerException(Exception e, HttpServletRequest request) {
//         log.error("예외 발생: 요청 URL = {}, 메서드 = {}", request.getRequestURL(), request.getMethod(), e);
//         return "error/500"; // 기타 예외 발생 시, 500 에러 페이지로 이동
//     }
    
// }
