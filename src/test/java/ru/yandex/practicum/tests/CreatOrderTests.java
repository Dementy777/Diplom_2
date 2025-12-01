package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.models.OrderPojo;
import ru.yandex.practicum.models.UserPojo;
import ru.yandex.practicum.steps.OrderSteps;
import ru.yandex.practicum.steps.UserSteps;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatOrderTests extends BaseTest {

    private UserSteps userSteps = new UserSteps();
    private OrderSteps orderSteps = new OrderSteps();
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
        userSteps.createUser(user);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и правильными ингредиентами")
    @Description("Проверка успешного создания заказа с авторизацией и корректными ингредиентами")
    public void shouldCreateOrderWithAuthAndIngredients() {
        // Получаем правильные ингредиенты
        List<String> validIngredients = orderSteps.getValidIngredients();
        OrderPojo order = new OrderPojo(validIngredients);

        // Авторизуемся
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);

        // Создаем заказ
        ValidatableResponse response = orderSteps.createOrder(order, user.getAccessToken());
        response
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка попытки создать заказ без авторизации")
    public void shouldFailToCreateOrderWithoutAuth() {
        OrderPojo emptyOrder = new OrderPojo();
        ValidatableResponse response = orderSteps.createOrder(emptyOrder, null);
        response.statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка возможности создания заказа без выбора ингредиентов")
    public void shouldFailToCreateOrderWithoutIngredients() {

        OrderPojo emptyOrder = new OrderPojo();
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);
        ValidatableResponse response = orderSteps.createOrder(emptyOrder, createdAccessToken);
        response.body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка невозможности создания заказа с неверным набором ингредиентов")
    public void shouldFailToCreateOrderWithInvalidHash() {
        List<String> invalidIngredients = List.of("invalid_ingredient_hash");
        OrderPojo fakeOrder = new OrderPojo(invalidIngredients);
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);
        ValidatableResponse response = orderSteps.createOrder(fakeOrder, createdAccessToken);
        response.statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}
