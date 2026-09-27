package com.librarymanagement.dto;
import jakarta.validation.constraints.*;
public class ApiDtos {
 public record Register(@NotBlank @Size(max=120) String name,@NotBlank @Email String email,@NotBlank @Size(min=8,max=100) String password,String phone){}
 public record Login(@NotBlank @Email String email,@NotBlank String password){}
 public record BookInput(@NotBlank String title,@NotBlank String author,@NotBlank String isbn,String genre,String publisher,@Min(0) Integer publicationYear,@Min(0) Integer quantity,String coverImage,String description){}
 public record ProfileInput(@NotBlank String name,@NotBlank @Email String email,String phone){}
 public record PasswordInput(@NotBlank String currentPassword,@NotBlank @Size(min=8) String newPassword){}
 public record BorrowInput(@NotNull Long bookId){}
}
