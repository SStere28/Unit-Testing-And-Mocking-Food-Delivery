/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.epam.training.fooddelivery;

import com.epam.training.fooddelivery.data.DataStore;
import com.epam.training.fooddelivery.data.FileDataStore;
import com.epam.training.fooddelivery.domain.*;
import com.epam.training.fooddelivery.service.AuthenticationException;
import com.epam.training.fooddelivery.service.DefaultFoodDeliveryService;
import com.epam.training.fooddelivery.service.FoodDeliveryService;
import com.epam.training.fooddelivery.service.LowBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceTest {

    DataStore dataStore;
    Customer customer;
    User user;

    Cart cart;
    Food food;

    FoodDeliveryService foodDeliveryService;
    private final String USERNAME = "test@epam.com";
    private static final String PASSWORD = "password";


    @BeforeEach
    public void setup() {
        dataStore = mock(FileDataStore.class);
        customer = new Customer();
        user = new User();
        food = new Food();
        cart = new Cart();
        foodDeliveryService = new DefaultFoodDeliveryService(dataStore);
    }

    @Test
    void checkIfAuthenticate() {
        user.setEmail(USERNAME);
        user.setPassword(PASSWORD);
        customer.setEmail(USERNAME);
        customer.setPassword(PASSWORD);

        when(dataStore.getCustomers()).thenReturn(List.of(customer));

        assertEquals(foodDeliveryService.authenticate(user).getEmail(), customer.getEmail());
        assertEquals(foodDeliveryService.authenticate(user).getName(), customer.getName());
    }

    @Test
    void checkIfNotAuthenticate() {

        user.setEmail(USERNAME);
        user.setPassword(PASSWORD);

        when(dataStore.getCustomers()).thenReturn(Collections.EMPTY_LIST);

        Throwable exception = assertThrows(AuthenticationException.class, () -> foodDeliveryService.authenticate(user));
        assertEquals("User it's not found", exception.getMessage());
    }

    @Test
    void checkIfUserAndPasswordIsWrongAuthenticate() {
        user.setEmail(USERNAME);
        user.setPassword(PASSWORD);
        customer.setEmail("email");
        customer.setPassword("pass");

        when(dataStore.getCustomers()).thenReturn(List.of(customer));

        Throwable exception = assertThrows(AuthenticationException.class, () -> foodDeliveryService.authenticate(user));
        assertEquals("User it's not found", exception.getMessage());
    }

    @Test
    public void checkIfListAllFoods() {

        when(dataStore.getFoods()).thenReturn(List.of(food));

        assertEquals(foodDeliveryService.listAllFood().get(0), food);
    }

    @Test
    public void checkIfCustomerCartIsEmpty() {

        cart.setPrice(BigDecimal.ZERO);
        customer.setCart(cart);

        assertThrows(IllegalStateException.class, () -> foodDeliveryService.createOrder(customer));

    }

    @Test
    public void checkCustomerBalance() {

        cart.setPrice(BigDecimal.TEN);
        customer.setBalance(BigDecimal.ONE);
        customer.setCart(cart);
        Throwable thrown = assertThrows(LowBalanceException.class, () -> foodDeliveryService.createOrder(customer));

        assertEquals(thrown.getMessage(), "You don't have enough money, your balance is only " + customer.getBalance() +
                " EUR. We do not empty your cart, please remove some of the items.");

    }

    @Test
    public void checkIfCreateOrder(){

        Order order=new Order();
        customer.setId(5);
        customer.setCart(cart);
        order.setCustomerId(customer.getId());
        order.setOrderId(2l);
        customer.setBalance(BigDecimal.TEN);
        customer.setOrders(Collections.emptyList());
        cart.setPrice(BigDecimal.ONE);
        cart.setOrderItems(List.of(new OrderItem()));

        when(dataStore.getOrders()).thenReturn(List.of(order));
        when(dataStore.createOrder(order)).thenReturn(order);
        foodDeliveryService.createOrder(customer);
        order.setOrderId(3l);
       assertEquals(customer.getOrders().get(0).getOrderId(), order.getOrderId());
    }

    @Test
    public void checkIfCreateOrderWithCustomerOrders(){

        Order order=new Order();
        customer.setId(5);
        customer.setCart(cart);
        order.setCustomerId(customer.getId());
        order.setOrderId(2l);
        customer.setBalance(BigDecimal.TEN);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        customer.setOrders(orders);
        cart.setPrice(BigDecimal.ONE);
        cart.setOrderItems(List.of(new OrderItem()));

        when(dataStore.getOrders()).thenReturn(List.of(order));
        when(dataStore.createOrder(order)).thenReturn(order);
        foodDeliveryService.createOrder(customer);
        order.setOrderId(3l);
        assertEquals(customer.getOrders().get(0).getOrderId(), order.getOrderId());
    }

    @Test
    public void updateCartNoCart() {

        food.setName("Food");
        food.setCategory(Category.MEAL);
        food.setPrice(BigDecimal.TEN);
        cart.setOrderItems(List.of());
        customer.setCart(cart);

        foodDeliveryService.updateCart(customer,food,3);
        assertEquals(customer.getCart().getPrice(),food.getPrice().multiply(BigDecimal.valueOf(3)));
    }

    @Test
    public void updateCartRemoveFood(){
        food.setName("Food");
        food.setCategory(Category.MEAL);
        food.setPrice(BigDecimal.TEN);
        OrderItem orderItem= new OrderItem();
        orderItem.setFood(food);
        List<OrderItem> orders = new ArrayList<>();
        orders.add(orderItem);
        customer.setOrders(Collections.emptyList());
        customer.setCart(cart);
        cart.setOrderItems(orders);
        foodDeliveryService.updateCart(customer,food,0);
        assertEquals(customer.getCart().getPrice().intValue(),0);
    }

    @Test
    public void updateCartUpdateFoodPieces(){
        food.setName("Food");
        food.setCategory(Category.MEAL);
        food.setPrice(BigDecimal.TEN);
        OrderItem orderItem= new OrderItem();
        orderItem.setFood(food);
        List<OrderItem> orders = new ArrayList<>();
        orders.add(orderItem);
        customer.setOrders(Collections.emptyList());
        customer.setCart(cart);
        cart.setOrderItems(orders);
        foodDeliveryService.updateCart(customer,food,3);
        assertEquals(customer.getCart().getPrice().intValue(),30);
    }

    @Test
    public void updateCartAddNewFood(){
        food.setName("Food");
        food.setCategory(Category.MEAL);
        food.setPrice(BigDecimal.TEN);
        OrderItem orderItem= new OrderItem();
        orderItem.setFood(food);
        List<OrderItem> orders = new ArrayList<>();
        orders.add(orderItem);
        customer.setOrders(Collections.emptyList());
        customer.setCart(cart);
        cart.setOrderItems(orders);
        cart.setPrice(BigDecimal.TEN);

        food = new Food();
        food.setName("Food2");
        food.setCategory(Category.MEAL);
        food.setPrice(BigDecimal.TEN);
        foodDeliveryService.updateCart(customer,food,3);
        assertEquals(customer.getCart().getPrice().intValue(),40);
    }
}