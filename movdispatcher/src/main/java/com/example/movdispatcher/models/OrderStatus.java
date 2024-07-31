package com.example.movdispatcher.models;

public enum OrderStatus {
    RECEIVED, // order first received
    CANCELLED, // delivery got cancelled (by customer or other reasons)
    PAYMENT_PENDING, // waiting for payment confirmation before processing delivery
    PAYMENT_COMPLETED,
    PROCESSING, // being prepared and processed
    OUT_FOR_DELIVERY, // the order is on the way
    DELAYED, // delayed due to reasons
    DELIVERED // delivered to the specified location
}
