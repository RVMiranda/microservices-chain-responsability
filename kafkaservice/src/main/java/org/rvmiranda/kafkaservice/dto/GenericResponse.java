package org.rvmiranda.kafkaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {
    private String status; // SUCCESS, ERROR
    private String message;
    private T data;

    public static <T> GenericResponse<T> success(T data, String message) {
        return new GenericResponse<>("SUCCESS", message, data);
    }

    public static <T> GenericResponse<T> error(String message) {
        return new GenericResponse<>("ERROR", message, null);
    }
}
