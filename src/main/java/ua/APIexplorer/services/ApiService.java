package ua.APIexplorer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.APIexplorer.entity.Api;
import ua.APIexplorer.repository.ApiRepository;

public class ApiService {

    @Autowired
    private ApiRepository repository;

    public Api saveApi(String name, String version, String specJson) {
        Api api = new Api();
        api.setName(name);
        api.setVersion(version);
        api.setSpec(specJson); // Ensure this is a proper JSON string

        return repository.save(api);
    }
}
