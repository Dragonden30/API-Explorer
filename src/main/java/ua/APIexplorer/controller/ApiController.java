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

    @GetMapping
    public ResponseEntity<List<Api>> getApi(@RequestParam("search") Optional<String> searchParam) {
        List<Api> apis = searchParam.map(param -> apiRepository.getContainingQuote(param))
                .orElse(apiRepository.findAll());
        return new ResponseEntity<>(apis, HttpStatus.OK);
    }

    @GetMapping("/{apiID}")
    public ResponseEntity<String> readApi(@PathVariable("apiID") Long id) {
        return apiRepository.findById(id)
                .map(api -> new ResponseEntity<>(api.getName(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Api> addApi(@RequestBody String api) {
        Api a = new Api();
        a.setName(api);
        Api savedApi = apiRepository.save(a);
        return new ResponseEntity<>(savedApi, HttpStatus.CREATED);
    }

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
