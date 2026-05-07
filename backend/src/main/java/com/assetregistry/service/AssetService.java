package com.assetregistry.service;

import com.assetregistry.model.AssetRecord;
import com.assetregistry.model.TransferEvent;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssetService {
    private final Map<String, AssetRecord> assetRegistry = new HashMap<>();
    
    public AssetRecord uploadAsset(byte[] fileContent, String fileName) throws Exception {
        String hash = generateSHA256Hash(fileContent);
        String ownerToken = UUID.randomUUID().toString();

        AssetRecord record = new AssetRecord();
        record.setHash(hash);
        record.setFileName(fileName);
        record.setOwnerToken(ownerToken);
        record.setTimestamp(LocalDateTime.now());
        record.setHistory(new ArrayList<>());
        record.getHistory().add(new TransferEvent("SYSTEM", ownerToken, LocalDateTime.now()));

        assetRegistry.put(hash, record);
        return record;
    }
    
    public boolean verifyAsset(String hash, byte[] fileContent) throws Exception {
        String computedHash = generateSHA256Hash(fileContent);
        return assetRegistry.containsKey(hash) && computedHash.equals(hash);
    }
    
    public List<TransferEvent> getAssetHistory(String hash) throws Exception {
        AssetRecord record = assetRegistry.get(hash);
        if (record == null) {
            throw new Exception("Asset not found for hash: " + hash);
        }
        return new ArrayList<>(record.getHistory());
    }
    
    public String transferOwnership(String hash, String currentOwnerToken, String newOwnerName) throws Exception {
        AssetRecord record = assetRegistry.get(hash);
        if (record == null) {
            throw new Exception("Asset not found for hash: " + hash);
        }
        if (!record.getOwnerToken().equals(currentOwnerToken)) {
            throw new Exception("Unauthorized: currentOwnerToken does not match");
        }

        TransferEvent event = new TransferEvent();
        event.setFrom(currentOwnerToken);
        event.setTo(newOwnerName);
        event.setTimestamp(LocalDateTime.now());
        record.getHistory().add(event);

        String newOwnerToken = UUID.randomUUID().toString();
        record.setOwnerToken(newOwnerToken);
        return newOwnerToken;
    }
    
    public List<AssetRecord> getAssetsByOwnerToken(String ownerToken) {
        List<AssetRecord> assets = new ArrayList<>();
        for (AssetRecord record : assetRegistry.values()) {
            if (record.getOwnerToken().equals(ownerToken)) {
                assets.add(record);
            }
        }
        return assets;
    }
    
    public boolean deleteAsset(String hash, String ownerToken) throws Exception {
        AssetRecord record = assetRegistry.get(hash);
        if (record == null) {
            throw new Exception("Asset not found");
        }
        if (!record.getOwnerToken().equals(ownerToken)) {
            throw new Exception("Unauthorized: Invalid owner token");
        }
        assetRegistry.remove(hash);
        return true;
    }
    
    private String generateSHA256Hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
