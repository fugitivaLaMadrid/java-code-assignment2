package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateWarehouseUseCaseUnitTest {

    WarehouseStore store = mock(WarehouseStore.class);
    LocationResolver resolver = mock(LocationResolver.class);
    CreateWarehouseUseCase useCase =
            new CreateWarehouseUseCase(store, resolver);



    @Test
    void shouldCreateWarehouse() {
        Warehouse w = new Warehouse();
        w.setLocation( "ZWOLLE-001");
        w.setCapacity(10);
        w.setStock(5);

        Location location = new Location("ZWOLLE-001", 5, 100);

        when(resolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(location);
        when(store.countByLocation("ZWOLLE-001")).thenReturn(0L);
        when(store.sumCapacityByLocation("ZWOLLE-001")).thenReturn(0);

        useCase.create(w);

        verify(store).create(w);
    }

    @Test
    void shouldRejectIfLocationInvalid() {
        Warehouse w = new Warehouse();
        w.setLocation("INVALID");
        w.setCapacity(10);
        w.setStock(5);

        when(resolver.resolveByIdentifier("INVALID"))
                .thenThrow(new RuntimeException("not found"));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(w));
    }

    @Test
    void shouldRejectIfCapacityTooLargeForLocation() {
        Warehouse w = new Warehouse();
        w.setLocation("ZWOLLE-001");
        w.setCapacity( 90);
        w.setStock(10);

        Location location = new Location("ZWOLLE-001", 5, 80);

        when(resolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(location);
        when(store.countByLocation("ZWOLLE-001")).thenReturn(0L);
        when(store.sumCapacityByLocation("ZWOLLE-001")).thenReturn(10);

        assertThrows(IllegalArgumentException.class,
                () -> useCase.create(w));
    }

}

