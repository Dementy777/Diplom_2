package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import ru.yandex.practicum.models.UserPojo;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.config.RestConfig.*;

public class UserSteps {

    @Step("Создание пользователя")
    public ValidatableResponse createUser(UserPojo user) {
        return given()
                .body(user)
                .when()
                .post(POSTREGISTER)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserPojo user) {
        return given()
                .body(user)
                .when()
                .post(POSTLOGIN)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(UserPojo user) {
        return given()
                .header("Authorization", user.getAccessToken())
                .when()
                .delete(DELETUSER)
                .then();
    }

    // Дополнительный метод для извлечения токена из ответа
    public String extractAccessToken(ValidatableResponse response) {
        return response.extract().body().jsonPath().getString("accessToken");
    }

    protected static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(HOST)
                .build();
    }

    protected static RequestSpecification getSpec(String bearerAndToken) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("authorization", bearerAndToken)
                .setBaseUri(HOST)
                .build();
    }

}
