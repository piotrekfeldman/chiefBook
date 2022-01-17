package pl.kulinarnie.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findAll();

    List<Recipe> findAllByCategory_Id(long id);

    @Query("SELECT r FROM Recipe r WHERE CONCAT(r.name, ' ' , r.description) LIKE %?1%")
    List<Recipe> findAllByKeyword(String keyword);

    @Override
    void deleteById(Long aLong);
}
