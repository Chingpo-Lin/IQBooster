package com.example.iqbooster.model;

public class Tags {
    private boolean technology;
    private boolean health;
    private boolean entertainment;
    private boolean sport;
    private boolean travel;
    private boolean food;
    private boolean psychology;
    private boolean business;

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
}
