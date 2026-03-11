package com.team15.partpicker.model.dto;

import javax.validation.constraints.NotBlank;

public class CreateBuildRequest {

    @NotBlank
    private String buildTitle;

    public CreateBuildRequest() {
    }

    public String getBuildTitle() {
        return buildTitle;
    }

    public void setBuildTitle(String buildTitle) {
        this.buildTitle = buildTitle;
    }
}
