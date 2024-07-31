package com.example.simplymov.models;

public enum OrderStatus {
    RECEIVED, // order first received
    CANCELLED, // delivery got cancelled (by customer or other reasons)
    PROCESSING, // being prepared and processed
    OUT_FOR_DELIVERY, // the order is on the way
    DELIVERED // delivered to the specified location
}
