package com.scoobyTech;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

public class KittyProjectiles {
    // custom cat shooting stuff!!
    private RuneLiteObject rlObject;
    private Client client;
    private Player player;
    private Model model;
    private LocalPoint endPoint = new LocalPoint(0, 0);

    public static int modelID = 10047;

    public static boolean shootingKitty = false;

    public static List<KittyProjectiles> kitties = new ArrayList<KittyProjectiles>();

    private boolean catified = false;

    public KittyProjectiles(Client client) {
        this.client = client;
        kitties.add(this);
        model = client.loadModel(modelID);
    }

    // Spawn the kitty in the world with the same orientation as our player
    public void spawn(WorldPoint position, int orientation) {
        player = client.getLocalPlayer();

        LocalPoint localPosition = LocalPoint.fromWorld(client, position);

        if (player != null && client.getPlane() == position.getPlane()) {

            // 36374 = cat statue for catify
            // 10047 = ghostly cat
            // 9413 = sitting black cat

            // 3006 jus
            // 32227 default head
            // 23151 toy cat, full cat model!!

            rlObject = client.createRuneLiteObject();
            rlObject.setModel(model);
            rlObject.setOrientation(orientation);
            rlObject.setLocation(localPosition, position.getPlane());
            //rlObject.setActive(true);

            shootingKitty = true;
        }
    } // end of spawn

    // Remember to despawn the objects, otherwise they get funky on the screen
    public void despawn()
    {
        if (rlObject != null) {
            rlObject.setActive(false);
        }
    }

    public void update(WorldPoint position, int orientation)
    {
        LocalPoint localPosition = LocalPoint.fromWorld(client, position);
        if (rlObject != null) {
            rlObject.setLocation(localPosition, position.getPlane());
            rlObject.setOrientation(orientation);
        }
    }

    // Update kitty projectile. Turns off the object once it has reached the endpoint.
    public void updateProjectile(int x, int y, int height, int plane)
    {
        if (shootingKitty) {
            LocalPoint newPos = new LocalPoint(x, y);
            rlObject.setModelHeight(height);
            rlObject.setLocation(newPos, plane);
        }

        if (rlObject.getLocation().distanceTo(player.getLocalLocation()) > 1) rlObject.setActive(true);

        if (rlObject.getLocation().distanceTo(endPoint) <= 1 && shootingKitty)
        {
            rlObject.setActive(false);
        }
    }

    // Turn the tile into a statue. Used for the projectiles logic. Can/probably should be to be modified
    // to happen only when the target NPC is dead. It's a "feature" at the moment.
    public void catify(LocalPoint catifyTile, int plane) {
        System.out.println("Spawning statue..");
        final int STATUE_ID = 36374;
        RuneLiteObject catObj = client.createRuneLiteObject();
        catObj.setLocation(catifyTile, plane);
        catObj.setModel(client.loadModel(STATUE_ID));
        catObj.setActive(true);
        catified = true;
    }

    public boolean isCat()
    {
        return catified;
    }
    public void kittyProjectileEndpoint(LocalPoint target)
    {
        this.endPoint = target;
    }



    public boolean isActive()
    {
        return rlObject.isActive();
    }

    public LocalPoint getLocalLocation() { return rlObject.getLocation(); }
}
