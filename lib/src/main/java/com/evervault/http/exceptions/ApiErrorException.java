package com.evervault.http.exceptions;

public class ApiErrorException extends Exception {
    private String code;
    private String title;
    private Integer status;
    private String detail;

    public ApiErrorException(String code, String title, Integer status, String detail) {
        super(detail);
        this.code = code;
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ApiErrorException{" +
                "code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", detail='" + detail + '\'' +
                '}';
    }
}
