package ua.APIexplorer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.APIexplorer.entity.Api;
import ua.APIexplorer.repository.ApiRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiRepository apiRepository;


    //"getApi" This method retrieves a list of all APIs or searches for APIs containing a specific word in their name.
    //If a "name" parameter is provided, it calls getContainingQuote from ApiRepository to search for APIs by name.
    //If no "name" parameter is provided, it calls findAll from JpaRepository to retrieve all APIs.
    @GetMapping
    public ResponseEntity<List<Api>> getApi(@RequestParam("name") Optional<String> searchParam) {
        List<Api> apis = searchParam.map(param -> apiRepository.getContainingQuote(param))
                .orElse(apiRepository.findAll());
        return new ResponseEntity<>(apis, HttpStatus.OK);
    }


    //"readApi" This method retrieves a specific API by its ID.
    //It uses findById from JpaRepository to find the API by ID.
    @GetMapping("/{apiID}")
    public ResponseEntity<String> readApi(@PathVariable("apiID") Long id) {
        return apiRepository.findById(id)
                .map(api -> new ResponseEntity<>(api.getName(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //"addApi" This method adds a new API.
    //It creates a new Api object, sets its name, and saves it using save from JpaRepository.
    @PostMapping
    public ResponseEntity<Api> addApi(@RequestBody String api) {
        Api a = new Api();
        a.setName(api);
        Api savedApi = apiRepository.save(a);
        return new ResponseEntity<>(savedApi, HttpStatus.CREATED);
    }

    //"updateApi" This method updates an existing API.
    //It retrieves the existing API by ID, updates its fields, and saves the changes.
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
    //It checks if the API exists by ID and then deletes it using deleteById.
    @DeleteMapping("/{apiID}")
    public ResponseEntity<Void> deleteApi(@PathVariable("apiID") Long id) {
        if (apiRepository.existsById(id)) {
            apiRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
