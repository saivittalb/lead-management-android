package com.community.jboss.leadmanagement.main.contacts.importcontact;


public class ImportContact {
    private String name;
    private String number;
    private boolean checked = false;

    public ImportContact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

