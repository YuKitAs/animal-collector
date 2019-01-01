package yukitas.animal.collector.controller;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import yukitas.animal.collector.controller.dto.CreateCategoryRequest;
import yukitas.animal.collector.model.Category;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryControllerTest extends AbstractControllerTest {

    @Test
    public void getAllCategories() {
        ResponseEntity<Category[]> response = getTestRestTemplate().getForEntity("/categories", Category[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).satisfies(categories -> {
            assertThat(categories.length).isEqualTo(2);
            assertThat(categories[0].getId()).isEqualTo(CATEGORY_CAT_ID);
            assertThat(categories[0].getName()).isEqualTo(CATEGORY_CAT_NAME);
            assertThat(categories[1].getId()).isEqualTo(CATEGORY_DOG_ID);
            assertThat(categories[1].getName()).isEqualTo(CATEGORY_DOG_NAME);
        });
    }

    @Test
    public void createCategory() throws Exception {
        ResponseEntity<Category> response = getTestRestTemplate().postForEntity("/categories",
                getFixture("create-category.json", CreateCategoryRequest.class), Category.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).satisfies(category -> {
            assertThat(category.getId()).isNotNull();
            assertThat(category.getName()).isEqualTo(CATEGORY_HAMSTER_NAME);
        });
    }

    @Test
    public void updateCategory() {
    }

    @Test
    public void deleteCategory() {
        ResponseEntity<Void> response = getTestRestTemplate().exchange("/categories/" + CATEGORY_DOG_ID,
                HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}