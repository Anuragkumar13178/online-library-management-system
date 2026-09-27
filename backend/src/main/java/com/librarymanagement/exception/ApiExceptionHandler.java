package com.librarymanagement.exception;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<?> missing(NoSuchElementException e){return ResponseEntity.status(404).body(Map.of("success",false,"message",e.getMessage()));}
 @ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) ResponseEntity<?> bad(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("success",false,"message",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getDefaultMessage()).orElse("Invalid request.");return ResponseEntity.badRequest().body(Map.of("success",false,"message",m));}
 @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class) ResponseEntity<?> duplicate(){return ResponseEntity.status(409).body(Map.of("success",false,"message","Email or ISBN already exists."));}
}
