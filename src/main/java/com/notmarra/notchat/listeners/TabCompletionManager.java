package com.notmarra.notchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import net.milkbowl.vault.permission.Permission;

import com.notmarra.notchat.NotChat;

public class TabCompletionManager implements Listener {
    
    private final NotChat plugin;
    private Permission perms = null;
    private boolean vaultEnabled = false;
    
    public TabCompletionManager(NotChat plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Inicializuje manažer tab-completion
     * 
     * @return true pokud byla inicializace úspěšná, false pokud není dostupný Vault
     */
    public boolean initialize() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return true;

        // // Kontrola dostupnosti Vaultu
        // if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
        //     // Inicializace Vault permission API
        //     RegisteredServiceProvider<Permission> rsp = 
        //         plugin.getServer().getServicesManager().getRegistration(Permission.class);
            
        //     if (rsp != null) {
        //         perms = rsp.getProvider();
        //         vaultEnabled = true;
                
        //         // Registrace event listeneru
        //         plugin.getServer().getPluginManager().registerEvents(this, plugin);
                
        //         return true;
        //     } else {
        //         plugin.getLogger().warning("Vault nalezen, ale nelze získat Permission API");
        //     }
        // } else {
        //     plugin.getLogger().warning("Vault nenalezen - filtrace tab-completion nebude aktivní");
        // }
        
        // return false;
    }
    
    /**
     * Ukončí manažer a odregistruje všechny event listenery
     */
    public void shutdown() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerCommandSendEvent(PlayerCommandSendEvent event) {
        // if (!vaultEnabled) return;
        event.getCommands().clear();

        //     CommandSender player = event.getSender();
    //     Collection<String> commands = event.getCompletions();
        
    //     // Vytvoření kopie seznamu příkazů
    //     List<String> allCommands = new ArrayList<>(commands);
        
    //     // Vymazání všech příkazů
    //     commands.clear();
        
    //     // Filtrace příkazů podle oprávnění hráče
    //     for (String commandName : allCommands) {
    //         Command command = plugin.getServer().getCommandMap().getCommand(commandName);
            
    //         // if (command == null) {
    //         //     // Pokud command není nalezen, přidáme ho zpět (pro jistotu)
    //         //     commands.add(commandName);
    //         //     continue;
    //         // }
            
    //         String permission = command.getPermission();
    //         // if (permission == null || perms.has(player, permission)) {
    //         if (permission == null || !player.hasPermission(permission)) {
    //             event.setCancelled(true);
    //             // Hráč má oprávnění, přidáme příkaz zpět do tab-completion
    //             // commands.add(commandName);
    //         }
    //     }
    }
}
