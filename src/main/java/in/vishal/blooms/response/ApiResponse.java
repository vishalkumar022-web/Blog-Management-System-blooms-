package in.vishal.blooms.response;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable { // ✅ Implements Serializable zaroori hai

    private static final long serialVersionUID = 1L; // ✅ Version ID

    private boolean success;
    private String errorMessage;
    private T data;

    // ✅ Default Constructor
    public ApiResponse() {
    }

    // ✅ Parameterized Constructor
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.errorMessage = message;
        this.data = data;
    }

    // ✅ Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}