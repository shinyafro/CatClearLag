package me.time6628.clag.sponge;

import me.time6628.clag.sponge.commands.ForceGCCommand;
import me.time6628.clag.sponge.commands.RemoveAllCommand;
import me.time6628.clag.sponge.commands.RemoveGItemsCommand;
import me.time6628.clag.sponge.commands.RemoveHostilesCommand;
import me.time6628.clag.sponge.runnables.ItemClearer;
import me.time6628.clag.sponge.runnables.ItemClearingWarning;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by TimeTheCat on 7/2/2016.
 */
@Plugin(name = "CatClearLag", id = "catclearlag", version = "0.4.1", description = "DIE LAG, DIE!")
public class CatClearLag {

    public static Logger cclLogger = Logger.getLogger("CCL");
    public Game game = Sponge.getGame();

    private Scheduler scheduler = Sponge.getScheduler();

    public Text prefix = Text.builder().color(TextColors.DARK_PURPLE).append(Text.of("[KKMCClearLag] ")).build();

    @Listener
    public void onInit(GameInitializationEvent event) {
        CatClearLag.cclLogger.info("Starting plugin...");
        registerCommands();
        Task.Builder builder = scheduler.createTaskBuilder();
        Task task = builder.execute(new ItemClearer(this))
                .async()
                .delay(10, TimeUnit.MINUTES)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Item Remover")
                .submit(this);
        Task warningTaskOne = builder.execute(new ItemClearingWarning(60, this))
                .async()
                .delay(540, TimeUnit.SECONDS)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
        Task warningTaskTwo = builder.execute(new ItemClearingWarning(30, this))
                .async()
                .delay(570, TimeUnit.SECONDS)
                .interval(10, TimeUnit.MINUTES)
                .name("CatClearLag Removal warning 1")
                .submit(this);
    }

    private void registerCommands() {

        CatClearLag.cclLogger.info("Registering commands...");

        CommandSpec cSpec = CommandSpec.builder()
                .description(Text.of("Remove all hostile entities from the server."))
                .permission("catclearlag.command.removehostile")
                .executor(new RemoveHostilesCommand(this))
                .build();

        CommandSpec cSpec2 = CommandSpec.builder()
                .description(Text.of("Remove all entities from the server."))
                .permission("catclearlag.command.removeall")
                .executor(new RemoveAllCommand(this))
                .build();

        CommandSpec cSpec3 = CommandSpec.builder()
                .description(Text.of("Remove all ground items from the server."))
                .permission("catclearlag.command.removegitems")
                .executor(new RemoveGItemsCommand(this))
                .build();
        CommandSpec cSpec4 = CommandSpec.builder()
                .description(Text.of("Force Garabage Collection"))
                .permission("catclearlag.command.forcegc")
                .executor(new ForceGCCommand(this))
                .build();

        Sponge.getCommandManager().register(this, cSpec, "removehostiles", "rhost");
        Sponge.getCommandManager().register(this, cSpec2, "removeall", "rall");
        Sponge.getCommandManager().register(this, cSpec3, "removegrounditems", "rgitems");
        Sponge.getCommandManager().register(this, cSpec4, "forcegc", "forcegarbagecollection");
    }


    public void clearGoundItems() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the item entities in the world
            Collection<Entity> entities = temp.getEntities();
            //for all the entities, remove the item ones
            entities.stream().filter(entity -> entity instanceof Item).forEach(Entity::remove);
        });
    }

    public void removeHostile() {
        //get all worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the hostile entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach((entity) -> {
                if (entity instanceof Hostile && !(entity instanceof Player)) {
                    entity.remove();
                }
            });
        });
    }

    public void removeAll() {
        //get all the worlds
        Collection<World> worlds = Sponge.getServer().getWorlds();
        //for each world
        worlds.forEach((temp) -> {
            //get all the entities in the world
            Collection<Entity> entities = temp.getEntities();
            //remove them all
            entities.forEach(Entity::remove);
        });
    }
}