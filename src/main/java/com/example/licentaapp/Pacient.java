package com.example.licentaapp;

public class Pacient {
    private int id;
    private int puls;
    private boolean activitatefizica;
    private String data;
    public Pacient(int id, int puls, boolean activitatefizica, String data) {
        super();
        this.id = id;
        this.puls = puls;
        this.activitatefizica = activitatefizica;
        this.data = data;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPuls() {
        return puls;
    }
    public void setPuls(int puls) {
        this.puls = puls;
    }
    public boolean isActivitatefizica() {
        return activitatefizica;
    }
    public void setActivitatefizica(boolean activitatefizica) {
        this.activitatefizica = activitatefizica;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String activitatefizica1=activitatefizica?"Da":"Nu";
        return "Pacientul " + id+", are pulsul: "+puls+", activitate: "+activitatefizica1+", la data: "+data;
    }
}
