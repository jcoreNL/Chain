package nl.johannisk.node.service;

import org.springframework.stereotype.Service;

@Service
public class ParameterService {
    private int difficulty;
    private String digest;

    public ParameterService() {
        difficulty = 0;
        digest = "SHA-256";
    }

    public void setDifficulty(int i) {
        this.difficulty = i;
    }

    public void increaseEncryption() {
        this.difficulty = 1;
        this.digest = "SHA-1";
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getEncryptionAlgorithm() {
        return digest;
    }
}
