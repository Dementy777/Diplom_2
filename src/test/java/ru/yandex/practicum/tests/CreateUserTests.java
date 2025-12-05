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
import static org.hamcrest.CoreMatchers.*;

public class CreateUserTests extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
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
    }

    @Test
    @DisplayName("Проверка на создание пользователя")
    @Description("Позитивный тест на создание пользователя с заполненными полями")
    public void shouldCreateUserTest() {
        ValidatableResponse response = userSteps.createUser(user).statusCode(SC_OK);
        createdAccessToken = userSteps.extractAccessToken(response); // Сохраняем доступный токен
        response.body("accessToken", notNullValue()); // Проверяем наличие токена
    }

    @Test
    @DisplayName("Проверка на невозможность создания одинаковых пользователей")
    @Description("Негативный тест на невозможность создание одинаковых пользователей")
    public void impossibleCreateIdenticalUsersTest() {
        ValidatableResponse response = userSteps.createUser(user);
        createdAccessToken = userSteps.extractAccessToken(response);
        userSteps.createUser(user).statusCode(SC_FORBIDDEN).body("message", equalTo("User already exists"));
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
    public void tearDown() {
        if (createdAccessToken != null && !createdAccessToken.isEmpty()) { // Убедимся, что мы имеем валидный токен перед удалением
            user.setAccessToken(createdAccessToken);
            userSteps.deleteUser(user).statusCode(SC_ACCEPTED);
            System.out.println("Пользователь успешно удалён.");
        } else {
            System.out.println("Пользователь не был создан или токен отсутствует. Удаление не требуется.");
        }
    }
}
