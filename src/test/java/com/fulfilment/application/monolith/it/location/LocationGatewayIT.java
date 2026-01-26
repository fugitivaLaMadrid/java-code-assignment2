package com.fulfilment.application.monolith.it.location;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class LocationGatewayIT {

  @Inject
  LocationGateway locationGateway;

  @Test
  void resolveExistingLocation_shouldReturnLocation() {
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.getIdentification());
  }

  @Test
  void resolveAnotherExistingLocation_shouldReturnLocation() {
    Location location = locationGateway.resolveByIdentifier("AMSTERDAM-002");

    assertEquals("AMSTERDAM-002", location.getIdentification());
  }

  @Test
  void resolveNonExistingLocation_shouldThrow() {
    UnsupportedOperationException ex =
            assertThrows(UnsupportedOperationException.class,
                    () -> locationGateway.resolveByIdentifier("UNKNOWN-001"));

    assertTrue(ex.getMessage().contains("UNKNOWN-001"));
  }

  @Test
  void resolveNullIdentifier_shouldThrow() {
    assertThrows(UnsupportedOperationException.class,
            () -> locationGateway.resolveByIdentifier(null));
  }
}