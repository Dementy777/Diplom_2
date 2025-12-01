package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.config.RestConfig;
import ru.yandex.practicum.models.OrderPojo;

import static io.restassured.RestAssured.given;
import static ru.yandex.practicum.config.RestConfig.POSTORDERS;
import static ru.yandex.practicum.steps.UserSteps.getSpec;

public class OrderSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(OrderPojo order, String bearerAndToken) {
        return given()
                .spec(getSpec(bearerAndToken))
                .body(order)
                .when()
                .post(POSTORDERS)
                .then();
    }

    @Step("Получение заказов конкретного пользователя")
    public static ValidatableResponse getUserOrders(String bearerPlusToken) {
        return given()
                .spec(getSpec(bearerPlusToken))
                .when()
                .get(POSTORDERS)
                .then();
    }
    @Step("Получение списка ингредиентов")
    public static ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec()) // Используйте спецификацию запроса
                .when()
                .get(RestConfig.GETINGREDIENTS) // Константа пути для получения ингредиентов
                .then();
    }
}
