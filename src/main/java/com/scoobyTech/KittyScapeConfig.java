package com.scoobyTech;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface KittyScapeConfig extends Config {
    @ConfigItem(
            keyName = "catifyPlayer",
            name = "Catify Player",
            description = "Catify your player",
            position = 0
    )
    default boolean catifyPlayer() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyOthers",
            name = "Catify Other Players",
            description = "Catify all other players",
            position = 1
    )
    default boolean catifyOthers() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyNPCs",
            name = "Catify NPCs",
            description = "Catify NPCs",
            position = 2
    )
    default boolean catifyNPCs() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyInventory",
            name = "Catify Inventory",
            description = "Catify Inventory",
            position = 3
    )
    default boolean catifyInventory() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyShops",
            name = "Catify Shops",
            description = "Catify all the items in shops",
            position = 4
    )
    default boolean  catifyShops() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyGameTabs",
            name = "Catify Game Tabs",
            description = "Catify the game tabs, e.g. Inventory, Prayer, Friends list...",
            position = 5
    )
    default boolean catifyGameTabs() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyProjectiles",
            name = "Catify Projectiles",
            description = "Catify all projectiles",
            position = 6
    )
    default boolean catifyProjectiles() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyAttackedNPCs",
            name = "Catify Attacked NPCs",
            description = "Catify NPCs as you attack them. Since they are now cats you can no longer attack them!!",
            position = 6
    )
    default boolean catifyAttackedNPCs() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyChatHeads",
            name = "Catify Chat Heads",
            description = "Catify all chat heads when talking to NPCs",
            position = 7
    )
    default boolean catifyChatHeads() {
        return false;
    }

    @ConfigItem(
            keyName = "catifyEverything",
            name = "Catify Everything",
            description = "Catify everything. Leads to chaos. Not sorry.",
            position = 8
    )
    default boolean catifyEverything() {
        return false;
    }

    @ConfigItem(
            keyName = "modelID",
            name = "Model ID",
            description = "Projectile animation model ID. 10,047 is default for the ghost cat thing",
            position = 10
    )
    default int modelID() {
        return 10047;
    }
}
