package com.notmarra.notchat.listeners;

import com.notmarra.notchat.NotChat;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;
import com.notmarra.notlib.utils.command.arguments.NotLiteralArg;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FilterListenerNew extends BaseNotListener {
    public static final String ID = "filter";

    public static final String PERMISSION_BYPASS = "notchat.filter.bypass";
    public static final String PERMISSION_RELOAD = "notchat.filter.reload";

    // Filter configs
    private boolean spamEnabled;
    private boolean advertisingEnabled;
    private boolean capsEnabled;
    private boolean unicodeEnabled;
    
    // Spam filter
    private long spamCooldown;
    private int maxSimilarMessages;
    private double similarityThreshold;
    private final Map<UUID, List<MessageData>> playerMessages = new ConcurrentHashMap<>();
    
    // Advertising filter
    private final Set<String> whitelistedUrls = new HashSet<>();
    private boolean detectIpAddresses;
    private String adAction;
    private String adBlockMessage;
    private final Pattern URL_PATTERN = Pattern.compile("(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
    private final Pattern IP_PATTERN = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
    
    // Caps filter
    private int capsMinLength;
    private int capsMaxPercentage;
    private String capsAction;
    
    // Unicode filter
    private boolean blockUnicode;
    private int unicodeMaxPercentage;
    private List<UnicodeRange> allowedRanges = new ArrayList<>();
    private String unicodeAction;
    private char unicodeReplacement;
    
    // General settings
    private String bypassPermission;
    private boolean logFiltered;

    public FilterListenerNew(NotChat plugin) {
        super(plugin);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean hasConfig() {
        return true;
    }

    @Override
    public void loadConfig() {
        // General filter settings
        bypassPermission = config.getString("general.bypass_permission", PERMISSION_BYPASS);
        logFiltered = config.getBoolean("general.log_filtered", true);
        
        // Load spam filter settings
        spamEnabled = config.getBoolean("spam.enabled", true);
        spamCooldown = config.getLong("spam.cooldown", 1000);
        maxSimilarMessages = config.getInt("spam.max_similar", 3);
        similarityThreshold = config.getDouble("spam.similarity_threshold", 0.8);
        
        // Load advertising filter settings
        advertisingEnabled = config.getBoolean("advertising.enabled", true);
        whitelistedUrls.clear();
        whitelistedUrls.addAll(config.getStringList("advertising.whitelisted_urls"));
        detectIpAddresses = config.getBoolean("advertising.detect_ip_addresses", true);
        adAction = config.getString("advertising.action", "BLOCK").toUpperCase();
        adBlockMessage = config.getString("advertising.block_message", "<red>Advertising is not allowed.");
        
        // Load caps filter settings
        capsEnabled = config.getBoolean("caps.enabled", true);
        capsMinLength = config.getInt("caps.min_length", 5);
        capsMaxPercentage = config.getInt("caps.max_percentage", 40);
        capsAction = config.getString("caps.action", "CONVERT").toUpperCase();
        
        // Load unicode filter settings
        unicodeEnabled = config.getBoolean("unicode.enabled", true);
        blockUnicode = config.getBoolean("unicode.block_unicode", false);
        unicodeMaxPercentage = config.getInt("unicode.max_percentage", 20);
        unicodeAction = config.getString("unicode.action", "REPLACE").toUpperCase();
        unicodeReplacement = config.getString("unicode.replacement_char", "?").charAt(0);
        
        // Load allowed unicode ranges
        allowedRanges.clear();
        for (String range : config.getStringList("unicode.allowed_ranges")) {
            String[] parts = range.split("-");
            if (parts.length == 2) {
                try {
                    int start = Integer.parseInt(parts[0], 16);
                    int end = Integer.parseInt(parts[1], 16);
                    allowedRanges.add(new UnicodeRange(start, end));
                } catch (NumberFormatException e) {
                    getLogger().warning("Invalid unicode range: " + range);
                }
            }
        }
    }

    @Override
    public List<NotCommand> getNotCommands() {
        return List.of(
            filterCommand()
        );
    }

    NotCommand filterCommand() {
        NotCommand ping = new NotCommand("filter");

        ping.onExecute(ctx -> {
            ChatF.empty()
                .appendBold("[NotChat " + getId().toUpperCase() + "] Commands:")
                .nl()
                .append("/filter reload - Reload the configuration")
                .sendTo(ctx.getSource().getExecutor());
        });

        NotLiteralArg reload = new NotLiteralArg("reload");

        reload.onExecute(ctx -> {
            Entity entity = ctx.getSource().getExecutor();

            if (entity.hasPermission(PERMISSION_RELOAD)) {
                reloadConfig();

                ChatF.empty()
                    .appendBold(getId().toUpperCase() + " configuration reloaded!", ChatF.C_GREEN)
                    .sendTo(entity);
            } else {
                ChatF.empty()
                    .appendBold("You don't have permission to use this command!", ChatF.C_RED)
                    .sendTo(entity);
            }
        });

        ping.addArg(reload);

        return ping;
    }

    private ChatF toError(String message) {
        return ChatF.empty().appendBold(message, ChatF.C_RED);
    }

    public FilterResult processMessage(Player player, Component message) {
        return processMessage(player, ChatF.of(message));
    }

    public FilterResult processMessage(Player player, String message) {
        return processMessage(player, ChatF.of(message));
    }

    public FilterResult processMessage(Player player, ChatF message) {
        String builtMessage = message.buildString();
        if (player.hasPermission(bypassPermission)) {
            return FilterResult.ofFiltered(message);
        }
        
        ChatF filteredMessage = message;
        
        // Check for spam
        if (spamEnabled) {
            SpamCheckResult spamResult = checkSpam(player, message);
            if (spamResult.isSpam()) {
                if (logFiltered) {
                    getLogger().info("Blocked spam message from " + player.getName() + ": " + message);
                }
                return FilterResult.ofBlocked(spamResult.getMessage());
            }
        }
        
        // Check for advertising
        if (advertisingEnabled) {
            AdvertisingCheckResult adResult = checkAdvertising(message);
            if (adResult.containsAdvertising()) {
                if (adAction.equals("BLOCK")) {
                    if (logFiltered) {
                        getLogger().info("Blocked advertising from " + player.getName() + ": " + message);
                    }
                    return FilterResult.ofBlocked(toError(adBlockMessage));
                } else if (adAction.equals("REPLACE")) {
                    filteredMessage = adResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Filtered advertising from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (adAction.equals("WARN")) {
                    return FilterResult.ofBlocked(toError(adBlockMessage));
                }
            }
        }
        
        // Check for excessive caps
        if (capsEnabled && builtMessage.length() >= capsMinLength) {
            CapsCheckResult capsResult = checkCaps(message);
            if (capsResult.isExcessiveCaps()) {
                if (capsAction.equals("BLOCK")) {
                    if (logFiltered) {
                        getLogger().info("Blocked excessive caps from " + player.getName() + ": " + message);
                    }
                    return FilterResult.ofBlocked(toError("Please don't use excessive caps."));
                } else if (capsAction.equals("CONVERT")) {
                    filteredMessage = capsResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Converted caps from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (capsAction.equals("WARN")) {
                    return FilterResult.ofBlocked(toError("Please don't use excessive caps."));
                }
            }
        }
        
        // Check for unicode characters
        if (unicodeEnabled) {
            UnicodeCheckResult unicodeResult = checkUnicode(filteredMessage);
            if (unicodeResult.isExcessiveUnicode()) {
                if (unicodeAction.equals("BLOCK")) {
                    if (logFiltered) {
                        getLogger().info("Blocked unicode from " + player.getName() + ": " + message);
                    }
                    return FilterResult.ofBlocked(toError("Please don't use excessive special characters."));
                } else if (unicodeAction.equals("REPLACE")) {
                    filteredMessage = unicodeResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Replaced unicode from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (unicodeAction.equals("WARN")) {
                    return FilterResult.ofBlocked(toError("Please don't use excessive special characters."));
                }
            }
        }
        
        return FilterResult.ofFiltered(filteredMessage);
    }
    
    private SpamCheckResult checkSpam(Player player, ChatF message) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Get player's message history or create a new one
        List<MessageData> messageHistory = playerMessages.computeIfAbsent(playerId, k -> new ArrayList<>());
        
        // Check cooldown
        if (!messageHistory.isEmpty()) {
            MessageData lastMessage = messageHistory.get(messageHistory.size() - 1);
            if (currentTime - lastMessage.getTimestamp() < spamCooldown) {
                return new SpamCheckResult(true, toError("Please wait before sending another message."));
            }
        }
        
        // Check similar messages
        String normalizedMessage = message.buildString().toLowerCase();
        int similarCount = 0;
        
        for (int i = messageHistory.size() - 1; i >= 0 && i >= messageHistory.size() - maxSimilarMessages; i--) {
            String lastMessage = messageHistory.get(i).getMessage().buildString().toLowerCase();
            if (calculateSimilarity(normalizedMessage, lastMessage) > similarityThreshold) {
                similarCount++;
            }
        }
        
        if (similarCount >= maxSimilarMessages - 1) {
            return new SpamCheckResult(true, toError("Please don't repeat similar messages."));
        }
        
        // Update message history
        messageHistory.add(new MessageData(message, currentTime));
        
        // Keep history size manageable
        while (messageHistory.size() > 10) {
            messageHistory.remove(0);
        }
        
        return new SpamCheckResult(false, null);
    }
    
    private AdvertisingCheckResult checkAdvertising(ChatF message) {
        String originalMessage = message.buildString();
        String lowerMessage = originalMessage.toLowerCase();
        boolean containsAdvertising = false;
        String filteredMessage = originalMessage;
        
        // Check for URLs
        Matcher urlMatcher = URL_PATTERN.matcher(lowerMessage);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            boolean isWhitelisted = false;
            
            for (String whitelistedUrl : whitelistedUrls) {
                if (url.contains(whitelistedUrl.toLowerCase())) {
                    isWhitelisted = true;
                    break;
                }
            }
            
            if (!isWhitelisted) {
                containsAdvertising = true;
                filteredMessage = filteredMessage.replaceAll("(?i)" + Pattern.quote(url), "[filtered]");
            }
        }
        
        // Check for IP addresses
        if (detectIpAddresses) {
            Matcher ipMatcher = IP_PATTERN.matcher(lowerMessage);
            while (ipMatcher.find()) {
                containsAdvertising = true;
                filteredMessage = filteredMessage.replaceAll(Pattern.quote(ipMatcher.group()), "[filtered]");
            }
        }
        
        return new AdvertisingCheckResult(containsAdvertising, ChatF.of(filteredMessage));
    }
    
    private CapsCheckResult checkCaps(ChatF message) {
        if (message.buildString().length() < capsMinLength) {
            return new CapsCheckResult(false, message);
        }
        
        int upperCount = 0;
        int letterCount = 0;
        
        for (char c : message.buildString().toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    upperCount++;
                }
            }
        }
        
        if (letterCount > 0) {
            double percentage = (double) upperCount / letterCount * 100;
            if (percentage > capsMaxPercentage) {
                return new CapsCheckResult(true, ChatF.of(message.buildString().toLowerCase()));
            }
        }
        
        return new CapsCheckResult(false, message);
    }
    
    private UnicodeCheckResult checkUnicode(ChatF message) {
        int unicodeCount = 0;
        int totalCount = 0;
        StringBuilder filtered = new StringBuilder();
        
        for (char c : message.buildString().toCharArray()) {
            totalCount++;
            
            if (c > 127) {
                boolean allowed = false;
                
                // Check if the character is in an allowed range
                for (UnicodeRange range : allowedRanges) {
                    if (c >= range.getStart() && c <= range.getEnd()) {
                        allowed = true;
                        break;
                    }
                }
                
                if (!allowed) {
                    unicodeCount++;
                    filtered.append(unicodeReplacement);
                } else {
                    filtered.append(c);
                }
            } else {
                filtered.append(c);
            }
        }
        
        double percentage = totalCount > 0 ? (double) unicodeCount / totalCount * 100 : 0;
        boolean excessive = percentage > unicodeMaxPercentage || (blockUnicode && unicodeCount > 0);
        
        return new UnicodeCheckResult(excessive, ChatF.of(filtered.toString()));
    }

    private double calculateSimilarity(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        
        int distance = costs[s2.length()];
        int maxLength = Math.max(s1.length(), s2.length());
        
        return maxLength > 0 ? 1.0 - (double) distance / maxLength : 1.0;
    }
    
    // Inner classes for filter results
    
    public static class FilterResult {
        private final boolean blocked;
        private final ChatF filteredMessage;
        private final ChatF blockMessage;
        
        public FilterResult(boolean blocked, ChatF filteredMessage, ChatF blockMessage) {
            this.blocked = blocked;
            this.filteredMessage = filteredMessage;
            this.blockMessage = blockMessage;
        }
        
        public boolean isBlocked() {
            return blocked;
        }
        
        public ChatF getFilteredMessage() {
            return filteredMessage;
        }
        
        public ChatF getBlockMessage() {
            return blockMessage;
        }

        public static FilterResult ofBlocked(ChatF blockMessage) {
            return new FilterResult(true, null, blockMessage);
        }

        public static FilterResult ofFiltered(ChatF filteredMessage) {
            return new FilterResult(false, filteredMessage, null);
        }
    }
    
    private static class MessageData {
        private final ChatF message;
        private final long timestamp;
        
        public MessageData(ChatF message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public ChatF getMessage() {
            return message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    private static class SpamCheckResult {
        private final boolean spam;
        private final ChatF message;
        
        public SpamCheckResult(boolean spam, ChatF message) {
            this.spam = spam;
            this.message = message;
        }
        
        public boolean isSpam() {
            return spam;
        }
        
        public ChatF getMessage() {
            return message;
        }
    }
    
    private static class AdvertisingCheckResult {
        private final boolean containsAdvertising;
        private final ChatF filteredMessage;
        
        public AdvertisingCheckResult(boolean containsAdvertising, ChatF filteredMessage) {
            this.containsAdvertising = containsAdvertising;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean containsAdvertising() {
            return containsAdvertising;
        }
        
        public ChatF getFilteredMessage() {
            return filteredMessage;
        }
    }
    
    private static class CapsCheckResult {
        private final boolean excessiveCaps;
        private final ChatF filteredMessage;
        
        public CapsCheckResult(boolean excessiveCaps, ChatF filteredMessage) {
            this.excessiveCaps = excessiveCaps;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean isExcessiveCaps() {
            return excessiveCaps;
        }
        
        public ChatF getFilteredMessage() {
            return filteredMessage;
        }
    }
    
    private static class UnicodeCheckResult {
        private final boolean excessiveUnicode;
        private final ChatF filteredMessage;
        
        public UnicodeCheckResult(boolean excessiveUnicode, ChatF filteredMessage) {
            this.excessiveUnicode = excessiveUnicode;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean isExcessiveUnicode() {
            return excessiveUnicode;
        }
        
        public ChatF getFilteredMessage() {
            return filteredMessage;
        }
    }
    
    private static class UnicodeRange {
        private final int start;
        private final int end;
        
        public UnicodeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
    }
}