package org.royaldev.royalcommands.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.protocol.packets.PacketCreativeInventoryAction;
import org.royaldev.royalcommands.protocol.packets.PacketSetSlot;
import org.royaldev.royalcommands.protocol.packets.PacketSetWindowItems;
import org.royaldev.royalcommands.spawninfo.NbtFactory;
import org.royaldev.royalcommands.spawninfo.SpawnInfo;

import java.util.ArrayList;
import java.util.List;

public class ProtocolListener {

    protected static final String NBT_INFO_KEY = "rcmds-spawninfo";
    final SpawnRenameProcessor srp = new SpawnRenameProcessor();
    private final RoyalCommands plugin;
    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

    public ProtocolListener(RoyalCommands instance) {
        this.plugin = instance;
    }

    public void initialize() {
        this.createSetSlotListener();
        this.createWindowItemsListener();
        this.createSetCreativeSlotListener();
    }

    public void uninitialize() {
        this.pm.removePacketListeners(this.plugin);
    }

    public void createSetSlotListener() {
        this.pm.addPacketListener(new PacketAdapter(PacketAdapter.params(this.plugin, PacketType.Play.Server.SET_SLOT)) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!Config.useProtocolLib) return;
                final PacketSetSlot p = new PacketSetSlot(event.getPacket());
                ItemStack is = p.getSlotData();
                if (is == null) return;
                final SpawnInfo si = SpawnInfo.SpawnInfoManager.getSpawnInfo(is);
                if (!si.isSpawned() && !si.hasComponents()) return;
                is = SpawnInfo.SpawnInfoManager.removeSpawnInfo(is);
                final NbtFactory.NbtCompound nbtc = NbtFactory.fromItemTag(is);
                nbtc.put(NBT_INFO_KEY, si.toString());
                NbtFactory.setItemTag(is, nbtc);
                p.setSlotData(is);
            }
        });
    }

    public void createWindowItemsListener() {
        this.pm.addPacketListener(new PacketAdapter(PacketAdapter.params(this.plugin, PacketType.Play.Server.WINDOW_ITEMS)) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!Config.useProtocolLib) return;
                final PacketSetWindowItems p = new PacketSetWindowItems(event.getPacket());
                final List<ItemStack> newItems = new ArrayList<ItemStack>();
                for (ItemStack is : p.getItems()) {
                    if (is == null) { // SpawnInfoManager can't take null ItemStacks
                        newItems.add(null);
                        continue;
                    }
                    final SpawnInfo si = SpawnInfo.SpawnInfoManager.getSpawnInfo(is);
                    if (!si.isSpawned() && !si.hasComponents()) continue;
                    is = SpawnInfo.SpawnInfoManager.removeSpawnInfo(is);
                    final NbtFactory.NbtCompound nbtc = NbtFactory.fromItemTag(is);
                    nbtc.put(NBT_INFO_KEY, si.toString());
                    NbtFactory.setItemTag(is, nbtc);
                    newItems.add(is);
                }
                p.setItems(newItems.toArray(new ItemStack[newItems.size()]));
            }
        });
    }

    public void createSetCreativeSlotListener() {
        this.pm.addPacketListener(new PacketAdapter(PacketAdapter.params(this.plugin, PacketType.Play.Client.SET_CREATIVE_SLOT).optionIntercept()) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (!Config.useProtocolLib) return;
                ProtocolListener.this.srp.unprocessFieldStack(event);
                final PacketCreativeInventoryAction p = new PacketCreativeInventoryAction(event.getPacket());
                if (p.getClickedItem() == null) return;
                final ItemStack is = p.getClickedItem();
                final NbtFactory.NbtCompound nbtc = NbtFactory.fromItemTag(is);
                if (!nbtc.containsKey(NBT_INFO_KEY)) return;
                final SpawnInfo si = new SpawnInfo(nbtc.getString(NBT_INFO_KEY, "false/null/false/null"));
                nbtc.remove(NBT_INFO_KEY);
                if (nbtc.isEmpty()) NbtFactory.setItemTag(is, null);
                else NbtFactory.setItemTag(is, nbtc);
                if (si.isSpawned() || si.hasComponents()) SpawnInfo.SpawnInfoManager.applySpawnInfo(is, si);
                p.setClickedItem(is);
            }
        });
    }
}
