package com.ibm.oas.overlay;

import com.fasterxml.jackson.databind.JsonNode;

public class Action {

    public String target;
    public String description;
    public JsonNode update;
    public Boolean remove = false;

}
