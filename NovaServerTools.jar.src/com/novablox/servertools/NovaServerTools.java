/*    */ package com.novablox.servertools;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerCommandPreprocessEvent;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.event.player.PlayerMoveEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public final class NovaServerTools extends JavaPlugin implements Listener, CommandExecutor {
/*    */   private boolean maintenance = false;
/* 21 */   private final Set<UUID> vanished = new HashSet<>();
/* 22 */   private final Set<UUID> frozen = new HashSet<>();
/* 23 */   public void onEnable() { saveDefaultConfig(); getServer().getPluginManager().registerEvents(this, (Plugin)this); String[] arrayOfString; for (String str : arrayOfString = new String[] { "clearchat", "maintenance", "fly", "vanish", "freeze", "staffchat", "invsee" }) { if (getCommand(str) != null) getCommand(str).setExecutor(this);  }  getLogger().info("NovaServerTools aktif!"); }
/* 24 */   private String c(String paramString) { return ChatColor.translateAlternateColorCodes('&', paramString); } public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString) { Player player;
/* 25 */     String str = paramCommand.getName().toLowerCase(); if (str.equals("clearchat")) { for (Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator(); iterator.hasNext();) { for (player1 = iterator.next(), b = 0; b < 120; ) { player1.sendMessage(""); b++; }  }  Bukkit.broadcastMessage(c("&a✔ Sohbet yetkili tarafından temizlendi.")); return true; }
/* 26 */      if (str.equals("maintenance")) { this.maintenance = !this.maintenance; Bukkit.broadcastMessage(c(this.maintenance ? "&c⚠ Sunucu bakım moduna alındı." : "&a✔ Sunucu bakım modundan çıkarıldı.")); return true; }
/* 27 */      if (paramCommandSender instanceof Player) { player = (Player)paramCommandSender; } else { paramCommandSender.sendMessage("Bu komut oyuncu içindir."); return true; }
/* 28 */      if (str.equals("fly")) { player.setAllowFlight(!player.getAllowFlight()); player.setFlying(player.getAllowFlight()); player.sendMessage(c("&bUçuş: " + (player.getAllowFlight() ? "&aAçık" : "&cKapalı"))); return true; }
/* 29 */      if (str.equals("vanish")) { boolean bool = this.vanished.add(player.getUniqueId()); if (!bool) this.vanished.remove(player.getUniqueId());  for (Player player1 : Bukkit.getOnlinePlayers()) { if (!player1.hasPermission("novaservertools.vanishsee")) { if (bool) { player1.hidePlayer((Plugin)this, player); continue; }  player1.showPlayer((Plugin)this, player); }  }  player.sendMessage(c(bool ? "&aGörünmezlik açıldı." : "&cGörünmezlik kapandı.")); return true; }
/* 30 */      if (str.equals("freeze")) { if (paramArrayOfString.length < 1) { player.sendMessage(c("&cKullanım: /freeze <oyuncu>")); return true; }  Player player1 = Bukkit.getPlayerExact(paramArrayOfString[0]); if (player1 == null) { player.sendMessage(c("&cOyuncu bulunamadı.")); return true; }  if (this.frozen.add(player1.getUniqueId())) { player1.sendMessage(c("&cDonduruldun. Yetkili kontrolü bekleniyor.")); player.sendMessage(c("&aOyuncu donduruldu.")); } else { this.frozen.remove(player1.getUniqueId()); player1.sendMessage(c("&aDondurman kaldırıldı.")); player.sendMessage(c("&aOyuncu çözüldü.")); }  return true; }
/* 31 */      if (str.equals("staffchat")) { String str1 = String.join(" ", (CharSequence[])paramArrayOfString); if (str1.isBlank()) { player.sendMessage(c("&cKullanım: /staffchat <mesaj>")); return true; }  String str2 = c(getConfig().getString("staffchat-format", "&8[&dSTAFF&8] &f{player} &8» &d{message}").replace("{player}", player.getName()).replace("{message}", str1)); for (Player player1 : Bukkit.getOnlinePlayers()) { if (player1.hasPermission("novaservertools.staffchat")) player1.sendMessage(str2);  }  Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(str2)); return true; }
/* 32 */      if (str.equals("invsee")) { if (paramArrayOfString.length < 1) { player.sendMessage(c("&cKullanım: /invsee <oyuncu>")); return true; }  Player player1 = Bukkit.getPlayerExact(paramArrayOfString[0]); if (player1 == null) { player.sendMessage(c("&cOyuncu bulunamadı.")); return true; }  player.openInventory(player1.getInventory()); return true; }
/* 33 */      return true; } @EventHandler
/* 34 */   public void move(PlayerMoveEvent paramPlayerMoveEvent) { if (this.frozen.contains(paramPlayerMoveEvent.getPlayer().getUniqueId())) paramPlayerMoveEvent.setTo(paramPlayerMoveEvent.getFrom());  } @EventHandler
/* 35 */   public void join(PlayerJoinEvent paramPlayerJoinEvent) { if (this.maintenance && !paramPlayerJoinEvent.getPlayer().hasPermission("novaservertools.maintenance")) { Bukkit.getScheduler().runTask((Plugin)this, () -> paramPlayerJoinEvent.getPlayer().kickPlayer(c(getConfig().getString("maintenance-message", "&cSunucu bakımda.")))); return; }  for (UUID uUID : this.vanished) { if (!paramPlayerJoinEvent.getPlayer().hasPermission("novaservertools.vanishsee")) { Player player = Bukkit.getPlayer(uUID); if (player != null) paramPlayerJoinEvent.getPlayer().hidePlayer((Plugin)this, player);  }  }
/* 36 */      } @EventHandler public void cmd(PlayerCommandPreprocessEvent paramPlayerCommandPreprocessEvent) { if (this.maintenance && !paramPlayerCommandPreprocessEvent.getPlayer().hasPermission("novaservertools.maintenance")) { String str = paramPlayerCommandPreprocessEvent.getMessage().toLowerCase(); if (!str.startsWith("/login") && !str.startsWith("/l")) { paramPlayerCommandPreprocessEvent.setCancelled(true); paramPlayerCommandPreprocessEvent.getPlayer().sendMessage(c(getConfig().getString("maintenance-message", "&cSunucu bakımda."))); }
/*    */        }
/*    */      }
/*    */ 
/*    */ }


/* Location:              C:\Users\adasd\Downloads\NOVABLOX-5-PLUGIN-JARLARI\NovaServerTools.jar!\com\novablox\servertools\NovaServerTools.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */