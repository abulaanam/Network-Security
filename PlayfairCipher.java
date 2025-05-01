import java.util.*;

public class PlayfairCipher {
    private static final int SIZE = 5;
    private char[][] keyMatrix;
    
    // Matrix
    public void generateKeyMatrix(String key) {
        key = key.replaceAll("J", "I"); 
        Set<Character> used = new LinkedHashSet<>();
        
        for (char c : key.toCharArray()) {
            if (Character.isUpperCase(c) && !used.contains(c)) {
                used.add(c);
            }
        }
        
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'J' && !used.contains(c)) {
                used.add(c);
            }
        }
        
        // 2D array
        keyMatrix = new char[SIZE][SIZE];
        Iterator<Character> iter = used.iterator();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                keyMatrix[i][j] = iter.next();
            }
        }
    }
    
    // Encrypt
    public String encrypt(String plaintext) {
        plaintext = prepareText(plaintext);
        StringBuilder ciphertext = new StringBuilder();
        
        for (int i = 0; i < plaintext.length(); i += 2) {
            char a = plaintext.charAt(i);
            char b = plaintext.charAt(i + 1);
            int[] posA = findPosition(a);
            int[] posB = findPosition(b);
            
            if (posA[0] == posB[0]) { 
                ciphertext.append(keyMatrix[posA[0]][(posA[1] + 1) % SIZE]);
                ciphertext.append(keyMatrix[posB[0]][(posB[1] + 1) % SIZE]);
            } else if (posA[1] == posB[1]) { 
                ciphertext.append(keyMatrix[(posA[0] + 1) % SIZE][posA[1]]);
                ciphertext.append(keyMatrix[(posB[0] + 1) % SIZE][posB[1]]);
            } else { 
                ciphertext.append(keyMatrix[posA[0]][posB[1]]);
                ciphertext.append(keyMatrix[posB[0]][posA[1]]);
            }
        }
        return ciphertext.toString();
    }
    
    // Decrypt ciphertext
    public String decrypt(String ciphertext) {
        StringBuilder plaintext = new StringBuilder();
        
        for (int i = 0; i < ciphertext.length(); i += 2) {
            char a = ciphertext.charAt(i);
            char b = ciphertext.charAt(i + 1);
            int[] posA = findPosition(a);
            int[] posB = findPosition(b);
            
            if (posA[0] == posB[0]) { 
                plaintext.append(keyMatrix[posA[0]][(posA[1] + SIZE - 1) % SIZE]);
                plaintext.append(keyMatrix[posB[0]][(posB[1] + SIZE - 1) % SIZE]);
            } else if (posA[1] == posB[1]) { 
                plaintext.append(keyMatrix[(posA[0] + SIZE - 1) % SIZE][posA[1]]);
                plaintext.append(keyMatrix[(posB[0] + SIZE - 1) % SIZE][posB[1]]);
            } else { 
                plaintext.append(keyMatrix[posA[0]][posB[1]]);
                plaintext.append(keyMatrix[posB[0]][posA[1]]);
            }
        }
        return cleanDecryptedText(plaintext.toString());
    }
    
    private String prepareText(String text) {
        text = text.replaceAll("[^A-Z]", "");
        text = text.replace("J", "I");
        StringBuilder prepared = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            prepared.append(text.charAt(i));
            if (i < text.length() - 1 && text.charAt(i) == text.charAt(i + 1)) {
                prepared.append('X'); // Insert 'X' between duplicate letters
            }
        }
        if (prepared.length() % 2 == 1) {
            prepared.append('X'); // Make sure it's even-length
        }
        return prepared.toString();
    }
    
    private int[] findPosition(char c) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (keyMatrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
    
    private String cleanDecryptedText(String text) {
        return text.replaceAll("(?<=[A-Z])X(?=[A-Z])", ""); 
    }
    
    public static void main(String[] args) {
        PlayfairCipher cipher = new PlayfairCipher();
        String key = "RAYQUAZA";
        String plaintext = "POKEMON TOWER DEFENSE\n" + //
                "YOUR MISSION IN THIS FUN STRATEGY TOWER DEFENSE GAME IS TO HELP PROFESSOR OAK TO STOP ATTACKS\n" + //
                "OF WILD RATTATA. SET OUT ON YOUR OWN POKEMON JOURNEY, TO CATCH AND TRAIN ALL POKEMON AND\n" + //
                "TRY TO SOLVE THE MYSTERY BEHIND THESE ATTACKS. YOU MUST PLACE POKEMON CHARACTERS\n" + //
                "STRATEGICALLY ON THE BATTLEFIELD SO THAT THEY STOP ALL WAVES OF ENEMY ATTACKER\n" + //
                "DURING THE BATTLE YOU WILL LEVEL UP AND EVOLVE YOUR POKEMON. YOU CAN ALSO CAPTURE OTHER\n" + //
                "POKEMON DURING THE BATTLE AND ADD THEM TO YOUR TEAM. USE YOUR MOUSE TO PLAY THE GAME.\n" + //
                "GOOD LUCK";
        
        cipher.generateKeyMatrix(key);
        String encrypted = cipher.encrypt(plaintext);
        String decrypted = cipher.decrypt(encrypted);
        
        System.out.println("PlainText: " + plaintext);
        System.out.println("Ciphertext: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}