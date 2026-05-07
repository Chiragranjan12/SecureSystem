package com.assetregistry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRecord {
    private String hash;
    private String fileName;
    private String ownerToken;
    private LocalDateTime timestamp;
    private List<TransferEvent> history;
}
