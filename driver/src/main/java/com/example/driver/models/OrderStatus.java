package com.example.driver.models;

public enum OrderStatus {
    RECEIVED, // order first received
    CANCELLED, // delivery got cancelled (by customer or driver or other reasons)
    OUT_FOR_DELIVERY, // the order is on the way
    DELAYED, // delayed due to reasons
    DELIVERED // delivered to the specified location
}
