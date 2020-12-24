package net.runelite.client.plugins.groupscape;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.groupscape.messages.DropMessage;
import net.runelite.client.plugins.groupscape.pojos.Drop;
import net.runelite.client.plugins.loottracker.LootReceived;

@Slf4j
@PluginDescriptor(
        name = "GroupScape Plugin",
        description = "Send game events to your group at GroupScape and track the groups progress",
        tags = {"discord", "challenges", "loot", "group"}
)

public class GroupScapePlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private GroupScapeConfig config;

    private Player player;
    private HttpSender sender;

    @Inject
    private ItemManager itemManager;

    @Override
    protected void startUp() throws Exception
    {
        log.info("Plug in started!");
        sender = new HttpSender("http://localhost", 3000);
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Plug in stopped!");
    }

    @Subscribe
    public void onLootReceived(LootReceived lootReceived){
        sendDropNotificationForUser(lootReceived);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        sender.sendPostRequest("{gameState: '" + gameStateChanged.getGameState().toString() +  "'}", "/changeGameState", "username=Iron 69m");
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            player = client.getLocalPlayer();
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "GroupScape loaded succesfully", null);
        }
    }

    private void sendDropNotificationForUser(LootReceived loot) {

        String sourceOfLoot = loot.getName();
        int combatLevel = loot.getCombatLevel();

        //Create the Drop Message
        DropMessage message = new DropMessage();
        message.setCombatLevel(combatLevel);
        message.setSource(sourceOfLoot);

        log.info("==== " + sourceOfLoot + " lvl: " + combatLevel + " ======");

        //Loop over Items and add up information
        for(ItemStack item : loot.getItems()){

            ItemComposition comp = itemManager.getItemComposition(item.getId());

            int quantity = item.getQuantity();
            int price = itemManager.getItemPrice(item.getId());
            String name =itemManager.getItemComposition(item.getId()).getName();

            log.info(" > " + name + " GP: " + price + " x " + quantity);

            Drop dropInformation = new Drop();
            dropInformation.setGp(price * quantity);
            dropInformation.setId(item.getId());
            dropInformation.setName(name);
            message.getDrops().add(dropInformation);
        }

        sender.sendPostRequest(message, "processLootDrop", "username=Iron 69m");
    }

    @Provides
    GroupScapeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GroupScapeConfig.class);
    }
}
