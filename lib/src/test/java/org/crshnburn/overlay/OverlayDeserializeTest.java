package org.crshnburn.overlay;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class OverlayDeserializeTest {

  private String testOverlay = """
    overlay: 1.0.0
    info:
      title: Overlay to update the description
      version: 0.1.0
    actions:
      - target: $.info
        update:
          description: >-
            A meaningful description for your API helps users to understand how to
            get started with your platform. Description fields support Markdown and
            the `>-` notation at the start makes this multiline Markdown.

            You can link to your [project README](https://github.com/lornajane/openapi-overlays-js)
            or other resources from here as well.
  """;

  private ObjectMapper om = new ObjectMapper(new YAMLFactory());

  @Test public void parseOverlay() throws JsonMappingException, JsonProcessingException {
    Overlay overlay = om.readValue(testOverlay, Overlay.class);
    assertEquals(overlay.overlay, "1.0.0");
  }

  @Test(expectedExceptions = DatabindException.class) public void notJsonPath() throws IOException {
    om.readValue(new File("src/test/resources/overlays/not-jsonpath.yaml"), Overlay.class);
  }

  @Test(expectedExceptions = DatabindException.class) public void notOverlay() throws IOException {
    om.readValue(new File("src/test/resources/overlays/not-overlay.yaml"), Overlay.class);
  }

}

