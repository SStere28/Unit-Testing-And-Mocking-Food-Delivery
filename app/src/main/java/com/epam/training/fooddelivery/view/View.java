package com.epam.training.fooddelivery.view;

import com.epam.training.fooddelivery.domain.*;

import java.util.List;

public interface View {
    User readCredentials();

    void printWelcomeMessage(Customer customer);

    void printAllFoods(List<Food> foods);

    Food selectFood(List<Food> foods);

    int readPieces();

    void printAddedToCart(Food food, int pieces);

    void printCart(Cart cart);

    void printConfirmOrder(Order order);

    boolean promptOrder();

    void printErrorMessage(String message);

}
