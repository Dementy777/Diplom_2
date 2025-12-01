package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.UserPojo;
import ru.yandex.practicum.steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class AuthorizationUserTests extends BaseTest {
    private UserSteps userSteps = new UserSteps();
    private UserPojo user;
    private String createdAccessToken;

    @Before
    public void setUp() {
        user = new UserPojo();
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "+" + System.currentTimeMillis() + "@" + domain;
        user
                .setEmail(email)
                .setPassword(RandomStringUtils.randomAlphanumeric(11))
                .setName(RandomStringUtils.randomAlphanumeric(10));
        userSteps
                .createUser(user);
    }

    @Test
    @DisplayName("позитивный тест на успешную авторизацию пользователя")
    @Description("Пользователь успешно авторизуется")
    public void shouldLoginUserTest() {
        ValidatableResponse response = userSteps.loginUser(user).statusCode(SC_OK);
        createdAccessToken = userSteps.extractAccessToken(response); // Сохраняем доступный токен
        response.body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Негативный тест на невозможность авторизации  пользователя с неверным емэйлом")
    @Description("Пользователь авторизуется без емэйла")
    public void errorAuthorizationWithoutEmailTest() {
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "+" + System.currentTimeMillis() + "@" + domain;
        user
                .setEmail(email);
        userSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Негативный тест на невозможность авторизации  пользователя без пароля")
    @Description("Пользователь авторизуется без пароля")
    public void errorAuthorizationWithoutPasswordTest() {
        user.setPassword(RandomStringUtils.randomAlphanumeric(11));
        userSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }


    @After
    public void tearDown() {
        if (createdAccessToken != null && !createdAccessToken.isEmpty()) { // Убедимся, что мы имеем валидный токен перед удалением
            user.setAccessToken(createdAccessToken);
            userSteps.deleteUser(user).statusCode(SC_ACCEPTED);
            System.out.println("Пользователь успешно удалён.");
        } else {
            System.out.println("Пользователь не был авторизован или токен отсутствует. Удаление не требуется.");
        }
    }

}
