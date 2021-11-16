package com.example.grandinstrument.data_base_model;

import java.util.Objects;

public class Client {
    private String id_1c;
    private String guid_1c;
    private String name;
    private String phone;
    private String api_key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        if (guid_1c==null && client.guid_1c == null) return true;
        if (guid_1c==null) return false;
        return guid_1c.equals(client.guid_1c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid_1c);
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId_1c() {
        return id_1c;
    }

    public void setId_1c(String id_1c) {
        this.id_1c = id_1c;
    }

    public String getGuid_1c() {
        return guid_1c;
    }

    public void setGuid_1c(String guid_1c) {
        this.guid_1c = guid_1c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
