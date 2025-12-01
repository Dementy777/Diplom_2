package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.UserPojo;
import ru.yandex.practicum.steps.UserSteps;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.*;

public class CreateUserTests extends BaseTest {
    private UserSteps userSteps = new UserSteps();
    private UserPojo user;

    @Before
    public void setUp() {
        user = new UserPojo();
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "@" + domain;
        user
                .setEmail(email)
                .setPassword(RandomStringUtils.randomAlphanumeric(11))
                .setName(RandomStringUtils.randomAlphanumeric(10));
    }

    @Test
    @DisplayName("Проверка на создание пользователя")
    @Description("Позитивный тест на создание пользователя с заполненными полями")
    public void shouldCreateUserTest() {
        userSteps
                .createUser(user)
                .statusCode(SC_OK)
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Проверка на невозможность создания одинаковых пользователей")
    @Description("Негативный тест на невозможность создание одинаковых пользователей")
    public void  impossibleCreateIdenticalUsersTest() {
        userSteps
                .createUser(user)
                .statusCode(SC_OK)
                .body("accessToken", notNullValue());
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверка на невозможность создание пользователя без емейл")
    @Description("Негативный тест на невозможность создания пользователя без обязательного  без  поля  емейл")
    public void shouldNotCreateUserWithoutEmail() {
        user.setEmail("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка на невозможность создание пользователя без пароля")
    @Description("Негативный тест на невозможность создания пользователя без обязательного поля  пароль")
    public void shouldNotCreateUserWithoutPassword() {
        user.setPassword("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка на невозможность создание пользователя без имени")
    @Description("Негативный тест на невозможность создания пользователя без обязательного поля  имя")
    public void shouldNotCreateUserWithoutName() {
        user.setName("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка на невозможность создание пользователя без емэйла пароля и имени")
    @Description("Негативный тест на невозможность создания пользователя без обязательных  полей  емэйла пароля и имени")
    public void shouldNotCreateUserWithEmptyFields() {
        user.setEmail("");
        user.setPassword("");
        user.setName("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown () {
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        if (loginResponse.extract().statusCode() == HttpStatus.SC_OK) {
            String accessToken = loginResponse.extract().body().path("accessToken");
            if (accessToken != null) {
                user.setAccessToken(accessToken);
                userSteps.deleteUser(user);
            } else {
                System.err.println("accessToken не найден в ответе авторизации. Удаление пользователя пропущено.");
            }
        } else {
            System.err.println("Пользователь не был создан. Статус: " + loginResponse.extract().statusCode());
        }
    }



}
