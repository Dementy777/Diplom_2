package ru.yandex.practicum.models;

public class UserPojo {

    private String login;
    private String password;
    private String firstName;
    private String accessToken;
    private String refreshToken;

    public String getLogin() {
        return login;
    }

    public UserPojo setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserPojo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserPojo setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserPojo setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
