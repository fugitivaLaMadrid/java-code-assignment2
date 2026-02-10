package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReplaceWarehouseUseCaseUnitTest {

    private WarehouseStore store;
    private LocationResolver resolver;
    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        resolver = mock(LocationResolver.class);
        useCase = new ReplaceWarehouseUseCase(store, resolver);
    }

    @Test
    void shouldRejectIfWarehouseDoesNotExist() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("B1");
        when(store.findByBusinessUnitCode("B1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(w));
    }

    @Test
    void shouldRejectIfStockMismatch() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("B2");
        existing.setStock(5);
        when(store.findByBusinessUnitCode("B2")).thenReturn(existing);

        Warehouse newW = new Warehouse();
        newW.setBusinessUnitCode("B2");
        newW.setStock(3);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newW));
    }

    @Test
    void shouldReplaceWarehouseSuccessfully() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("B3");
        existing.setStock(10);
        existing.setLocation("ZWOLLE-001");

        Warehouse newW = new Warehouse();
        newW.setBusinessUnitCode("B3");
        newW.setStock(10);
        newW.setLocation("ZWOLLE-001");
        newW.setCapacity(40);

        Location loc = new Location("ZWOLLE-001", 5, 100);

        when(store.findByBusinessUnitCode("B3")).thenReturn(existing);
        when(resolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(loc);

        useCase.replace(newW);

        verify(store).archiveByBusinessUnitCode("B3");
        verify(store).create(newW);
    }
}
