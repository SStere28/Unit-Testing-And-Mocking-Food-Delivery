package com.epam.training.fooddelivery;

import com.epam.training.fooddelivery.data.DataStore;
import com.epam.training.fooddelivery.data.FileDataStore;
import com.epam.training.fooddelivery.domain.Customer;
import com.epam.training.fooddelivery.domain.Food;
import com.epam.training.fooddelivery.service.AuthenticationException;
import com.epam.training.fooddelivery.service.DefaultFoodDeliveryService;
import com.epam.training.fooddelivery.service.FoodDeliveryService;
import com.epam.training.fooddelivery.view.CLIView;
import com.epam.training.fooddelivery.view.View;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        Application application = new Application();
        application.start();
    }

    private void start() {
        DataStore fileDataStore = new FileDataStore(readFolderName());
        fileDataStore.init();
        FoodDeliveryService foodDeliveryService = new DefaultFoodDeliveryService(
                fileDataStore);
        View view = new CLIView();

        Customer customer = null;
        Food food;
        int pieces;
        boolean confirmOrder;
        try {
            customer = foodDeliveryService.authenticate(view.readCredentials());
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        view.printWelcomeMessage(customer);

        do {

            view.printAllFoods(foodDeliveryService.listAllFood());
            food = view.selectFood(foodDeliveryService.listAllFood());
            pieces = view.readPieces();
            foodDeliveryService.updateCart(customer, food, pieces);
            view.printAddedToCart(food, pieces);
            view.printCart(customer.getCart());
            confirmOrder = view.promptOrder();

            if (confirmOrder) {
                try {
                    view.printConfirmOrder(foodDeliveryService.createOrder(customer));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    confirmOrder = false;
                }
            }

        } while (!confirmOrder);


    }

    private String readFolderName() {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter path of folder: ");
        return keyboard.next();
    }
}
