/*
Copyright 2025 IBM

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ibm.oas.overlay;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OverlayTest {

  @DataProvider(name = "overlays")
  public Object[][] overlays() {
    return new Object[][]{
      // {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-properties.yaml"), "src/test/resources/expected/town-remove-properties.yaml"}, // Commented out as JSON Path doesn't parse path correctly
      {new Input("src/test/resources/openapi/petstore.yaml", "src/test/resources/overlays/overlay.yaml"), "src/test/resources/expected/output1.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/building-description.yaml"), "src/test/resources/expected/town-building-description.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/update-root.yaml"), "src/test/resources/expected/town-root-updated.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-example.yaml"), "src/test/resources/expected/town-remove-example.yaml"},
      {new Input("src/test/resources/openapi/town.yaml", "src/test/resources/overlays/remove-descriptions.yaml"), "src/test/resources/expected/town-remove-descriptions.yaml"},
      {new Input("src/test/resources/openapi/openapi-with-servers.yaml", "src/test/resources/overlays/remove-server.yaml"), "src/test/resources/expected/one-less-server.yaml"},
      {new Input("src/test/resources/openapi/immutable.yaml", "src/test/resources/overlays/immutable.yaml"), "src/test/resources/expected/immutable.yaml"},
      {new Input("src/test/resources/openapi/responses.yaml", "src/test/resources/overlays/remove-responses.yaml"), "src/test/resources/expected/remove-responses.yaml"},
      // {new Input("src/test/resources/openapi/traits.yaml", "src/test/resources/overlays/traits.yaml"), "src/test/resources/expected/traits.yaml"}, //Commented out as JSON Path doesn't parse and find as expected
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

