package com.assetregistry.service;

import com.assetregistry.model.Asset;
import org.springframework.stereotype.Service;
// import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssetService {
    private final Map<String, Asset> assetRegistry = new HashMap<>();
    
    public Asset uploadAsset(byte[] fileContent, String fileName, String fileType) throws Exception {
        // Generate SHA-256 hash
        String hash = generateSHA256Hash(fileContent);
        
        // Generate unique owner token (secure secret)
        String ownerToken = UUID.randomUUID().toString();
        
        // Create asset record
        Asset asset = new Asset();
        asset.setHash(hash);
        asset.setFileName(fileName);
        asset.setFileSize((long) fileContent.length);
        asset.setFileType(fileType);
        asset.setOwnerToken(ownerToken);
        asset.setUploadedAt(LocalDateTime.now());
        asset.setFileContent(fileContent);
        
        // Store in registry
        assetRegistry.put(hash, asset);
        
        return asset;
    }
    
    public Asset getAsset(String hash, String ownerToken) throws Exception {
        Asset asset = assetRegistry.get(hash);
        if (asset == null) {
            throw new Exception("Asset not found with hash: " + hash);
        }
        if (!asset.getOwnerToken().equals(ownerToken)) {
            throw new Exception("Unauthorized: Invalid owner token");
        }
        return asset;
    }
    
    public List<Asset> getAssetsByToken(String ownerToken) {
        List<Asset> userAssets = new ArrayList<>();
        for (Asset asset : assetRegistry.values()) {
            if (asset.getOwnerToken().equals(ownerToken)) {
                userAssets.add(asset);
            }
        }
        return userAssets;
    }
    
    public boolean verifyAsset(String hash, byte[] fileContent) throws Exception {
        String computedHash = generateSHA256Hash(fileContent);
        return computedHash.equals(hash);
    }
    
    public boolean deleteAsset(String hash, String ownerToken) throws Exception {
        Asset asset = assetRegistry.get(hash);
        if (asset == null) {
            throw new Exception("Asset not found");
        }
        if (!asset.getOwnerToken().equals(ownerToken)) {
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
