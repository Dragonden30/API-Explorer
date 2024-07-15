package ua.APIexplorer.controller;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.APIexplorer.entity.Api;
import ua.APIexplorer.repository.ApiRepository;
import ua.APIexplorer.services.OpenAPIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiRepository apiRepository;
    @Autowired
    private OpenAPIService openAPIService;


    //"getApi" This method retrieves a list of all APIs or searches for APIs containing a specific word in their name/id.
    //Test request GET http://localhost:8080/api (to find all) + ?name= (find api via specific name) or ?id= (find api via specific id)
    @GetMapping
    public ResponseEntity<List<Api>> getApi(@RequestParam("name") Optional<String> searchParam,
                                            @RequestParam("id") Optional<Long> idParam) {
        List<Api> apis;

        if(idParam.isPresent()){
            Api api = apiRepository.findById(idParam.get()).orElse(null);
            apis = (api != null) ? List.of(api) : new ArrayList<>();
        } else {
            apis = searchParam.map(param -> apiRepository.getContainingQuote(param))
                    .orElse(apiRepository.findAll());
        }

        return new ResponseEntity<>(apis, HttpStatus.OK);
    }

    //"addApi" This method adds a new API.
    //Test request POST http://localhost:8080/api (adds new api)
    @PostMapping
    public ResponseEntity<Api> addApi(@RequestBody Api api) {
        Api savedApi = apiRepository.save(api);
        return new ResponseEntity<>(savedApi, HttpStatus.CREATED);
    }

    //"updateApi" This method updates an existing API.
    //Test request PUT http://localhost:8080/api/{apiID} (allow to change exising api)
    @PutMapping("/{apiID}")
    public ResponseEntity<Api> updateApi(@PathVariable("apiID") Long id, @RequestBody Api newApi) {
        Optional<Api> optionalApi = apiRepository.findById(id);

        if (optionalApi.isPresent()) {
            Api apiToUpdate = optionalApi.get();
            apiToUpdate.setName(newApi.getName());
            apiToUpdate.setVersion(newApi.getVersion());
            apiToUpdate.setSpec(newApi.getSpec());
            Api updatedApi = apiRepository.save(apiToUpdate);
            return new ResponseEntity<>(updatedApi, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //"deleteApi" This method deletes an API by its ID.
    //Test request DELETE http://localhost:8080/api/{apiID} (removes exising api)
    @DeleteMapping("/{apiID}")
    public ResponseEntity<Void> deleteApi(@PathVariable("apiID") Long id) {
        if (apiRepository.existsById(id)) {
            apiRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to parse OpenAPI
    @GetMapping("/parseFromDB/{id}")
    public OpenAPI parseFromDB(@PathVariable Long id) {
        return openAPIService.parseOpenAPIFromDatabase(id);
    }

    @GetMapping("/parseFromDB/{id}/paths")
    public Map<String, PathItem> parsePathsFromDB(@PathVariable Long id) {
        return openAPIService.parsePathsFromDatabase(id);
    }

}
