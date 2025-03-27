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

