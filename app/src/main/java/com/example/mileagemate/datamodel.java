package com.example.mileagemate;

public class datamodel {
    private int id;
    private String task_name;
    private  String task_date;
    private String task_desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public datamodel(String task_name, String task_date, String task_desc) {
        this.task_name = task_name;
        this.task_date = task_date;
        this.task_desc = task_desc;
    }
    datamodel(){

    }


    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }
}
