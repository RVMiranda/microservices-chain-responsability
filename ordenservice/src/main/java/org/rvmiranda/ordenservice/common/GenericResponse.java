package org.rvmiranda.ordenservice.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> GenericResponse<T> success(T data, String message) {
        return GenericResponse.<T>builder().data(data).success(true).message(message).build();
    }

    public static <T> GenericResponse<T> error(String message){
        return GenericResponse.<T>builder().data(null).success(false).message(message).build();
    }
}
