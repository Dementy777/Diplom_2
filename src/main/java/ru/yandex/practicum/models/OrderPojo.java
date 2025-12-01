package ru.yandex.practicum.models;

import java.util.List;

public class OrderPojo {

    private List<String> ingredients;

    public OrderPojo(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public OrderPojo(){
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public OrderPojo setIngredients(){
        this.ingredients=ingredients;
        return this;
    }

}
