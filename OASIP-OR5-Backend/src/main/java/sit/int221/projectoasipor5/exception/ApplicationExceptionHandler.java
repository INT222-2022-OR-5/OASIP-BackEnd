package sit.int221.projectoasipor5.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler extends Exception {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HandleError handleInvalidArgument(MethodArgumentNotValidException ex, ServletWebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> {
                    errorMap.put(error.getField(), error.getDefaultMessage());
                }
        );
        HandleError errors = new HandleError(
                Date.from(Instant.now()), HttpStatus.BAD_REQUEST.value(), request.getRequest().getRequestURI(), "Validation failed",
                "Bad request", errorMap);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OverlappedExceptionHandler.class)
    public HandleOverlappedError handleOverlapped(OverlappedExceptionHandler rs) {
        HandleOverlappedError errors = new HandleOverlappedError();
        errors.setStatus(400);
        errors.setPath("/api/events");
        errors.setMessage("Bad Request");
        errors.setError("Time is Overlapped");
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", rs.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public HandleValidationErrors handleCheckUnique(DataIntegrityViolationException de, ServletWebRequest request) {
        HandleValidationErrors errors = new HandleValidationErrors();
        errors.setStatus(500);
        errors.setPath(request.getRequest().getRequestURI());
        errors.setMessage("Internal Server Error");
        errors.setError("Clinic name must be unique.");
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CheckUniqueUserExceptionHandler.class)
    public HandleValidationErrors handleCheckUniqueUser(CheckUniqueUserExceptionHandler cu, ServletWebRequest request ) {
        HandleValidationErrors errors = new HandleValidationErrors();
        String mser = cu.getMessage();
        errors.setStatus(500);
        errors.setPath(request.getRequest().getRequestURI());
        errors.setMessage("Internal Server Error");
        errors.setError(mser);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HandleValidationErrors handleNotEnum(HttpMessageNotReadableException ex, ServletWebRequest request) {
        HandleValidationErrors errors = new HandleValidationErrors();
        errors.setStatus(400);
        errors.setPath("/api/users");
        errors.setMessage("Bad Request");
        errors.setError("Role is wrong.");
        return errors;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MatchUserExceptionHandler.class)
    public ResponseEntity MatchUserExceptionHandler(MatchUserExceptionHandler cu, ServletWebRequest request ,HttpStatus httpStatus, String message) {
        HandleValidationErrors errors = new HandleValidationErrors();
        String mser = cu.getMessage();
        errors.setStatus(httpStatus.value());
        errors.setPath(request.getRequest().getRequestURI());
        errors.setMessage(message);
        errors.setError(mser);
        return (ResponseEntity) ResponseEntity.status(httpStatus.value()).body(errors);
    }

    public static ResponseEntity<Object> uniqueHandleException(HttpStatus httpStatus, String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", Instant.now());
        errorMap.put("status", httpStatus.value());
        errorMap.put("message", message);
        errorMap.put("path", "/api/match");
        errorMap.put("error", httpStatus.getReasonPhrase());
        return new ResponseEntity<Object>(errorMap, httpStatus);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HandleExceptionForbidden.class)
    public HandleErrorUnsucceess handleExceptionForbidden(HandleExceptionForbidden fb, ServletWebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        Date timestamp = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        errorMap.put("Error:", fb.getMessage());
        return new HandleErrorUnsucceess(sdf3.format(timestamp), HttpStatus.FORBIDDEN.value(),
                request.getRequest().getRequestURI(), "Validation", "Forbidden", errorMap);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandleExceptionBadRequest.class)
    public HandleErrorUnsucceess HandleExceptionBadRequest (HandleExceptionBadRequest br, ServletWebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        Date timestamp = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        errorMap.put("Error:", br.getMessage());
        return new HandleErrorUnsucceess(sdf3.format(timestamp), HttpStatus.FORBIDDEN.value(),
                request.getRequest().getRequestURI(), "Validation Failed", "Bad Request", errorMap);
    }
}
