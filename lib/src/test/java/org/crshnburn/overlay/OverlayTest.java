package org.crshnburn.overlay;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OverlayTest {

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

  private String openApi = """
openapi: 3.1.0
info:
  title: Example API
  version: 1.0.0
paths:
  /example:
    get:
      responses:
        '200':
          description: A simple example response
          content:
            application/json:
              schema:
                type: object
                properties:
                  message:
                    type: string
      """;

  private ObjectMapper om = new ObjectMapper();

  @Test public void applyOverlay() throws JsonMappingException, JsonProcessingException {
      String result = OverlayProcessor.processOverlay(openApi, testOverlay);
      JsonNode resultJson = om.readTree(result);
      assertTrue(resultJson.at("/info/description").asText().contains("meaningful description"));
  }

  @DataProvider(name = "overlays")
  public Object[][] overlays() {
    return new Object[][]{
      // {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-properties.yaml"), "src/test/resources/expected/town-remove-properties.yaml"}, // Commented out as JSON Path doesn't parse path correctly
      // {new Input("src/test/resources/openapi/not-jsonpath.yaml", "src/test/resources/overlays/not-jsonpath.yaml"), "src/test/resources/expected/not-jsonpath.yaml"}, // TODO sort out how this should fail
      // {new Input("src/test/resources/openapi/not-overlay.yaml", "src/test/resources/overlays/not-overlay.yaml"), "src/test/resources/expected/not-overlay.yaml"}, //TODO sort out how this should fail
      {new Input("src/test/resources/openapi/petstore.yaml", "src/test/resources/overlays/overlay.yaml"), "src/test/resources/expected/output1.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/building-description.yaml"), "src/test/resources/expected/town-building-description.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/update-root.yaml"), "src/test/resources/expected/town-root-updated.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-example.yaml"), "src/test/resources/expected/town-remove-example.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-descriptions.yaml"), "src/test/resources/expected/town-remove-descriptions.yaml"},
      {new Input("src/test/resources/openapi/openapi-with-servers.yaml", "src/test/resources/overlays/remove-server.yaml"), "src/test/resources/expected/one-less-server.yaml"},
      {new Input("src/test/resources/openapi/immutable.yaml", "src/test/resources/overlays/immutable.yaml"), "src/test/resources/expected/immutable.yaml"},
      {new Input("src/test/resources/openapi/responses.yaml", "src/test/resources/overlays/remove-responses.yaml"), "src/test/resources/expected/remove-responses.yaml"},
      {new Input("src/test/resources/openapi/traits.yaml", "src/test/resources/overlays/traits.yaml"), "src/test/resources/expected/traits.yaml"},
    };
  }


  @Test(dataProvider = "overlays")
  public void testOverlay(Input inputs, String expected) {
    try {
      String openApi = new String(Files.readAllBytes(Path.of(inputs.openApi)));
      String overlay = new String(Files.readAllBytes(Path.of(inputs.overlay)));
      String expectedResult = new String(Files.readAllBytes(Path.of(expected)));
      String result = OverlayProcessor.processOverlay(openApi, overlay);
      assertEquals(result, expectedResult);
    } catch (IOException e) {
      fail(e.getMessage(), e);
    }
  }

  private class Input {
    public String openApi;
    public String overlay;

    public Input(String openApi, String overlay) {
      this.openApi = openApi;
      this.overlay = overlay;
    }
  }

}

