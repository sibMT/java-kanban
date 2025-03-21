package server;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE");

    private final String httpMethod;

    HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
