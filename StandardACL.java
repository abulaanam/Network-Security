import java.io.*;
import java.util.*;

public class StandardACLRouterSim {
    static class ACLRule {
        String action, sourceIp, wildcard;

        ACLRule(String action, String sourceIp, String wildcard) {
            this.action = action;
            this.sourceIp = sourceIp;
            this.wildcard = wildcard;
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

    static void processStandardACL(String aclFile, String packetsFile) throws IOException {
        List<ACLRule> aclRules = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(aclFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("access-list")) {
                    aclRules.add(new ACLRule(parts[2], parts[3], parts[4]));
                }
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(packetsFile))) {
            String packet;
            while ((packet = br.readLine()) != null) {
                String action = "deny";
                for (ACLRule rule : aclRules) {
                    if (matchesRule(packet, rule.sourceIp, rule.wildcard)) {
                        action = rule.action;
                        break;
                    }
                }
                System.out.println("Packet from " + packet + " " + action);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        processStandardACL("acl_rules_standard.txt", "packets_standard.txt");
    }
}
