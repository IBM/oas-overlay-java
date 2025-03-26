package org.crshnburn.overlay;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

public class OverlayProcessor {

    private static ObjectMapper om = new ObjectMapper(new YAMLFactory()
        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));
    
    static {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonNodeJsonProvider(om);
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.of(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);
            }

        });
    }

        public static String processOverlay(String openApi, String overlay) throws JsonMappingException, JsonProcessingException {
        Overlay overlayObj = om.readValue(overlay, Overlay.class);
        JsonNode openApiObj = (JsonNode)Configuration.defaultConfiguration().jsonProvider().parse(openApi);
        for (Action action : overlayObj.actions) {
            if (action.remove) {
                ArrayNode results = JsonPath.using(Configuration.builder().options(Option.AS_PATH_LIST).build()).parse(openApi).read(action.target);
                for (JsonNode result : results) {
                    String path = result.asText();
                    
                    String parent = path.substring(0, path.lastIndexOf("["));
                    ArrayNode removeResults = JsonPath.read(openApiObj, parent);
                    for (JsonNode toRemove : removeResults) {
                        if (toRemove instanceof ObjectNode) {
                            String element = path.substring(path.lastIndexOf("[")+2, path.length()-2);
                            ((ObjectNode)toRemove).remove(element);
                        } else if (toRemove instanceof ArrayNode) {
                            int element = Integer.parseInt(path.substring(path.lastIndexOf("[")+1, path.lastIndexOf("]")));
                            ((ArrayNode)toRemove).remove(element);
                        }
                    }

                }         
            } else {
                ArrayNode results = JsonPath.using(Configuration.builder().options(Option.AS_PATH_LIST).build()).parse(openApi).read(action.target);
                for (JsonNode result : results) {
                    ArrayNode addResults = JsonPath.read(openApiObj, result.asText());
                    for (JsonNode addResult : addResults) {
                        if (addResult instanceof ObjectNode) {
                            mergeChanges((ObjectNode)addResult, (ObjectNode)action.update);
                        } else if (addResult instanceof ArrayNode) {
                            ((ArrayNode)addResult).add(action.update);
                        } //else the JSONPointer points at a field and this is ignored as per the spec
                    }
                }
            }
        }
        return om.writeValueAsString(openApiObj);
    }

    private static void mergeChanges(ObjectNode orig, ObjectNode updates) {
        Iterator<String> updateFields = updates.fieldNames();
        while (updateFields.hasNext()) {
            String updateField = updateFields.next();
            if(orig.get(updateField) != null && orig.get(updateField).isArray()) {
                orig.withArray(updateField).addAll((ArrayNode)updates.get(updateField));
            } else if (orig.get(updateField) != null && orig.get(updateField).isObject()) {
                mergeChanges((ObjectNode)orig.get(updateField), (ObjectNode)updates.get(updateField));
            } else {
                orig.set(updateField, updates.get(updateField));
            }
        }
    }

}
