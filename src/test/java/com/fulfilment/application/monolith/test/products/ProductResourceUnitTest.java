package com.fulfilment.application.monolith.test.products;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.products.ProductResource;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductResourceUnitTest {

  private ProductRepository repository;
  private ProductResource resource;

  @BeforeEach
  void setup() {
    repository = mock(ProductRepository.class);
    resource = new ProductResource(repository);
  }

  @Test
  void testGetAllProducts() {
    Product p1 = new Product("TONSTAD");
    p1.id = 1L;
    Product p2 = new Product("KALLAX");
    p2.id = 2L;

    when(repository.listAll(any())).thenReturn(Arrays.asList(p1, p2));

    List<Product> result = resource.get();

    assertEquals(2, result.size());
    assertEquals("TONSTAD", result.get(0).name);
    assertEquals("KALLAX", result.get(1).name);
  }

  @Test
  void testGetSingleProduct_success() {
    Product p = new Product("BESTÅ");
    p.id = 3L;

    when(repository.findById(3L)).thenReturn(p);

    Product result = resource.getSingle(3L);

    assertNotNull(result);
    assertEquals("BESTÅ", result.name);
    assertEquals(3L, result.id);
  }

  @Test
  void testGetSingleProduct_notFound() {
    when(repository.findById(99L)).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
            () -> resource.getSingle(99L));

    assertEquals(404, ex.getResponse().getStatus());
  }

  @Test
  void testCreateProduct_success() {
    Product p = new Product("MALM");
    p.stock = 7;
    p.price = new BigDecimal("50.00");

    doAnswer(invocation -> {
      p.id = 4L; // simulate DB auto-generated ID
      return null;
    }).when(repository).persist(p);

    Response resp = resource.create(p);

    assertEquals(201, resp.getStatus());
    assertNotNull(((Product) resp.getEntity()).id);
    assertEquals("MALM", ((Product) resp.getEntity()).name);
  }

  @Test
  void testCreateProduct_invalidId() {
    Product p = new Product("INVALID");
    p.id = 99L;

    WebApplicationException ex = assertThrows(WebApplicationException.class,
            () -> resource.create(p));

    assertEquals(422, ex.getResponse().getStatus());
  }

  @Test
  void testUpdateProduct_success() {
    Product existing = new Product("KALLAX");
    existing.id = 2L;

    Product updated = new Product("KALLAX_NEW");
    updated.stock = 8;
    updated.price = new BigDecimal("30.00");

    when(repository.findById(2L)).thenReturn(existing);

    Product result = resource.update(2L, updated);

    assertEquals("KALLAX_NEW", result.name);
    assertEquals(8, result.stock);
  }

  @Test
  void testUpdateProduct_notFound() {
    Product updated = new Product("NON_EXISTENT");

    when(repository.findById(999L)).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
            () -> resource.update(999L, updated));

    assertEquals(404, ex.getResponse().getStatus());
  }

  @Test
  void testDeleteProduct_success() {
    Product p = new Product("BESTÅ");
    p.id = 3L;

    when(repository.findById(3L)).thenReturn(p);

    Response resp = resource.delete(3L);

    verify(repository).delete(p);
    assertEquals(204, resp.getStatus());
  }

  @Test
  void testDeleteProduct_notFound() {
    when(repository.findById(999L)).thenReturn(null);

    WebApplicationException ex = assertThrows(WebApplicationException.class,
            () -> resource.delete(999L));

    assertEquals(404, ex.getResponse().getStatus());
  }
}
