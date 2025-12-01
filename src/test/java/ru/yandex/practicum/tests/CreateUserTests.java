package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
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
    @Description("Негативный тест на невозможность создания пользователя без обязательного  без  поля  пароль")
    public void shouldNotCreateUserWithoutPassword() {
        user.setPassword("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }


}
