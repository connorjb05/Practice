package net.syphlex.practice.manager.match;

import lombok.Getter;
import net.syphlex.practice.manager.profile.objects.InventorySnapshot;
import net.syphlex.practice.util.TtlHashMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class MatchInventories {
    private final TtlHashMap<UUID, InventorySnapshot> inventories = new TtlHashMap<>(TimeUnit.MINUTES, 10);
}
