package com.scoobyTech;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

public class ExampleOverlay extends Overlay {

    private final Client client;
    private ExamplePlugin plugin;

    @Inject
    private ExampleOverlay(Client client, ExamplePlugin plugin) {
        this.client = client;
        this.plugin = plugin;

        System.out.println("Made it here");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return null;
    }
}
