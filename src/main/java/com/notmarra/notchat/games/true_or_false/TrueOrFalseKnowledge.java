package com.notmarra.notchat.games.true_or_false;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrueOrFalseKnowledge implements KnowledgeBase {
    private static TrueOrFalseKnowledge instance;

    private final Map<String, Map<String, Map<String, String>>> categoryData;
    private final Map<String, List<String>> falsePropertiesMap;
    private final Map<String, String> hintsMap;
    private final Map<String, Map<String, String>> explanationsMap;
    private final Map<String, String> falseExplanationsMap;
    
    private final List<String> trueTemplates;
    private final List<String> falseTemplates;
    
    public static synchronized KnowledgeBase getInstance() {
        if (instance == null) {
            instance = new TrueOrFalseKnowledge();
        }
        return instance;
    }
    
    private TrueOrFalseKnowledge() {
        categoryData = new HashMap<>();
        falsePropertiesMap = new HashMap<>();
        hintsMap = new HashMap<>();
        explanationsMap = new HashMap<>();
        falseExplanationsMap = new HashMap<>();
        
        trueTemplates = new ArrayList<>(Arrays.asList(
            "In Minecraft, %s %s.",
            "It's true that %s %s in Minecraft.",
            "Players know that %s %s."
        ));
        
        falseTemplates = new ArrayList<>(Arrays.asList(
            "In Minecraft, %s %s.",
            "It's a fact that %s %s in Minecraft.",
            "According to Minecraft, %s %s."
        ));
        
        initializeBlockCategory();
        initializeItemCategory();
        initializeMobCategory();
        initializeBiomeCategory();
        
        initializeStructureCategory();
        initializeDimensionCategory();
    }
    
    private void initializeBlockCategory() {
        String category = "block";
        Map<String, Map<String, String>> blocks = new HashMap<>();
        
        addBlock(blocks, "stone", Map.of(
            "common", "is very common and can be mined with a pickaxe",
            "tool", "requires at least a wooden pickaxe to mine"
        ));
        
        addBlock(blocks, "dirt", Map.of(
            "common", "is very common throughout the overworld",
            "till", "can be tilled with a hoe to create farmland",
            "tool", "can be mined with anything including bare hands"
        ));
        
        addBlock(blocks, "obsidian", Map.of(
            "tough", "requires a diamond pickaxe to mine",
            "blast", "is highly resistant to explosions",
            "creation", "is created when water flows over lava source blocks"
        ));
        
        addBlock(blocks, "bedrock", Map.of(
            "unbreakable", "cannot be broken in survival mode",
            "location", "forms the bottom layer of the Overworld and top and bottom of the Nether"
        ));
        
        addBlock(blocks, "glass", Map.of(
            "transparent", "is transparent and doesn't drop when broken",
            "creation", "is created by smelting sand in a furnace"
        ));
        
        addBlock(blocks, "sand", Map.of(
            "gravity", "is affected by gravity",
            "location", "is commonly found in deserts and beaches",
            "use", "can be smelted into glass"
        ));
        
        addBlock(blocks, "diamond_ore", Map.of(
            "valuable", "drops diamonds when mined",
            "rare", "is quite rare and typically found deep underground",
            "tool", "requires an iron pickaxe or better to obtain diamonds"
        ));
        
        addBlock(blocks, "tnt", Map.of(
            "explosive", "explodes when activated by redstone or fire",
            "crafting", "is crafted using gunpowder and sand"
        ));
        
        addBlock(blocks, "oak_log", Map.of(
            "renewable", "can be farmed by growing oak trees",
            "crafting", "can be crafted into planks",
            "tool", "can be chopped with an axe"
        ));
        
        addBlock(blocks, "chest", Map.of(
            "storage", "can store items",
            "double", "can be placed next to another chest to form a large chest",
            "obstruction", "is considered a non-solid block for redstone purposes"
        ));
        
        categoryData.put(category, blocks);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "requires a netherite pickaxe to mine",
            "drops diamonds when mined with any tool",
            "is completely immune to explosions",
            "emits light equivalent to a torch",
            "can be placed underwater without breaking",
            "teleports players when stepped on",
            "is only found in the End dimension",
            "can be broken instantly regardless of tool",
            "floats on water instead of sinking",
            "changes color when it rains"
        ));
        
        falseExplanationsMap.put(category, "This is not a property of this block in Minecraft.");
    }
    
    private void initializeItemCategory() {
        String category = "item";
        Map<String, Map<String, String>> items = new HashMap<>();
        
        addItem(items, "diamond_sword", Map.of(
            "damage", "deals 7 attack damage",
            "enchant", "can be enchanted with sharpness to increase damage",
            "material", "is crafted using diamonds and a stick"
        ));
        
        addItem(items, "bow", Map.of(
            "projectile", "can shoot arrows",
            "enchant", "can be enchanted with infinity to shoot infinite arrows",
            "charge", "deals more damage when fully charged"
        ));
        
        addItem(items, "fishing_rod", Map.of(
            "fishing", "can catch fish from water",
            "treasure", "can sometimes catch treasure items like enchanted books",
            "hook", "can hook mobs and pull them towards the player"
        ));
        
        addItem(items, "golden_apple", Map.of(
            "health", "restores health and gives absorption hearts",
            "rare", "is relatively rare in dungeon and mineshaft chests",
            "effects", "provides temporary health-related status effects"
        ));
        
        addItem(items, "compass", Map.of(
            "navigation", "points to the world spawn point",
            "crafting", "is crafted with iron ingots and redstone",
            "lodestone", "can be bound to a lodestone to point to that location instead"
        ));
        
        categoryData.put(category, items);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "can be enchanted with Protection",
            "has unlimited durability",
            "is stackable up to 64 in a single inventory slot",
            "always drops from endermen",
            "gives a flying ability when equipped",
            "can be traded with villagers for emeralds",
            "is required to enter the End dimension",
            "restores all hunger points when eaten",
            "is immune to lava",
            "creates a lightning strike when thrown"
        ));
        
        falseExplanationsMap.put(category, "This item doesn't have this property in Minecraft.");
    }
    
    private void initializeMobCategory() {
        String category = "mob";
        Map<String, Map<String, String>> mobs = new HashMap<>();
        
        addMob(mobs, "creeper", Map.of(
            "explosive", "explodes when close to players",
            "silent", "approaches silently without making sound",
            "charged", "can be charged by lightning to become more powerful",
            "fear", "is afraid of ocelots and cats"
        ));
        
        addMob(mobs, "enderman", Map.of(
            "teleport", "teleports when hit with projectiles or when in water",
            "block", "can pick up and move certain blocks",
            "eye", "becomes aggressive when players look at its eyes",
            "water", "takes damage from water"
        ));
        
        addMob(mobs, "zombie", Map.of(
            "undead", "burns in sunlight",
            "doors", "can break down wooden doors on hard difficulty",
            "villagers", "can convert villagers into zombie villagers",
            "reinforcement", "can summon reinforcements when attacked"
        ));
        
        addMob(mobs, "skeleton", Map.of(
            "archer", "shoots arrows at players",
            "undead", "burns in sunlight",
            "fear", "is afraid of wolves",
            "drop", "can sometimes drop bows and arrows when killed"
        ));
        
        addMob(mobs, "spider", Map.of(
            "climbing", "can climb walls",
            "vision", "has night vision",
            "neutral", "becomes neutral during daytime",
            "size", "can fit through 1x1 block spaces"
        ));
        
        categoryData.put(category, mobs);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "can swim underwater indefinitely",
            "is immune to fall damage",
            "steals items from players' inventories",
            "can break any type of block",
            "only spawns during thunderstorms",
            "drops diamonds when killed",
            "is passive and never attacks players",
            "can fly in all biomes",
            "can be tamed with any food item",
            "always drops its head when killed"
        ));
        
        falseExplanationsMap.put(category, "This mob doesn't have this characteristic in Minecraft.");
    }
    
    private void initializeBiomeCategory() {
        String category = "biome";
        Map<String, Map<String, String>> biomes = new HashMap<>();
        
        addBiome(biomes, "desert", Map.of(
            "terrain", "has cacti and sand as primary blocks",
            "weather", "has no rain or snow",
            "structure", "contains desert temples and desert wells",
            "village", "can contain desert-themed villages"
        ));
        
        addBiome(biomes, "forest", Map.of(
            "trees", "has many oak and birch trees",
            "passive", "spawns many passive mobs like rabbits",
            "flower", "commonly has flowers and mushrooms",
            "structure", "can contain woodland mansions in certain variants"
        ));
        
        addBiome(biomes, "ocean", Map.of(
            "water", "is mostly water with occasional islands",
            "life", "contains fish, squid, and dolphins",
            "structure", "has underwater ruins and shipwrecks",
            "hostile", "spawns drowned as a water-based hostile mob"
        ));
        
        addBiome(biomes, "jungle", Map.of(
            "vegetation", "has dense tree cover with vines",
            "animals", "has parrots and ocelots as unique mobs",
            "structure", "contains jungle temples and bamboo",
            "terrain", "has a very uneven surface with many hills"
        ));
        
        addBiome(biomes, "mushroom_fields", Map.of(
            "unique", "is one of the rarest biomes in the game",
            "mobs", "has mooshrooms as unique passive mobs",
            "blocks", "has mycelium instead of grass blocks",
            "hostile", "is the only biome where hostile mobs don't naturally spawn"
        ));
        
        categoryData.put(category, biomes);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "has naturally occurring diamond blocks on the surface",
            "causes all mobs to become passive",
            "has purple-colored water",
            "gives players special abilities while inside it",
            "is completely immune to player modifications",
            "can only be accessed through portals",
            "has reversed gravity for all entities",
            "changes its terrain features daily",
            "always has permanent darkness regardless of time",
            "allows flying without creative mode"
        ));
        
        falseExplanationsMap.put(category, "This is not a characteristic of this biome in Minecraft.");
    }
    
    private void initializeStructureCategory() {
        String category = "structure";
        Map<String, Map<String, String>> structures = new HashMap<>();
        
        addStructure(structures, "village", Map.of(
            "inhabitants", "is inhabited by villagers",
            "spawn", "can spawn in multiple biome types",
            "loot", "contains chests with loot",
            "job", "has different buildings for different villager jobs"
        ));
        
        addStructure(structures, "ocean_monument", Map.of(
            "location", "is found in deep ocean biomes",
            "guardian", "is guarded by guardians and elder guardians",
            "material", "is made primarily of prismarine blocks",
            "treasure", "contains sponge rooms and gold blocks"
        ));
        
        addStructure(structures, "woodland_mansion", Map.of(
            "rare", "is extremely rare and found in dark forests",
            "mobs", "is inhabited by illagers like vindicators and evokers",
            "size", "is one of the largest structures in the game",
            "loot", "contains unique loot including totems of undying"
        ));
        
        categoryData.put(category, structures);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "automatically regenerates when damaged",
            "changes location every Minecraft day",
            "contains natural nether portals",
            "is completely immune to explosions",
            "grants special powers to players inside it",
            "can be crafted and placed by players",
            "spawns only after defeating certain bosses",
            "disappears if a player dies inside it",
            "contains unlimited resources that respawn",
            "teleports players to random locations"
        ));
        
        falseExplanationsMap.put(category, "This is not a feature of this structure in Minecraft.");
    }
    
    private void initializeDimensionCategory() {
        String category = "dimension";
        Map<String, Map<String, String>> dimensions = new HashMap<>();
        
        addDimension(dimensions, "overworld", Map.of(
            "main", "is the primary dimension where players spawn",
            "water", "has oceans, rivers, and rain",
            "life", "supports all types of passive mobs",
            "cycle", "has a day-night cycle"
        ));
        
        addDimension(dimensions, "nether", Map.of(
            "heat", "is filled with lava oceans",
            "travel", "allows faster travel compared to the Overworld",
            "water", "causes water to evaporate instantly",
            "ceiling", "has a bedrock ceiling and floor"
        ));
        
        addDimension(dimensions, "end", Map.of(
            "dragon", "is home to the Ender Dragon",
            "islands", "has a main island and outer islands",
            "chorus", "has chorus plants as unique vegetation",
            "void", "has void surrounding the islands that causes death"
        ));
        
        categoryData.put(category, dimensions);
        
        falsePropertiesMap.put(category, Arrays.asList(
            "has naturally occurring diamond blocks",
            "automatically restores player health",
            "has reversed gravity",
            "can only be accessed during specific Minecraft times",
            "allows creative-like flying in survival mode",
            "has no fall damage",
            "causes all mobs to be passive",
            "changes player's appearance",
            "limits inventory space",
            "prevents certain items from working"
        ));
        
        falseExplanationsMap.put(category, "This is not a property of this dimension in Minecraft.");
    }
    
    private void addBlock(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("block:" + name, "This is a common block in Minecraft. Think about its properties.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This block " + property.getValue() + ".");
        }
        explanationsMap.put("block:" + name, explanations);
    }
    
    private void addItem(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("item:" + name, "This is an item in Minecraft. Consider what you can do with it.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This item " + property.getValue() + ".");
        }
        explanationsMap.put("item:" + name, explanations);
    }
    
    private void addMob(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("mob:" + name, "This is a mob in Minecraft. Think about its behavior.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This mob " + property.getValue() + ".");
        }
        explanationsMap.put("mob:" + name, explanations);
    }
    
    private void addBiome(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("biome:" + name, "This is a biome in Minecraft. Think about its features.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This biome " + property.getValue() + ".");
        }
        explanationsMap.put("biome:" + name, explanations);
    }
    
    private void addStructure(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("structure:" + name, "This is a generated structure in Minecraft. Consider where and how it appears.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This structure " + property.getValue() + ".");
        }
        explanationsMap.put("structure:" + name, explanations);
    }

    private void addDimension(Map<String, Map<String, String>> map, String name, Map<String, String> properties) {
        map.put(name, properties);
        
        hintsMap.put("dimension:" + name, "This is a dimension in Minecraft. Think about its unique properties.");
        
        Map<String, String> explanations = new HashMap<>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            explanations.put(property.getKey(), "This dimension " + property.getValue() + ".");
        }
        explanationsMap.put("dimension:" + name, explanations);
    }
    
    @Override
    public List<String> getCategories() {
        return new ArrayList<>(categoryData.keySet());
    }
    
    @Override
    public boolean hasCategory(String category) {
        return categoryData.containsKey(category);
    }
    
    @Override
    public List<String> getSubjects(String category) {
        if (!hasCategory(category)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(categoryData.get(category).keySet());
    }
    
    @Override
    public Map<String, String> getProperties(String category, String subject) {
        if (!hasCategory(category) || !categoryData.get(category).containsKey(subject)) {
            return null;
        }
        return categoryData.get(category).get(subject);
    }
    
    @Override
    public String getHint(String category, String subject) {
        return hintsMap.getOrDefault(category + ":" + subject, null);
    }
    
    @Override
    public String getExplanation(String category, String subject, String propertyKey) {
        if (!explanationsMap.containsKey(category + ":" + subject)) {
            return null;
        }
        
        Map<String, String> explanations = explanationsMap.get(category + ":" + subject);
        return explanations.getOrDefault(propertyKey, null);
    }
    
    @Override
    public String getFalseExplanation(String category, String subject) {
        return falseExplanationsMap.getOrDefault(category, "This statement is false in Minecraft.");
    }
    
    @Override
    public List<String> getFalseProperties(String category) {
        return falsePropertiesMap.getOrDefault(category, new ArrayList<>());
    }
    
    @Override
    public List<String> getTrueTemplates() {
        return trueTemplates;
    }
    
    @Override
    public List<String> getFalseTemplates() {
        return falseTemplates;
    }
    
    @Override
    public boolean addCategory(String category) {
        if (hasCategory(category)) {
            return false;
        }
        
        categoryData.put(category, new HashMap<>());
        falsePropertiesMap.put(category, new ArrayList<>());
        falseExplanationsMap.put(category, "This statement is false.");
        return true;
    }
    
    @Override
    public boolean addSubject(String category, String subject, Map<String, String> properties) {
        if (!hasCategory(category)) {
            return false;
        }
        
        Map<String, Map<String, String>> subjects = categoryData.get(category);
        if (subjects.containsKey(subject)) {
            return false;
        }
        
        subjects.put(subject, properties);
        return true;
    }
    
    @Override
    public boolean addFalseProperties(String category, List<String> properties) {
        if (!hasCategory(category)) {
            return false;
        }
        
        falsePropertiesMap.put(category, properties);
        return true;
    }
}