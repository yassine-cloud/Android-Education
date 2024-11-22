package com.iset.education.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Entity(tableName = "cours")
public class Cour implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String instructor;
    private String schedule;

    private byte[] document;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public byte[] getDocument() { return document; }
    public void setDocument(byte[] document) { this.document = document; }

    @Override
    public String toString() {
        return "Cour{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                ", schedule='" + schedule + '\'' +
                ", document=" + Arrays.toString(document) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cour cour = (Cour) o;
        return id == cour.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}