/*
 * Copyright © 2022-2024, RezzedUp and Contributors <https://github.com/LeafCommunity/ConcreteMixer>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.survival.concretemixer;

import community.leaf.survival.concretemixer.ConcreteMixerPlugin;
import community.leaf.survival.concretemixer.metrics.TransformationsPerHour;
import community.leaf.survival.concretemixer.Concrete;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public final class CauldronPowderDropListener implements Listener {
    private final ConcreteMixerPlugin plugin;
    private final TransformationsPerHour counter;

    public CauldronPowderDropListener(ConcreteMixerPlugin plugin, TransformationsPerHour counter) {
        this.plugin = plugin;
        this.counter = counter;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        ItemStack stack = item.getItemStack();

        if (!ConcreteType.isConcretePowder(stack.getType())) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!item.isValid() || item.isDead()) return;

            Location loc = item.getLocation();
            Block block = loc.getBlock();

            if (block.getType() == Material.CAULDRON && block.getBlockData() instanceof Levelled cauldron) {
                if (cauldron.getLevel() == 3) {
                    item.remove();
                    Material concrete = ConcreteType.toConcrete(stack.getType());
                    ItemStack converted = new ItemStack(concrete, stack.getAmount());
                    loc.getWorld().dropItemNaturally(loc, converted);
                    counter.increment();
                }
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack stack = item.getItemStack();

        // Check for concrete powder
        if (!ConcreteType.isConcretePowder(stack.getType())) {
            return;
        }

        // OPTIONAL: Skip if item has a thrower
        // This avoids double-processing.
        if (item.getThrower() != null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!item.isValid() || item.isDead()) return;

            Location loc = item.getLocation();
            Block block = loc.getBlock();

            if (block.getType() == Material.CAULDRON && block.getBlockData() instanceof Levelled cauldron) {
                if (cauldron.getLevel() == 3) {
                    item.remove();
                    Material concrete = ConcreteType.toConcrete(stack.getType());
                    ItemStack converted = new ItemStack(concrete, stack.getAmount());
                    loc.getWorld().dropItemNaturally(loc, converted);
                    counter.increment();
                }
            }
        }, 5L);
    }
}
