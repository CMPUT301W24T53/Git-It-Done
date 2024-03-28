package com.example.gidevents;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

public class NotifTest {
    private Notification mockNotif() {
        return new NotifUpdate("Mock Notification", "This is a mock notification", "2024-3-8");
    }

    @Test
    public void testGetTitle() {
        Notification testNotif = mockNotif();
        assertEquals("Mock Notification", testNotif.getEventTitle());
    }

    @Test
    public void testGetNotifInfo() {
        Notification testNotif = mockNotif();
        assertEquals("This is a mock notification", testNotif.getNotifInfo());
    }

    @Test
    public void testGetDate() {
        Notification testNotif = mockNotif();
        assertEquals("2024-3-8", testNotif.getDate());
    }

    @Test
    public void testSetTitle() {
        Notification testNotif = mockNotif();
        testNotif.setEventTitle("New Mock Notification Title");
        assertEquals("New Mock Notification Title", testNotif.getEventTitle());
    }

    @Test
    public void testSetNotifInfo() {
        Notification testNotif = mockNotif();
        testNotif.setNotifInfo("Updated mock notification details");
        assertEquals("Updated mock notification details", testNotif.getNotifInfo());
    }

    @Test
    public void testSetDate() {
        Notification testNotif = mockNotif();
        testNotif.setDate("2024-3-7");
        assertEquals("2024-3-7", testNotif.getDate());
    }
}
