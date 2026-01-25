package com.fulfilment.application.monolith.stores;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/stores")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

  private static final String STORE_WITH_ID = "Store with id %d does not exist";

  private final StoreRepository storeRepository;
  private final Event<StoreSavedEvent> storeSavedEvent;

  // ---------- Constructor injection ----------
  @Inject
  public StoreResource(StoreRepository storeRepository,
                       Event<StoreSavedEvent> storeSavedEvent) {
    this.storeRepository = storeRepository;
    this.storeSavedEvent = storeSavedEvent;
  }

  // ---------- GET ALL ----------
  @GET
  public List<Store> getAll() {
    return storeRepository.listAll(Sort.by("name"));
  }

  // ---------- GET ONE ----------
  @GET
  @Path("{id}")
  public Store getSingle(@PathParam("id") Long id) {
    return storeRepository.findByIdOptional(id)
            .orElseThrow(() -> new WebApplicationException(
                    String.format(STORE_WITH_ID, id), 404
            ));
  }

  // ---------- CREATE ----------
  @POST
  @Transactional
  public Response create(Store store) {
    if (store.getId() != null) {
      throw new WebApplicationException("Id must not be set when creating a Store", 422);
    }

    storeRepository.persist(store);

    storeSavedEvent.fire(new StoreSavedEvent(store.getId(), StoreSavedEvent.Action.CREATE));

    return Response.status(Response.Status.CREATED).entity(store).build();
  }

  // ---------- UPDATE ----------
  @PUT
  @Path("{id}")
  @Transactional
  public Store update(@PathParam("id") Long id, Store updatedStore) {
    Store entity = storeRepository.findByIdOptional(id)
            .orElseThrow(() -> new WebApplicationException(
                    String.format(STORE_WITH_ID, id), 404
            ));

    if (updatedStore.getName() == null) {
      throw new WebApplicationException("Store name must be provided", 422);
    }

    entity.setName(updatedStore.getName());
    entity.setQuantityProductsInStock(updatedStore.getQuantityProductsInStock());

    storeSavedEvent.fire(new StoreSavedEvent(entity.getId(), StoreSavedEvent.Action.UPDATE));

    return entity;
  }

  // ---------- PATCH ----------
  @PATCH
  @Path("{id}")
  @Transactional
  public Store patch(@PathParam("id") Long id, Store updatedStore) {
    Store entity = storeRepository.findByIdOptional(id)
            .orElseThrow(() -> new WebApplicationException(
                    String.format(STORE_WITH_ID, id), 404
            ));

    if (updatedStore.getName() != null) {
      entity.setName(updatedStore.getName());
    }

    if (updatedStore.getQuantityProductsInStock() != 0) {
      entity.setQuantityProductsInStock(updatedStore.getQuantityProductsInStock());
    }

    storeSavedEvent.fire(new StoreSavedEvent(entity.getId(), StoreSavedEvent.Action.UPDATE));

    return entity;
  }

  // ---------- DELETE ----------
  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(@PathParam("id") Long id) {
    Store entity = storeRepository.findByIdOptional(id)
            .orElseThrow(() -> new WebApplicationException(
                    String.format(STORE_WITH_ID, id), 404
            ));

    storeRepository.delete(entity);

    return Response.noContent().build();
  }
}