package com.example.myapplication.presells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RootModel {
    private List<RoomModel> rooms = new ArrayList<RoomModel>();
    private String floor;
    public List<RoomModel> getRooms() {
        return rooms;
    }
    public void setRooms(List<RoomModel> rooms) {
        this.rooms = rooms;
    }
    public String getFloor() {
        return floor;
    }
    public void setFloor(String floor) {
        this.floor = floor;
    }
}
