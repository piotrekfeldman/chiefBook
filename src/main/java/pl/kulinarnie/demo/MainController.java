package pl.kulinarnie.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
public class MainController {

    private CategoryRepository categoryRepository;
    private RecipeRepository recipeRepository;

    public MainController(CategoryRepository categoryRepository, RecipeRepository recipeRepository) {
        this.categoryRepository = categoryRepository;
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "home";
    }


    @GetMapping("/allrecipies")
    public String allRecipies(Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        List<Recipe> recipeListAll = recipeRepository.findAll();
        model.addAttribute("recipies", recipeListAll);

        return "allRecipies";
    }


    @GetMapping("/find")
    public String search(Model model, @RequestParam String keyword) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        List<Recipe> recipeListAll = recipeRepository.findAllByKeyword(keyword);
        if (recipeListAll.isEmpty()) {

            String keywordFirstLetter = keyword.substring(0, 1);
            String keywordFirstLetterCaps = keywordFirstLetter.toUpperCase();
            String key = keywordFirstLetterCaps.concat(keyword.substring(1, keyword.length()));
            recipeListAll = recipeRepository.findAllByKeyword(key);
        }
        if (keyword != null) {
            model.addAttribute("recipies", recipeListAll);
        }
        return "allRecipies";
    }


    @GetMapping("/category")
    public String getRecipies(Model model, @RequestParam(defaultValue = "5") Long id) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);


        List<Recipe> recipeList = null;

        if (recipeRepository.findAllByCategory_Id(id).isEmpty()) {
            return "noRecipeAvailable";
        } else {
            if (id != 5) {
                recipeList = recipeRepository.findAllByCategory_Id(id);

            } else {
                recipeList = recipeRepository.findAll();
            }
            model.addAttribute("recipies", recipeList);
        }

        Category category = recipeList.get(0).getCategory();
        model.addAttribute("category", category);
        model.addAttribute("deleteRecipe", new Recipe());
        return "recipies";
    }


    @GetMapping("/addRecipe")
    public String addRecipeForm(Model model) {

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("recipe", new Recipe());
        return "addRecipeForm";
    }

    @PostMapping("/addRecipe")
    public String add(Recipe recipe) {

        recipeRepository.save(recipe);

        return "redirect:/";
    }


    @PostMapping("/delete")
    private String deleteRecipe(@RequestParam Long id) {
        System.out.println(id);
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        recipeRepository.deleteById(id);


        return "redirect:/category?id=" + recipeOpt.get().getCategory().getId();
    }

    @PostMapping("/like")
    private String likeRecipe(@RequestParam Long id) {
        Recipe recipe = recipeRepository.getById(id);
        recipe.setNumberOfLikes(recipe.getNumberOfLikes() + 1);
        recipeRepository.save(recipe);
        return "redirect:/category?id=" + recipe.getCategory().getId();
    }


    @GetMapping("/edit")
    private String editRecipe(@RequestParam Long id, Model model) {
        System.out.println(id);
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        for (Recipe recipe : recipeRepository.findAll()) {

            if (recipe.getId() == id) {
                model.addAttribute("edit", recipe);
                return "edit";
            }
        }
        return "redirect:/category?id=" + recipeOpt.get().getCategory().getId();
    }

    @PostMapping("/editRecipe")
    public String edit(Recipe input) {
        for (Recipe recipe : recipeRepository.findAll()) {
            if (input.getId() == recipe.getId()) {
                recipe.setName(input.getName());
                recipe.setCategory(input.getCategory());
                recipe.setDescription(input.getDescription());
                recipeRepository.save(recipe);
            }
        }
        return "redirect:/category?id=" + input.getCategory().getId();
    }
}