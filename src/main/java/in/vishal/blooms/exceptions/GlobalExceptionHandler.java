package in.vishal.blooms.exceptions;

import in.vishal.blooms.exceptions.ApplicationException;
import in.vishal.blooms.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler<T> {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<T> handleException(ApplicationException applicationException){
        log.error("ApplicationException caught: ", applicationException);
        return new ApiResponse<T>(false, applicationException.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<T> handleRTException(RuntimeException runtimeException){
        log.error("RuntimeException caught: ", runtimeException);
        return new ApiResponse<T>(false, runtimeException.getMessage(), null);
    }
    
    @ExceptionHandler(Exception.class)
    public ApiResponse<T> handleGlobalException(Exception exception){
        log.error("Unhandled Exception caught: ", exception);
        return new ApiResponse<T>(false, "An unexpected error occurred: " + exception.getMessage(), null);
    }
}