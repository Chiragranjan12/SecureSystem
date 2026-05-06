package com.assetregistry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private String hash;
    private String fileName;
    private Long fileSize;
    private String fileType;
    private String ownerToken;
    private LocalDateTime uploadedAt;
    private byte[] fileContent;
}
