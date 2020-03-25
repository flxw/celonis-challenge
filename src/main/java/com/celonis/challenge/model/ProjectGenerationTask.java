package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;

@Entity
public class ProjectGenerationTask extends Task {
    @JsonIgnore
    private String storageLocation;

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

}
