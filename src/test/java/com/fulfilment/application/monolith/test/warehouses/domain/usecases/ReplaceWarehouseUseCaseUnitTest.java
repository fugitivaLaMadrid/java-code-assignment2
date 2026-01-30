package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseUnitTest {

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
    void shouldRejectIfNoExisting() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "B1";
        when(store.findByBusinessUnitCode("B1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(w));
    }

    @Test
    void shouldRejectIfStockMismatch() {
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "B2";
        existing.stock = 5;
        when(store.findByBusinessUnitCode("B2")).thenReturn(existing);

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "B2";
        newW.stock = 3; // mismatch

        assertThrows(IllegalArgumentException.class, () -> useCase.replace(newW));
    }
    @Test
    void shouldReplaceWarehouseSuccessfully() {
        // given
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "1";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 40;
        warehouse.stock = 10;

        when(store.findByBusinessUnitCode("1")).thenReturn(warehouse);
        when(resolver.resolveByIdentifier("ZWOLLE-001"))
                .thenReturn(new Location("ZWOLLE-001", 5, 100));

        // when
        useCase.replace(warehouse);

        // then
        verify(store).update(warehouse);
    }
}
