package com.scoobyTech;

import net.runelite.api.*;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.*;
import javax.inject.Inject;
import java.awt.*;

public class KittyScapeOverlay extends Overlay {
    private final KittyScapePlugin plugin;
    private final SkillIconManager slm;

    @Inject
    private Client client;

    @Inject
    public KittyScapeOverlay(KittyScapePlugin plugin, Client client, SkillIconManager slm) {
        super(plugin);

        this.plugin = plugin;
        this.slm = slm;
        this.client = client;

        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.LOW);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        return null;
    }


}
