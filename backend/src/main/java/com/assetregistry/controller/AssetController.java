package com.assetregistry.controller;

import com.assetregistry.model.AssetRecord;
import com.assetregistry.model.TransferEvent;
import com.assetregistry.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAsset(
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            String fileName = file.getOriginalFilename();
            
            AssetRecord asset = assetService.uploadAsset(fileContent, fileName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hash", asset.getHash());
            response.put("ownerToken", asset.getOwnerToken());
            response.put("fileName", asset.getFileName());
            response.put("timestamp", asset.getTimestamp());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyAsset(
            @RequestParam String hash,
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            boolean isValid = assetService.verifyAsset(hash, fileContent);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hash", hash);
            response.put("isValid", isValid);
            response.put("message", isValid ?
                    "✓ FILE VERIFIED! The provided file matches the stored asset." :
                    "✗ VERIFICATION FAILED. The asset hash is not registered or the file content is different.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(@RequestParam String hash) {
        try {
            List<TransferEvent> history = assetService.getAssetHistory(hash);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hash", hash);
            response.put("history", history);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferOwnership(
            @RequestBody Map<String, String> request) {
        try {
            String hash = request.get("hash");
            String currentOwnerToken = request.get("currentOwnerToken");
            String newOwnerName = request.get("newOwnerName");

            if (hash == null || currentOwnerToken == null || newOwnerName == null) {
                throw new IllegalArgumentException("hash, currentOwnerToken, and newOwnerName are required");
            }

            String newOwnerToken = assetService.transferOwnership(hash, currentOwnerToken, newOwnerName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hash", hash);
            response.put("newOwnerToken", newOwnerToken);
            response.put("message", "Ownership transferred successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listUserAssets(@RequestParam String ownerToken) {
        try {
            List<AssetRecord> assets = assetService.getAssetsByOwnerToken(ownerToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("assetCount", assets.size());
            response.put("assets", assets.stream().map(a -> {
                Map<String, Object> assetMap = new HashMap<>();
                assetMap.put("hash", a.getHash());
                assetMap.put("fileName", a.getFileName());
                assetMap.put("timestamp", a.getTimestamp());
                return assetMap;
            }).toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

