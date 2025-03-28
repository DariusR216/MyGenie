package com.flightpath.mygenie;

public class Task {
    private String description;
    private boolean completed;

    // Default constructor for Firebase or serialization
    public Task() {}

    // Constructor with parameters
    public Task(String description, boolean completed) {
        this.description = description;
        this.completed = completed;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Toggle completion status
    public void toggleCompletion() {
        this.completed = !this.completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", completed=" + completed +
                '}';
    }
}
