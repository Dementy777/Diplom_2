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

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatOrderTests extends BaseTest{

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
        List<String> validIngredients = getValidIngredients();
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



    // Утилитарный метод для получения массива ингредиентов
    private List<String> getValidIngredients() {
        ValidatableResponse ingredientsResponse = OrderSteps.getIngredients(); // Обратите внимание на использование OrderSteps
        return ingredientsResponse.extract().path("data._id");
    }
}
