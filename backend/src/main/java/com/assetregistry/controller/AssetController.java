package com.assetregistry.controller;

import com.assetregistry.model.Asset;
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
            String fileType = file.getContentType();
            
            Asset asset = assetService.uploadAsset(fileContent, fileName, fileType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hash", asset.getHash());
            response.put("ownerToken", asset.getOwnerToken());
            response.put("fileName", asset.getFileName());
            response.put("fileSize", asset.getFileSize());
            response.put("uploadedAt", asset.getUploadedAt());
            response.put("message", "Asset committed to chain successfully! SAVE YOUR OWNER TOKEN!");
            
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
            List<Asset> assets = assetService.getAssetsByToken(ownerToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("assetCount", assets.size());
            response.put("assets", assets.stream().map(a -> {
                Map<String, Object> assetMap = new HashMap<>();
                assetMap.put("hash", a.getHash());
                assetMap.put("fileName", a.getFileName());
                assetMap.put("fileSize", a.getFileSize());
                assetMap.put("uploadedAt", a.getUploadedAt());
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
                "✓ FILE VERIFIED SAFE! File matches the hash - NO CORRUPTION!" : 
                "✗ HASH MISMATCH! File does not match - File corrupted or different!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/download/{hash}")
    public ResponseEntity<?> downloadAsset(
            @PathVariable String hash,
            @RequestParam String ownerToken) {
        try {
            Asset asset = assetService.getAsset(hash, ownerToken);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + asset.getFileName() + "\"")
                    .header("Content-Type", asset.getFileType() != null ? asset.getFileType() : "application/octet-stream")
                    .body(asset.getFileContent());
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @DeleteMapping("/delete/{hash}")
    public ResponseEntity<Map<String, Object>> deleteAsset(
            @PathVariable String hash,
            @RequestParam String ownerToken) {
        try {
            assetService.deleteAsset(hash, ownerToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "✓ Asset deleted successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
}

