package com.example.organizeteam;

/**
 * @author ofek gani
 * @version 1.0
 * @since 30-07-2020
 */
public class Team
{
    private String name;
    private String description;

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
