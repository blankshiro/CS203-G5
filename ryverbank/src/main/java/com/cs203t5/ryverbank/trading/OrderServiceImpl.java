package com.cs203t5.ryverbank.trading;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderServices {
    private OrderRepository orders;

    public List<Order> listOrders() {
        return orders.findAll();
    }
}
