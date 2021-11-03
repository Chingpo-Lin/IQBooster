package com.example.iqbooster.model;

import java.util.ArrayList;

public class Tags {
    public boolean technology;
    public boolean health;
    public boolean entertainment;
    public boolean sport;
    public boolean travel;
    public boolean food;
    public boolean psychology;
    public boolean business;

    public Tags() {
    }

    public Tags(int custom) {
        this.technology = false;
        this.health = false;
        this.entertainment = false;
        this.sport = false;
        this.travel = false;
        this.food = false;
        this.psychology = false;
        this.business = false;
    }


    public Tags(boolean technology, boolean health, boolean entertainment, boolean sport, boolean travel, boolean food, boolean psychology, boolean business) {
        this.technology = technology;
        this.health = health;
        this.entertainment = entertainment;
        this.sport = sport;
        this.travel = travel;
        this.food = food;
        this.psychology = psychology;
        this.business = business;
    }

    public boolean isTechnology() {
        return technology;
    }

    public void setTechnology(boolean technology) {
        this.technology = technology;
    }

    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }

    public boolean isEntertainment() {
        return entertainment;
    }

    public void setEntertainment(boolean entertainment) {
        this.entertainment = entertainment;
    }

    public boolean isSport() {
        return sport;
    }

    public void setSport(boolean sport) {
        this.sport = sport;
    }

    public boolean isTravel() {
        return travel;
    }

    public void setTravel(boolean travel) {
        this.travel = travel;
    }

    public boolean isFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public boolean isPsychology() {
        return psychology;
    }

    public void setPsychology(boolean psychology) {
        this.psychology = psychology;
    }

    public boolean isBusiness() {
        return business;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public ArrayList<String> allTrue() {
        ArrayList<String> temp = new ArrayList<>();
        if (technology) {
            temp.add("technology");
        }
        if (health) {
            temp.add("health");
        }
        if (entertainment) {
            temp.add("entertainment");
        }
        if (sport) {
            temp.add("sport");
        }
        if (travel) {
            temp.add("travel");
        }
        if (food) {
            temp.add("food");
        }
        if (psychology) {
            temp.add("psychology");
        }
        if (business) {
            temp.add("business");
        }
        return temp;
    }
}
