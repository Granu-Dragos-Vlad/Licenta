package com.example.licentaapp;

public class Medic {
    private int id;
    private String listapac;
    public Medic(int id, String listapac)
    {

        this.id = id;
        this.listapac = listapac;
    }
    public Medic() {};
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getListapac()
    {
        return listapac;
    }
    public void setListapac(String listapac)
    {
        this.listapac = listapac;
    }
}
