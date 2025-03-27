package com.notmarra.notchat.listeners.chat.modules;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.listeners.NotChatListener;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notlib.utils.command.NotCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

public class FilterChatListener extends NotChatListener {
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

    public FilterChatListener(NotChat plugin) { super(plugin); }

    @Override
    public String getId() { return ID; }

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
                    getLogger().warn(ChatF.of("Invalid unicode range: " + range, ChatF.C_ORANGE).build());
                }
            }
        }
    }

    @Override
    public List<NotCommand> notCommands() {
        return List.of(
            filterCommand()
        );
    }

    NotCommand filterCommand() {
        NotCommand command = NotCommand.of("filter", cmd -> {
            ChatF.empty()
                .appendBold("[NotChat " + getId().toUpperCase() + "] Commands:")
                .nl()
                .append("/filter reload - Reload the configuration")
                .sendTo(cmd.getPlayer());
        });

        command.literalArg("reload", arg -> {
            Player player = arg.getPlayer();

            if (player.hasPermission(PERMISSION_RELOAD)) {
                reloadConfig();

                ChatF.empty()
                    .appendBold(getId().toUpperCase() + " configuration reloaded!", ChatF.C_GREEN)
                    .sendTo(player);
            } else {
                ChatF.empty()
                    .appendBold("You don't have permission to use this command!", ChatF.C_RED)
                    .sendTo(player);
            }
        });

        return command;
    }

    public FilterResult processMessage(Player player, String message) {
        if (!player.hasPermission(bypassPermission)) {
            return new FilterResult(false, message);
        }
        
        String filteredMessage = message;
        boolean block = false;
        
        // Check for spam
        if (spamEnabled) {
            SpamCheckResult spamResult = checkSpam(player, message);
            if (spamResult.isSpam()) {
                if (logFiltered) {
                    getLogger().info("Blocked spam message from " + player.getName() + ": " + message);
                }
                return new FilterResult(true, null, spamResult.getMessage());
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
                    return new FilterResult(true, null, adBlockMessage);
                } else if (adAction.equals("REPLACE")) {
                    filteredMessage = adResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Filtered advertising from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (adAction.equals("WARN")) {
                    ChatF.empty()
                        .append(adBlockMessage, ChatF.C_RED)
                        .sendTo(player);
                }
            }
        }
        
        // Check for excessive caps
        if (capsEnabled && message.length() >= capsMinLength) {
            CapsCheckResult capsResult = checkCaps(message);
            if (capsResult.isExcessiveCaps()) {
                if (capsAction.equals("BLOCK")) {
                    if (logFiltered) {
                        getLogger().info("Blocked excessive caps from " + player.getName() + ": " + message);
                    }
                    return new FilterResult(true, null, "Please don't use excessive caps.");
                } else if (capsAction.equals("CONVERT")) {
                    filteredMessage = capsResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Converted caps from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (capsAction.equals("WARN")) {
                    ChatF.empty()
                        .append("Please don't use excessive caps.", ChatF.C_RED)
                        .sendTo(player);
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
                    return new FilterResult(true, null, "Please don't use excessive special characters.");
                } else if (unicodeAction.equals("REPLACE")) {
                    filteredMessage = unicodeResult.getFilteredMessage();
                    if (logFiltered) {
                        getLogger().info("Replaced unicode from " + player.getName() + ": " + message + " -> " + filteredMessage);
                    }
                } else if (unicodeAction.equals("WARN")) {
                    ChatF.empty()
                        .append("Please don't use excessive special characters.", ChatF.C_RED)
                        .sendTo(player);
                }
            }
        }
        
        return new FilterResult(block, filteredMessage);
    }
    
    private SpamCheckResult checkSpam(Player player, String message) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        
        // Get player's message history or create a new one
        List<MessageData> messageHistory = playerMessages.computeIfAbsent(playerId, k -> new ArrayList<>());
        
        // Check cooldown
        if (!messageHistory.isEmpty()) {
            MessageData lastMessage = messageHistory.get(messageHistory.size() - 1);
            if (currentTime - lastMessage.getTimestamp() < spamCooldown) {
                return new SpamCheckResult(true, "Please wait before sending another message.");
            }
        }
        
        // Check similar messages
        String normalizedMessage = message.toLowerCase();
        int similarCount = 0;
        
        for (int i = messageHistory.size() - 1; i >= 0 && i >= messageHistory.size() - maxSimilarMessages; i--) {
            if (calculateSimilarity(normalizedMessage, messageHistory.get(i).getMessage().toLowerCase()) > similarityThreshold) {
                similarCount++;
            }
        }
        
        if (similarCount >= maxSimilarMessages - 1) {
            return new SpamCheckResult(true, "Please don't repeat similar messages.");
        }
        
        // Update message history
        messageHistory.add(new MessageData(message, currentTime));
        
        // Keep history size manageable
        while (messageHistory.size() > 10) {
            messageHistory.remove(0);
        }
        
        return new SpamCheckResult(false, null);
    }
    
    private AdvertisingCheckResult checkAdvertising(String message) {
        String lowerMessage = message.toLowerCase();
        boolean containsAdvertising = false;
        String filteredMessage = message;
        
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
        
        return new AdvertisingCheckResult(containsAdvertising, filteredMessage);
    }
    
    private CapsCheckResult checkCaps(String message) {
        if (message.length() < capsMinLength) {
            return new CapsCheckResult(false, message);
        }
        
        int upperCount = 0;
        int letterCount = 0;
        
        for (char c : message.toCharArray()) {
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
                return new CapsCheckResult(true, message.toLowerCase());
            }
        }
        
        return new CapsCheckResult(false, message);
    }
    
    private UnicodeCheckResult checkUnicode(String message) {
        int unicodeCount = 0;
        int totalCount = 0;
        StringBuilder filtered = new StringBuilder();
        
        for (char c : message.toCharArray()) {
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
        
        return new UnicodeCheckResult(excessive, filtered.toString());
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
        private final String filteredMessage;
        private final String blockMessage;
        
        public FilterResult(boolean blocked, String filteredMessage) {
            this(blocked, filteredMessage, null);
        }
        
        public FilterResult(boolean blocked, String filteredMessage, String blockMessage) {
            this.blocked = blocked;
            this.filteredMessage = filteredMessage;
            this.blockMessage = blockMessage;
        }
        
        public boolean isBlocked() {
            return blocked;
        }
        
        public String getFilteredMessage() {
            return filteredMessage;
        }
        
        public String getBlockMessage() {
            return blockMessage;
        }
    }
    
    private static class MessageData {
        private final String message;
        private final long timestamp;
        
        public MessageData(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    private static class SpamCheckResult {
        private final boolean spam;
        private final String message;
        
        public SpamCheckResult(boolean spam, String message) {
            this.spam = spam;
            this.message = message;
        }
        
        public boolean isSpam() {
            return spam;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private static class AdvertisingCheckResult {
        private final boolean containsAdvertising;
        private final String filteredMessage;
        
        public AdvertisingCheckResult(boolean containsAdvertising, String filteredMessage) {
            this.containsAdvertising = containsAdvertising;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean containsAdvertising() {
            return containsAdvertising;
        }
        
        public String getFilteredMessage() {
            return filteredMessage;
        }
    }
    
    private static class CapsCheckResult {
        private final boolean excessiveCaps;
        private final String filteredMessage;
        
        public CapsCheckResult(boolean excessiveCaps, String filteredMessage) {
            this.excessiveCaps = excessiveCaps;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean isExcessiveCaps() {
            return excessiveCaps;
        }
        
        public String getFilteredMessage() {
            return filteredMessage;
        }
    }
    
    private static class UnicodeCheckResult {
        private final boolean excessiveUnicode;
        private final String filteredMessage;
        
        public UnicodeCheckResult(boolean excessiveUnicode, String filteredMessage) {
            this.excessiveUnicode = excessiveUnicode;
            this.filteredMessage = filteredMessage;
        }
        
        public boolean isExcessiveUnicode() {
            return excessiveUnicode;
        }
        
        public String getFilteredMessage() {
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