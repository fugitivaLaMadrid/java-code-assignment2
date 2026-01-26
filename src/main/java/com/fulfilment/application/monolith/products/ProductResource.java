package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.List;

@Path("product")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ProductResource {
  private static final Logger LOGGER = Logger.getLogger(ProductResource.class.getName());
  private final ProductRepository productRepository;

  //Constructor Injection
  public ProductResource(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GET
  public List<Product> get() {
    return productRepository.listAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  public Product getSingle(Long id) {
    Product entity = productRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id " + id + " does not exist.", 404);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(Product product) {
    if (product.id != null) {
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }
    productRepository.persist(product);
    return Response.status(201).entity(product).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Product update(Long id, Product product) {
    Product entity = productRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id " + id + " does not exist.", 404);
    }

    entity.name = product.name;
    entity.description = product.description;
    entity.price = product.price;
    entity.stock = product.stock;

    productRepository.persist(entity);
    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(Long id) {
    Product entity = productRepository.findById(id);
    if (entity == null) {
      throw new WebApplicationException("Product with id " + id + " does not exist.", 404);
    }
    productRepository.delete(entity);
    return Response.status(204).build();
  }

  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {
    private final ObjectMapper objectMapper;

    public ErrorMapper() {
      this.objectMapper = new ObjectMapper();
    }

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);
      int code = 500;
      if (exception instanceof WebApplicationException wae) {
        code = wae.getResponse().getStatus();
      }


      ObjectNode json = objectMapper.createObjectNode();
      json.put("exceptionType", exception.getClass().getName());
      json.put("code", code);
      json.put("error", exception.getMessage());

      return Response.status(code).entity(json).build();
    }
  }
}
