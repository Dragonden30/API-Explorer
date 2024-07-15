package ua.APIexplorer.services;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.APIexplorer.entity.Api;
import ua.APIexplorer.repository.ApiRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class OpenAPIService {

    @Autowired
    private ApiRepository repository;

    // Helper method to fetch and parse the OpenAPI specification from the database
    private OpenAPI fetchAndParseOpenAPI(long id) throws IllegalArgumentException {
        // Fetch the content from the database
        Api api = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid API ID: " + id));
        String apiSpec = api.getSpec();

        // Parse the OpenAPI content
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true); // Example setting, you can configure more based on your needs

        SwaggerParseResult result = new OpenAPIParser().readContents(apiSpec, null, parseOptions);

        if (result.getMessages() != null) {
            result.getMessages().forEach(System.err::println); // Log validation errors and warnings
        }

        return result.getOpenAPI();
    }

    // Helper method to filter out unnecessary data from OpenAPI object
    private Map<String, PathItem> filterOpenAPIPaths(OpenAPI openAPI) {
        if (openAPI == null || openAPI.getPaths() == null) {
            return new HashMap<>();
        }

        Map<String, PathItem> filteredPaths = new HashMap<>();
        openAPI.getPaths().forEach((path, pathItem) -> {
            PathItem filteredPathItem = new PathItem();

            // Filter only necessary operations (GET, POST, etc.)
            if (pathItem.getGet() != null) {
                filteredPathItem.setGet(filterOperation(pathItem.getGet()));
            }
            if (pathItem.getPost() != null) {
                filteredPathItem.setPost(filterOperation(pathItem.getPost()));
            }
            if (pathItem.getPut() != null) {
                filteredPathItem.setPut(filterOperation(pathItem.getPut()));
            }
            if (pathItem.getDelete() != null) {
                filteredPathItem.setDelete(filterOperation(pathItem.getDelete()));
            }
            if (pathItem.getPatch() != null) {
                filteredPathItem.setPatch(filterOperation(pathItem.getPatch()));
            }
            if (pathItem.getOptions() != null) {
                filteredPathItem.setOptions(filterOperation(pathItem.getOptions()));
            }
            if (pathItem.getHead() != null) {
                filteredPathItem.setHead(filterOperation(pathItem.getHead()));
            }
            if (pathItem.getTrace() != null) {
                filteredPathItem.setTrace(filterOperation(pathItem.getTrace()));
            }

            if (!filteredPathItem.readOperations().isEmpty()) {
                filteredPaths.put(path, filteredPathItem);
            }
        });

        return filteredPaths;
    }

    // Helper method to filter unnecessary fields from Operation objects
    private Operation filterOperation(Operation operation) {
        Operation filteredOperation = new Operation();
        filteredOperation.setTags(operation.getTags());
        filteredOperation.setSummary(operation.getSummary());
        filteredOperation.setOperationId(operation.getOperationId());
        filteredOperation.setParameters(operation.getParameters());
        return filteredOperation;
    }

    public OpenAPI parseOpenAPIFromDatabase(long id) {
        try {
            return fetchAndParseOpenAPI(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, PathItem> parsePathsFromDatabase(long id) {
        try {
            OpenAPI openAPI = fetchAndParseOpenAPI(id);
            return filterOpenAPIPaths(openAPI);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // Return an empty map in case of an error
        }
    }
}