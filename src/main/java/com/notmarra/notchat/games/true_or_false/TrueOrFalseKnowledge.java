package com.notmarra.notchat.games.true_or_false;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrueOrFalseKnowledge {
    // Knowledge about Minecraft blocks
    public static final Map<String, String> BLOCK_PROPERTIES = Map.of(
        "stone", "This block is very common and can be mined with a pickaxe",
        "dirt", "This block can be tilled with a hoe to create farmland",
        "obsidian", "This block requires a diamond pickaxe to mine",
        "bedrock", "This block cannot be broken in survival mode",
        "glass", "This block is transparent and doesn't drop when broken",
        "sand", "This block is affected by gravity",
        "diamond_ore", "This block drops diamonds when mined",
        "tnt", "This block explodes when activated",
        "oak_log", "This block can be crafted into planks",
        "chest", "This block can store items"
    );
    
    // Knowledge about Minecraft items
    public static final Map<String, String> ITEM_PROPERTIES = Map.of(
        "diamond_sword", "This item deals 7 attack damage",
        "bow", "This item can shoot arrows",
        "fishing_rod", "This item can catch fish",
        "golden_apple", "This item restores health and gives absorption",
        "compass", "This item points to the world spawn",
        "bucket", "This item can carry water or lava",
        "ender_pearl", "This item allows teleportation when thrown",
        "shield", "This item can block attacks",
        "flint_and_steel", "This item can light fires",
        "shears", "This item can collect wool from sheep"
    );
    
    // Knowledge about Minecraft mobs
    public static final Map<String, String> MOB_PROPERTIES = Map.of(
        "creeper", "This mob explodes when close to players",
        "enderman", "This mob teleports when hit with projectiles",
        "zombie", "This mob burns in sunlight",
        "skeleton", "This mob shoots arrows",
        "spider", "This mob can climb walls",
        "pig", "This mob can be ridden with a saddle",
        "cow", "This mob drops leather and beef",
        "sheep", "This mob can be sheared for wool",
        "villager", "This mob can trade with players",
        "wolf", "This mob can be tamed with bones"
    );
    
    // Knowledge about Minecraft biomes
    public static final Map<String, String> BIOME_PROPERTIES = Map.of(
        "desert", "This biome has cacti and sand",
        "forest", "This biome has many trees",
        "ocean", "This biome is mostly water",
        "jungle", "This biome has parrots and ocelots",
        "mesa", "This biome has terracotta and red sand",
        "mushroom_fields", "This biome has mooshrooms",
        "nether", "This biome is filled with lava and netherrack",
        "the_end", "This biome contains endermen and the Ender Dragon",
        "taiga", "This biome has spruce trees and wolves",
        "swamp", "This biome has slimes and lily pads"
    );
    
    // Objects/entities from each category
    public static final List<String> BLOCKS = new ArrayList<>(BLOCK_PROPERTIES.keySet());
    public static final List<String> ITEMS = new ArrayList<>(ITEM_PROPERTIES.keySet());
    public static final List<String> MOBS = new ArrayList<>(MOB_PROPERTIES.keySet());
    public static final List<String> BIOMES = new ArrayList<>(BIOME_PROPERTIES.keySet());
    
    // Question templates for true statements
    public static final List<String> TRUE_TEMPLATES = Arrays.asList(
        // "%s %s.",
        "In Minecraft, %s %s.",
        "It's true that %s %s in Minecraft."
    );
    
    // Question templates for false statements
    public static final List<String> FALSE_TEMPLATES = Arrays.asList(
        // "%s %s.",
        "In Minecraft, %s %s.",
        "It's a fact that %s %s in Minecraft."
    );
    
    // Properties for false statement generation
    public static final Map<String, List<String>> FALSE_PROPERTIES = Map.of(
        "block", Arrays.asList(
            "requires a diamond pickaxe to mine",
            "drops diamonds when mined",
            "is immune to explosions",
            "emits light",
            "can be placed underwater",
            "is affected by gravity",
            "can be crafted in a regular crafting table",
            "is only found in the Nether",
            "can be broken by hand"
        ),
        
        "item", Arrays.asList(
            "can be enchanted",
            "has durability",
            "is stackable up to 64",
            "can be found in dungeon chests",
            "gives a speed boost when used",
            "can be traded with villagers",
            "is craftable from diamond",
            "is required to enter the End",
            "restores all hunger when eaten"
        ),
        
        "mob", Arrays.asList(
            "drops experience when killed",
            "is hostile to players",
            "can swim",
            "is immune to fire damage",
            "can be tamed",
            "spawns in the Nether",
            "drops rare items when killed",
            "can climb ladders",
            "is afraid of cats"
        ),
        
        "biome", Arrays.asList(
            "contains unique structures",
            "has snow",
            "has frequent rain",
            "spawns specific mobs",
            "has unique vegetation",
            "has a special sky color",
            "is extremely rare",
            "has different water color",
            "has unique ore generation"
        )
    );
}