package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.config.RestConfig;
import ru.yandex.practicum.models.UserPojo;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.config.RestConfig.*;

public class UserSteps {

@Step("Создание пользователя")
  public ValidatableResponse creatUser(UserPojo user){
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
                .pathParam("accessToken", user.getAccessToken())
                .when()
                .delete(DELETUSER)
                .then();





}
