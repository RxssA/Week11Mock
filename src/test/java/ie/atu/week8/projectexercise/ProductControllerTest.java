package ie.atu.week8.projectexercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllProducts() throws Exception {
        List<Product> products = List.of(
                new Product(1L, "Pot", "10litres", 10000),
                new Product(2L, "Beer", "50litres", 8000)
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pot"))
                .andExpect(jsonPath("$[1].name").value("Pan"));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product = new Product(1L, "Pot", "10litres", 10000);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/product/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pot"));
    }

    @Test
    void testCreateProduct() throws Exception {
        Product product = new Product(1L, "Pot", "10litres", 10000);
        when(productService.saveProduct(product)).thenReturn((product));
        ObjectMapper mapper = new ObjectMapper();
        String jsonDetails = mapper.writeValueAsString(product);
        mockMvc.perform(post("/product").contentType("application/json").content(jsonDetails))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pot"));
    }

    @Test
    void updateProduct() throws Exception {
        Product existingProduct = new Product(1L, "Pot", "10litres", 10000);
        Product updatedProduct = new Product(1L, "Updated Pot", "15litres", 12000);

        when(productService.getProductById(1L)).thenReturn(Optional.of(existingProduct));
        when(productService.saveProduct(existingProduct)).thenReturn(updatedProduct);

        ObjectMapper mapper = new ObjectMapper();
        String jsonDetails = mapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType("application/json")
                        .content(jsonDetails))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pot"))
                .andExpect(jsonPath("$.description").value("15litres"))
                .andExpect(jsonPath("$.price").value(12000));
    }

    @Test
    void deleteProduct() throws Exception {
        Product product = new Product(1L, "Pot", "10litres", 10000);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        Product updatedProduct = new Product(1L, "Updated Pot", "15litres", 12000);

        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        ObjectMapper mapper = new ObjectMapper();
        String jsonDetails = mapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType("application/json")
                        .content(jsonDetails))
                .andExpect(status().isNotFound());
    }
}