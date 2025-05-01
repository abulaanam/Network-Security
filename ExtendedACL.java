import java.io.*;
import java.util.*;

public class ExtendedACL {
    static class ACLRule {
        String action, sourceIp, wildcard, destIp, destWildcard;
        Integer portStart, portEnd;

        ACLRule(String action, String sourceIp, String wildcard, String destIp, String destWildcard, Integer portStart, Integer portEnd) {
            this.action = action;
            this.sourceIp = sourceIp;
            this.wildcard = wildcard;
            this.destIp = destIp;
            this.destWildcard = destWildcard;
            this.portStart = portStart;
            this.portEnd = portEnd;
        }
    }

    static boolean matchesRule(String ip, String aclIp, String wildcard) {
        String[] ipParts = ip.split("\\.");
        String[] aclParts = aclIp.split("\\.");
        String[] wildcardParts = wildcard.split("\\.");
        
        for (int i = 0; i < 4; i++) {
            if ((Integer.parseInt(ipParts[i]) & ~Integer.parseInt(wildcardParts[i])) != 
                (Integer.parseInt(aclParts[i]) & ~Integer.parseInt(wildcardParts[i]))) {
                return false;
            }
        }
        return true;
    }

    static void processExtendedACL(String aclFile, String packetsFile) throws IOException {
        List<ACLRule> aclRules = new ArrayList<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(aclFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");  // Handle variable spacing
    
                // Ignore non-access-list lines
                if (parts.length < 7 || !parts[0].equals("access-list")) {
                    continue;
                }
    
                String action = parts[2];
                String srcIp = parts[4], srcWildcard = parts[5];
                String destIp = parts[6], destWildcard = parts[7];
                Integer portStart = null, portEnd = null;
    
                // If "range" is present, extract port range safely
                if (parts.length > 8 && "range".equals(parts[8]) && parts.length > 9) {
                    String[] range = parts[9].split("-");
                    if (range.length == 2) {
                        portStart = Integer.parseInt(range[0]);
                        portEnd = Integer.parseInt(range[1]);
                    }
                }
    
                aclRules.add(new ACLRule(action, srcIp, srcWildcard, destIp, destWildcard, portStart, portEnd));
            }
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(packetsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] packetParts = line.trim().split("\\s+");
                if (packetParts.length < 3) {
                    System.out.println("Skipping malformed packet: " + line);
                    continue;
                }
    
                String srcIp = packetParts[0], destIp = packetParts[1];
                int port = Integer.parseInt(packetParts[2]);
                String action = "deny";
    
                for (ACLRule rule : aclRules) {
                    if (matchesRule(srcIp, rule.sourceIp, rule.wildcard) &&
                        matchesRule(destIp, rule.destIp, rule.destWildcard) &&
                        (rule.portStart == null || (port >= rule.portStart && port <= rule.portEnd))) {
                        action = rule.action;
                        break;
                    }
                }
    
                System.out.println("Packet from " + srcIp + " to " + destIp + " on port " + port + " " + action);
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        processExtendedACL("acl_rules_extended.txt", "extended_packets.txt");
    }
}
