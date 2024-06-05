package ua.APIexplorer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.APIexplorer.entity.Api;

import java.util.List;

public interface ApiRepository extends JpaRepository<Api, Long> {

    @Query("SELECT a FROM Api a WHERE a.name LIKE %?1%")
    List<Api> getContainingQuote(String word);

}
