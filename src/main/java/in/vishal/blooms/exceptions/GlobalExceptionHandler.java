package in.vishal.blooms.exceptions;

import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.response.ApiResponse;
import in.vishal.blooms.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler<T> {
    // You can add methods here to handle specific exceptions and return appropriate responses
    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<T> handleException(ApplicationException applicationException){
        return new ApiResponse<T>(false, applicationException.getMessage(), null);

    }        // You can add methods here to handle specific exceptions and return appropriate responses
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<T> handleRTException(RuntimeException runtimeException){
        return new ApiResponse<T>(false, runtimeException.getMessage(), null);
    }
}