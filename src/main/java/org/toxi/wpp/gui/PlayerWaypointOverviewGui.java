package org.toxi.wpp.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import nl.odalitadevelopments.menus.annotations.Menu;
import nl.odalitadevelopments.menus.contents.MenuContents;
import nl.odalitadevelopments.menus.contents.placeableitem.PlaceableItemsCloseAction;
import nl.odalitadevelopments.menus.items.ClickableItem;
import nl.odalitadevelopments.menus.items.UpdatableItem;
import nl.odalitadevelopments.menus.items.buttons.PageItem;
import nl.odalitadevelopments.menus.iterators.MenuIteratorType;
import nl.odalitadevelopments.menus.iterators.MenuObjectIterator;
import nl.odalitadevelopments.menus.menu.providers.PlayerMenuProvider;
import nl.odalitadevelopments.menus.menu.type.MenuType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.toxi.misc.ItemFactory;
import org.toxi.wpp.WaypointsPlugin;
import org.toxi.wpp.data.Waypoint;
import org.toxi.wpp.util.OverviewMenuFilter;
import org.toxi.wpp.util.SortMechanism;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Menu(
        title = "",
        type = MenuType.CHEST_6_ROW
)
public class PlayerWaypointOverviewGui implements PlayerMenuProvider {
    private static final int ITEMS_PER_PAGE = 27;
    private final WaypointsPlugin plugin;

    //TODO: replace filter map with inbuilt session cache
    private final Map<UUID, OverviewMenuFilter> activeFilterMap = new ConcurrentHashMap<>();

    public PlayerWaypointOverviewGui(WaypointsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(@NotNull Player player, @NotNull MenuContents menuContents) {
        var dataFile = plugin.getWaypointRegistry().getData(player).orElse(null);

        if (dataFile == null || dataFile.getWayPointList().isEmpty()) {
            player.sendMessage(Component.translatable("gui.loading_error"));
            menuContents.closeInventory(player, PlaceableItemsCloseAction.RETURN);
            return;
        }
        setBorderFillers(player, menuContents);

        // place filter buttons on the overview page if enabled by the user
        if (dataFile.isEnableOverviewFilters()) {
            setOverviewFilterButtons(player, menuContents);
        }
        var pagination = menuContents.pagination("ov_page_switcher", ITEMS_PER_PAGE)
                .objectIterator(createWaypointObjectIterator(menuContents))
                .create();

        // using a batch for improved performance
        pagination.createBatch();
        for (Waypoint waypoint : dataFile.getWayPointList()) {
            pagination.addItem(waypoint);
        }
        pagination.endBatch();

        menuContents.set(4, 1, PageItem.previous(pagination, ItemFactory.create(Material.ARROW)
                .meta()
                .withName(Component.translatable("gui.overview.previous"))
                        .withLore(Component.translatable("gui.overview.previous.lore"))
                .build()));

        menuContents.set(4, 7, PageItem.next(pagination, ItemFactory.create(Material.ARROW)
                .meta()
                .withName(Component.translatable("gui.overview.next"))
                .withLore(Component.translatable("gui.overview.next.lore"))
                .build()));
    }

    private MenuObjectIterator<Waypoint> createWaypointObjectIterator(MenuContents menuContents) {
        return menuContents.createObjectIterator(MenuIteratorType.HORIZONTAL, 0, 0, Waypoint.class, waypoint ->
                ClickableItem.of(ItemFactory.create(waypoint.displayMaterial())
                        .meta()
                        .withName(Component.text(waypoint.name())
                                .decoration(TextDecoration.BOLD, false)
                                .color(TextColor.color(250, 172, 5)))
                        .withLore(Component.translatable("gui.overview.item.lore"))
                        .build(), inventoryClickEvent -> {
                    // open actual waypoint inventory
                    plugin.getWaypointRegistry().openPoint((Player) inventoryClickEvent.getWhoClicked(), waypoint);

        })).sorter(0, SortMechanism.NAME_ASCENDING);
    }

    private void setOverviewFilterButtons(@NotNull Player player, @NotNull MenuContents menuContents) {
         var currentFilter = activeFilterMap.getOrDefault(player.getUniqueId(), OverviewMenuFilter.NONE);

         //OVERWORLD
         menuContents.setRefreshable(3, 3, () ->
             ClickableItem.of(ItemFactory.create(Material.GRASS_BLOCK)
                             .meta().withName(Component.translatable("gui.overview.filter.overworld"))
                             .withLore(Component.translatable("gui.overview.filter.overworld.lore")).build(),
                     inventoryClickEvent -> {
                         player.playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1);
                         if (currentFilter == OverviewMenuFilter.OVERWORLD) {
                             activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.NONE);
                             return;
                         }
                         activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.OVERWORLD);
                     })
         );
         //NETHER
         menuContents.setRefreshable(3, 4, () ->
                 ClickableItem.of(ItemFactory.create(Material.NETHERRACK)
                                 .meta().withName(Component.translatable("gui.overview.filter.nether"))
                                 .withLore(Component.translatable("gui.overview.filter.nether.lore")).build(),
                         inventoryClickEvent -> {

                     player.playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1);
                     if (currentFilter == OverviewMenuFilter.NETHER) {
                         activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.NONE);
                         return;
                     }
                     activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.NETHER);

         }));
         //END
        menuContents.setRefreshable(3, 5, () ->
                ClickableItem.of(ItemFactory.create(Material.END_STONE)
                                .meta()
                                .withName(Component.translatable("gui.overview.filter.end"))
                                .withLore(Component.translatable("gui.overview.filter.end.lore"))
                                .build(),
                        inventoryClickEvent -> {

                    player.playSound(player, Sound.UI_BUTTON_CLICK, 0.5f, 1);
                    if (currentFilter == OverviewMenuFilter.END) {
                        activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.NONE);
                        return;
                    }
                    activeFilterMap.put(player.getUniqueId(), OverviewMenuFilter.END);
                }));

    }

    private void setBorderFillers(Player player, MenuContents menuContents) {
        menuContents.fillRow(3, UpdatableItem.of(() -> {

            var currentFilter = activeFilterMap.getOrDefault(player.getUniqueId(), OverviewMenuFilter.NONE);
            ItemFactory filler = ItemFactory.empty();

            switch (currentFilter) {
                case END -> filler.withType(Material.PURPLE_STAINED_GLASS_PANE);
                case NETHER -> filler.withType(Material.RED_STAINED_GLASS_PANE);
                case OVERWORLD -> filler.withType(Material.GREEN_STAINED_GLASS_PANE);
                case null, default -> filler.withType(Material.BLACK_STAINED_GLASS_PANE);
            }
            return filler.build();
        }));

    }
}
