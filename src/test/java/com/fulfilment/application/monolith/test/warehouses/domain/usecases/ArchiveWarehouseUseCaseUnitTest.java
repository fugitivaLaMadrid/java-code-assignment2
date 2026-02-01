package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ArchiveWarehouseUseCaseUnitTest {

    private WarehouseStore store;
    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        useCase = new ArchiveWarehouseUseCase(store);
    }

    @Test
    void shouldArchiveWarehouse() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode( "WH-001");

        // Mock the store to return the warehouse
        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        // Call the use case
        useCase.archiveByBusinessUnitCode("WH-001");

        // Verify that archive() is called on the store
        verify(store).archiveByBusinessUnitCode(w.getBusinessUnitCode());
    }

    @Test
    void shouldFailWhenWarehouseDoesNotExist() {
        when(store.findByBusinessUnitCode("WH-999")).thenReturn(null);

        Exception ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.archiveByBusinessUnitCode("WH-999")
        );

        assertEquals("Warehouse not found", ex.getMessage());
        verify(store, never()).archiveByBusinessUnitCode(any());
    }


    @Test
    void shouldAllowArchiveIfAlreadyArchived() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("WH-001");
        w.setArchivedAt(ZonedDateTime.now());

        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        // should NOT throw
        useCase.archiveByBusinessUnitCode("WH-001");

        verify(store).archiveByBusinessUnitCode(w.getBusinessUnitCode());
    }

//    @Test
//    void shouldRejectIfAlreadyArchived() {
//        Warehouse w = new Warehouse();
//        w.businessUnitCode = "WH-002";
//        w.archivedAt = java.time.ZonedDateTime.now();
//
//        when(store.findByBusinessUnitCode("WH-002")).thenReturn(w);
//
//        Exception ex = assertThrows(IllegalArgumentException.class,
//                () -> useCase.archiveByBusinessUnitCode("WH-002"));
//
//        assertEquals("Warehouse not found or archived", ex.getMessage());
//        verify(store, never()).archive(any());
//    }
}
