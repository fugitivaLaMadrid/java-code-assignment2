package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.GetWarehouseByIdUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetWarehouseByIdUseCaseUnitTest {

    private WarehouseStore store;
    private GetWarehouseByIdUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        useCase = new GetWarehouseByIdUseCase(store);
    }

    @Test
    void getById_success() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "WH-001";
        w.location = "AMSTERDAM";
        w.capacity = 100;
        w.stock = 50;

        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        Warehouse result = useCase.getById("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.businessUnitCode);
        assertEquals("AMSTERDAM", result.location);
        assertEquals(100, result.capacity);
        assertEquals(50, result.stock);
    }

    @Test
    void getById_notFound() {
        when(store.findByBusinessUnitCode("WH-999")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.getById("WH-999"));

        assertEquals("Warehouse not found or archived", ex.getMessage());
    }

    @Test
    void getById_archived() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "WH-002";
        w.archivedAt = java.time.ZonedDateTime.now();

        when(store.findByBusinessUnitCode("WH-002")).thenReturn(w);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.getById("WH-002"));

        assertEquals("Warehouse not found or archived", ex.getMessage());
    }
}
