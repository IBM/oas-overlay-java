package org.crshnburn.overlay;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

public class Action {

    public String target;
    public String description;
    public JsonNode update;
    public Boolean remove = false;

}
