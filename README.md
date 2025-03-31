# oas-overlay-java

Java library for processing [OpenAPI Overlay documents](https://github.com/OAI/Overlay-Specification/blob/main/versions/1.0.0.md)

## Usage

The `OverlayProcessor` class provides a method `processOverlay` which takes the OpenAPI document as a String and the Overlay document as a String and returns a String of the OpenAPI document with the updates applied.

Example:

```java
public class OverlayExample {

    private static String openAPI = """
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
    """

    private static String overlay = """
overlay: 1.0.0
info:
  title: Overlay to update the description
  version: 1.0.0
actions:
- target: $.info
  update:
    description: >-
      A description about the API for the user to understand what is going on.
    """

    public static void main(String arg[]) {
        System.out.println(OverlayProcessor.processOverlay(openAPI, overlay));
    }
}
```

