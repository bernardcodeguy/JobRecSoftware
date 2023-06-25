package jobs.rec.jobrecsoftware;

import java.sql.Blob;

public class Credential {
    private int id;
    private int user_id;
    private String education,category,experience,ceification;
    private Blob document;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCeification() {
        return ceification;
    }

    public void setCeification(String ceification) {
        this.ceification = ceification;
    }

    public Blob getDocument() {
        return document;
    }

    public void setDocument(Blob document) {
        this.document = document;
    }
}
