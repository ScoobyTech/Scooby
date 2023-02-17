package com.scoobyTech;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.*;
import java.util.List;


@Slf4j
@PluginDescriptor(
        name = "KittyScape",
        description = "Catifies many things around the game."
)

public class KittyScapePlugin extends Plugin {

    @Inject
    private KittyScapeConfig config;

    @Inject
    private Hooks hooks;

    @Inject
    private KittyScapeOverlay overlay;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    private int ticks = 0;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    public static Player player;

    // Yes I put these here. Fight me.
    public static final int[] CAT_IDS = new int[]{1561, 1562, 1563, 1564, 1565,
                                           1566, 1567, 1567, 1567, 1568,
                                           1569, 1570, 1571, 1572, 1491};
    public static final int[] CAT_MODEL_IDS = new int[]{395, 1619, 1620, 1621, 1622,
                                                1623, 1624, 1625, 5604, 6668};

    public static final int[] CAT_CHATHEAD_IDS = new int[]{6689, 5590, 1619, 1620, 4780,
                                                    6662, 6663, 6664, 6665, 6666,
                                                    5600, 5601, 5602, 5603, 1626,
                                                    5588, 5589, 6690, 6691, 6695};


    // config settings
    private boolean catifyPlayer = false;
    private boolean catifyOthers = false;
    private boolean catifyNPCs = false;
    private boolean catifyInventory = false;
    private boolean catifyShops = false;
    private boolean catifyGameTabs = false;
    private boolean catifyProjectiles = false;
    private boolean catifyAttackedNPCs = false;
    private boolean catifyChatHeads = false;
    private boolean catifyEverything = false;
    private int modelID = 10047;

    private KittyProjectiles kl;
    private List<NPC> blacklistRendering = new ArrayList<>();

    public KittyScapePlugin() {
    }

    @Override
    protected void startUp() throws Exception {
        log.info("KittyScape Started");
        hooks.registerRenderableDrawListener(drawListener);;
        this.reload();
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("KittyScape Stopped");
        hooks.unregisterRenderableDrawListener(drawListener);
    }

    @Subscribe
    protected void onConfigChanged(ConfigChanged configChangedEvent) {
        this.reload();
    }

    /**
     * Reload config options
     */
    private void reload() {
        catifyPlayer = this.config.catifyPlayer();
        catifyOthers = this.config.catifyOthers();
        catifyNPCs = this.config.catifyNPCs();
        catifyInventory = this.config.catifyInventory();
        catifyShops = this.config.catifyShops();
        catifyGameTabs = this.config.catifyGameTabs();
        catifyProjectiles = this.config.catifyProjectiles();
        catifyAttackedNPCs = this.config.catifyAttackedNPCs();
        catifyChatHeads = this.config.catifyChatHeads();
        catifyEverything = this.config.catifyEverything();
        modelID = this.config.modelID();
        this.resetConfiguration();
    }

    // Different ID types for chat heads, npcs, models, etc, so need different lists
    private int getRandomCatID(int[] catIDsToUse) {
        Random r = new Random();
        int catID = r.nextInt(catIDsToUse.length);
        return catIDsToUse[catID];
    }

    // Catify our player, other players and NPCs. Soon(TM) to implement catifying NPCs properly.
    public void catifyPlayers() {
        List<Player> players = client.getPlayers();
        // We're the first player on the list of players
        if (catifyPlayer) {
            player = client.getLocalPlayer();
            player.getPlayerComposition().setTransformedNpcId(getRandomCatID(CAT_MODEL_IDS));
        }

        if (catifyOthers)
        {
            if (players.size() > 1) {
                // start at 1 since 0 is our player
                for (int i = 1; i < players.size(); i++) {
                    players.get(i).getPlayerComposition().setTransformedNpcId(getRandomCatID(CAT_MODEL_IDS));
                }
            }
        }

        // NPCs somewhat janky for now. Tried making it before I learned how to properly use runeliteobjects
        // so just leaves everything on the screen. Wild to watch. Clearly an intended "feature"
        if (catifyNPCs) {
            List<NPC> npcs = client.getNpcs();
            List<RuneLiteObject> ourNpcs = new ArrayList<RuneLiteObject>();

            Random r = new Random();
            for (NPC npc : npcs) {
                RuneLiteObject newNpc = client.createRuneLiteObject();
                Model model = players.get(r.nextInt(players.size())).getModel();
                newNpc.setModel(model);
                newNpc.setOrientation(npc.getOrientation());

                ourNpcs.add(newNpc);
            }

            for (int i = 0; i < ourNpcs.size(); i++) {
                RuneLiteObject obj = ourNpcs.get(i);

                obj.setLocation(npcs.get(i).getLocalLocation(), npcs.get(i).getWorldLocation().getPlane());
                obj.setActive(true);
            }
        }

    }

    // Catify the inventory
    public void catifyInventory() {
        catifyWidget(WidgetInfo.INVENTORY.getId(), false);
    }

    // Catify the shop stock
    public void catifyShop() {
        // GENERAL SHOP ID = 19660816, PARENT = 19660800
        // First widget is blank
        final int SHOP_WIDGET_ID = 19660816;
        catifyWidget(SHOP_WIDGET_ID, false);
    }

    // Catify the game tabs
    public void catifyTabs() {
        final int TOP_TABS = 35913789;
        final int BOTTOM_TABS = 35913773;

        catifyWidget(TOP_TABS, true);
        catifyWidget(BOTTOM_TABS, true);
    }

    // remember to use chathead models!!
    public void catifyChatDialogue() {
        final int CHAT_HEAD = 15138818;
        final int CHAT_NAME = 15138820;
        final int CHAT_TEXT = 15138822;

        Widget head = client.getWidget(CHAT_HEAD);
        if (head != null) {
            head.setModelId(getRandomCatID(CAT_CHATHEAD_IDS));

            Widget name = client.getWidget(CHAT_NAME);
            name.setText("Cat");

            Widget text = client.getWidget(CHAT_TEXT);
            text.setText("Meow!");
        }
    }


    // Used these before the recursive catifying. It's more tame since it only does static and normal widgets.
    public void catifyWidget(int widgetID, boolean isStatic) {
        Widget tabs = client.getWidget(widgetID);

        if (tabs != null) {
            Widget[] widgetChildren;
            if (!isStatic) {
                widgetChildren = tabs.getChildren();
            } else {
                widgetChildren = tabs.getStaticChildren();
            }

            if (widgetChildren != null && widgetChildren.length > 0) {
                for (Widget widget : widgetChildren) {
                    widget.setItemId(getRandomCatID(CAT_IDS));
                    widget.setItemQuantityMode(0);
                    widget.setText("Meow");
                }
            }
        }
    }

    public void catifyAllWidgets() {
        final int ALL_WIDGETS_ID = 35913728;

        Widget widgets = client.getWidget(ALL_WIDGETS_ID);

        if (widgets != null) {
            catifyWidget(widgets);
        }
    }

    // Replaces the first combat spell of normal spellbook with the catify spell.
    public void makeSpellCatifyWidget()
    {
        Widget spell = client.getWidget(14286855);

        if (spell != null)
        {
            spell.setName("Catify!!");
            spell.setText("Catify Target!!");
            spell.setItemId(CAT_IDS[0]);
            spell.setItemQuantityMode(0);
            spell.setItemQuantity(0);
        }
    }

    // recursively catify -every- widget in the game. Absolutely chaotic. It's a "feature" :123:
    public void catifyWidget(Widget widget) {
        if (widget != null) {
            if (widget.getText() != null && widget.getText().length() > 0) {
                widget.setText("Meow!");
            }
            widget.setItemId(getRandomCatID(CAT_IDS));
            widget.setItemQuantityMode(0);
            widget.setItemQuantity(0);
            widget.setName("Meow!");
            if (widget.getChildren() != null) {
                for (Widget child : widget.getChildren()) {
                    catifyWidget(child);
                }
            }

            if (widget.getStaticChildren() != null) {
                for (Widget child : widget.getStaticChildren()) {
                    catifyWidget(child);
                }
            }

            if (widget.getDynamicChildren() != null) {
                for (Widget child : widget.getDynamicChildren()) {
                    catifyWidget(child);
                }
            }
            if (widget.getNestedChildren() != null) {
                for (Widget child : widget.getNestedChildren()) {
                    catifyWidget(child);
                }
            }
        }
    }

    // Not yet implemented.
    public void catifyGameObjects() {
        List<GameObject> allObjs = new ArrayList<GameObject>();
        Tile[][][] tiles = client.getScene().getTiles();

        for (int x = 0; x < tiles.length; x++) {
            for(int y = 0; y < tiles.length; y++) {
                for (int z = 0; z < tiles.length; z++) {
                    if (tiles[x][y][z] != null) {
                        GameObject[] obs = tiles[x][y][z].getGameObjects();

                        if (obs != null && obs.length > 0) {
                            for (GameObject object : obs)
                                allObjs.add(object);
                        }
                    }
                }
            }
        }

        //System.out.println("Found Objects: " + allObjs.size());
        for (int x = 0; x < allObjs.size(); x++) {
            GameObject obj = allObjs.get(x);

            if (obj != null) {
                System.out.println(obj.getX());
            }
        }
    }
    public void catifyTiles() {

    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        player = client.getLocalPlayer();

        if (player != null && kl == null) {
            kl = new KittyProjectiles(client);
        }

        // Don't ask.
        //ticks++;

        // Used this to show off the magic projectiles being catified
        //makeSpellCatifyWidget();

        //catifyGameObjects();
        //catifyAllWidgets();
        if (catifyPlayer || catifyOthers || catifyNPCs) { catifyPlayers(); }
        if (catifyShops) { catifyShop(); }
        if (catifyInventory) { catifyInventory(); }
        if (catifyGameTabs) { catifyTabs(); }
        if (catifyChatHeads) { catifyChatDialogue(); }

        if (catifyEverything)
        {
            catifyPlayer = true;
            catifyOthers = true;
            catifyNPCs = true;
            catifyInventory = true;
            catifyShops = true;
            catifyGameTabs = true;
            catifyProjectiles = true;
            catifyAttackedNPCs = true;
            catifyChatHeads = true;
            catifyAllWidgets();
        }

    }

    // Felt cute, decided to trace over the would-be projectile since they're spriteanims
    // and not easily modified
    @Subscribe
    public void onProjectileMoved(ProjectileMoved projectileMoved) {
        Projectile projectile = projectileMoved.getProjectile();

        // used wind bolt to test, sometimes uncomment it to feel cute and not replace all projectiles.
        //final int wind_bolt = 91;
       //if (projectile.getId() == wind_bolt) {

        if (catifyProjectiles) {

            kl.spawn(player.getWorldLocation(), player.getOrientation());
            kl.kittyProjectileEndpoint(projectile.getTarget());

            // if the projectile is same location as player, we need to shoot it and update it until
            // it reaches the end cycle.
            if (kl.getLocalLocation() == player.getLocalLocation()) {
                kl.shootingKitty = true;
            } else if (client.getGameCycle() >= projectile.getEndCycle()) {
                kl.shootingKitty = false;
                // the catify npc part of the projectile lol. Not perfect if the NPC is moving, but works most of
                // the time. Then adds the NPC to a rendering blacklist so the statue is visible at all times.
                if (catifyAttackedNPCs) {
                    kl.catify(projectile.getInteracting().getLocalLocation(), projectile.getFloor());
                    Actor blacklist = projectile.getInteracting();
                    blacklistRendering.add((NPC) blacklist);
                }
                // de-spawn all non-cat runeliteobjects. Need to do this before clearing the list otherwise it will
                // all stay on the screen and clutter.
                for (int i = 0; i < KittyProjectiles.kitties.size(); i++) {
                    if (!KittyProjectiles.kitties.get(i).isCat()) {
                        KittyProjectiles.kitties.get(i).despawn();
                    }
                }
                KittyProjectiles.kitties.clear();
            }

            // Saw at some point the location would randomly become 0,0. Fixed it somewhere along the way, who knows how
            if (projectile.getX() != 0.00) {
                kl.updateProjectile((int) projectile.getX(), (int) projectile.getY(), projectile.getHeight(),
                        projectile.getFloor());
            }
        }
    }

    @Provides
    KittyScapeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(KittyScapeConfig.class);
    }

    // updates the projectiles on the frame ticks and not game ticks
    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        if (kl != null) {
            kl.update(player.getWorldLocation(), player.getOrientation());
            kl.despawn();
        }
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean b) {

        if (renderable instanceof NPC) {
            if (blacklistRendering.contains(renderable)) return false;
        } else if (renderable instanceof Projectile) {
            if (catifyProjectiles || catifyAttackedNPCs) return false;
        }
        return true;
    }
}
