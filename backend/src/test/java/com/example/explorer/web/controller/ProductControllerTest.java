package com.example.explorer.web.controller;

import com.example.explorer.domain.service.ProductService;
import com.example.explorer.security.JwtService;
import com.example.explorer.security.MockUserDetailsService;
import com.example.explorer.web.dto.PageResponse;
import com.example.explorer.web.dto.ProductRequest;
import com.example.explorer.web.dto.ProductResponse;
import com.example.explorer.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private MockUserDetailsService mockUserDetailsService;

    @Test
    void listProducts_returns200() throws Exception {
        PageResponse<ProductResponse> page = new PageResponse<>(List.of(
                new ProductResponse(1L, "SKU-1", "Test", new BigDecimal("10.0"), 5, 1L, "Cat")
        ), 0, 10, 1, 1);
        
        when(productService.findAll(null, null, 0, 10)).thenReturn(page);
        
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value("SKU-1"));
    }

    @Test
    void createProduct_returns201() throws Exception {
        ProductResponse response = new ProductResponse(1L, "SKU-1", "Test", new BigDecimal("10.0"), 5, 1L, "Cat");
        
        when(productService.create(any(ProductRequest.class))).thenReturn(response);
        
        String json = """
                {
                    "sku": "SKU-1",
                    "name": "Test",
                    "price": 10.0,
                    "stock": 5,
                    "categoryId": 1
                }
                """;
                
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-1"));
    }

    @Test
    void createProduct_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Error de validación"));
    }
}
