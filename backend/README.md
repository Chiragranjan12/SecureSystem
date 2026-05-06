# Asset Registry Backend - Java Service

High-performance Java backend for decentralized asset registry with blockchain integration, SHA-256 file hashing, and AES-256 encryption.

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2.0
- **JDK**: Java 11+
- **Blockchain**: Web3j 4.10.3
- **Cryptography**: BouncyCastle, Java Security API
- **Build Tool**: Maven
- **Database**: Supabase PostgreSQL (optional)

### Project Structure

```
backend/
├── pom.xml                          # Maven dependencies
├── src/main/java/com/assetregistry/
│   ├── AssetRegistryApplication.java    # Spring Boot entry point
│   ├── config/
│   │   └── BlockchainConfig.java        # Blockchain service initialization
│   ├── controller/
│   │   ├── FileHashingController.java   # SHA-256 API endpoints
│   │   ├── EncryptionController.java    # AES-256 API endpoints
│   │   └── BlockchainController.java    # Blockchain verification API
│   ├── service/
│   │   ├── EncryptionService.java       # AES-256 encryption logic
│   │   └── BlockchainService.java       # Web3j blockchain integration
│   ├── util/
│   │   └── FileHashingUtil.java         # SHA-256 hashing utility
│   └── model/
│       ├── HashResult.java              # Hash result DTO
│       └── ValidationResult.java        # Validation result DTO
├── src/main/resources/
│   └── application.yml              # Spring Boot configuration
└── src/test/
    └── java/                        # Unit tests
```

## Features

### 1. File Hashing (SHA-256)

**Endpoint**: `POST /api/v1/hash/file`

Generates cryptographic SHA-256 hash of uploaded files.

**Request**:
```bash
curl -X POST -F "file=@document.pdf" http://localhost:8080/api/v1/hash/file
```

**Response**:
```json
{
  "success": true,
  "hash": "abc123...",
  "fileName": "document.pdf",
  "fileSize": 12345,
  "mimeType": "application/pdf",
  "bytes32Hash": "0xabc123..."
}
```

**Features**:
- Client-side validation before hashing
- Magic byte verification (prevents file type spoofing)
- Streaming support for large files
- Returns bytes32 format for Solidity smart contracts

### 2. File Encryption (AES-256)

**Endpoint**: `POST /api/v1/encrypt/file`

Encrypts files using AES-256-GCM with PBKDF2 key derivation.

**Request**:
```bash
curl -X POST -F "file=@document.pdf" -F "password=mypassword123" http://localhost:8080/api/v1/encrypt/file
```

**Response**:
```json
{
  "success": true,
  "encryptedData": "base64encodedencrypteddata...",
  "fileName": "document.pdf",
  "originalSize": 12345,
  "encryptedSize": 45678
}
```

**Security Features**:
- AES-256-GCM authenticated encryption
- PBKDF2 with 600,000 iterations
- Random 12-byte IV per encryption
- Random 16-byte salt per encryption
- No keys stored in plain text

### 3. Blockchain Integration

**Verify Asset**:
```bash
curl -X POST http://localhost:8080/api/v1/blockchain/verify \
  -H "Content-Type: application/json" \
  -d '{"assetId":"0x...", "fileHash":"0x..."}'
```

**Get Asset**:
```bash
curl http://localhost:8080/api/v1/blockchain/asset/0xassetid
```

**Check Issuer**:
```bash
curl http://localhost:8080/api/v1/blockchain/issuer/0xaddress
```

## Setup & Installation

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Ethereum testnet RPC endpoint (Sepolia, Polygon Mumbai)
- Deployed AssetRegistry smart contract

### Build

```bash
cd backend
mvn clean install
```

### Configuration

Create environment variables or edit `application.yml`:

```bash
export RPC_URL=https://sepolia.infura.io/v3/YOUR_PROJECT_ID
export CONTRACT_ADDRESS=0x...
export NETWORK_ID=11155111
```

Or edit `src/main/resources/application.yml`:

```yaml
blockchain:
  rpc-url: https://sepolia.infura.io/v3/YOUR_PROJECT_ID
  contract-address: 0x...
  network-id: 11155111
```

### Run

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### Docker

```bash
# Build Docker image
docker build -t asset-registry-backend .

# Run container
docker run -p 8080:8080 \
  -e RPC_URL=https://sepolia.infura.io/v3/YOUR_PROJECT_ID \
  -e CONTRACT_ADDRESS=0x... \
  asset-registry-backend
```

## API Reference

### File Hashing API

#### POST /api/v1/hash/file
Generate SHA-256 hash for uploaded file

**Parameters**:
- `file` (form data): File to hash (PDF, PNG, JPEG)

**Response**: HashResult object

#### POST /api/v1/hash/validate
Validate file before hashing

**Parameters**:
- `file` (form data): File to validate
- `mimeType` (query, optional): MIME type hint

**Response**: Validation status

#### GET /api/v1/hash/health
Health check for hash service

### Encryption API

#### POST /api/v1/encrypt/file
Encrypt file with AES-256

**Parameters**:
- `file` (form data): File to encrypt
- `password` (query): Password (min 8 characters)

**Response**: Encrypted data (Base64)

#### POST /api/v1/encrypt/decrypt
Decrypt encrypted data

**Parameters**:
- `encryptedData` (query): Base64 encrypted data
- `password` (query): Password

**Response**: Decryption confirmation

#### POST /api/v1/encrypt/derive-key
Derive key from wallet signature

**Parameters**:
- `walletAddress` (query): Ethereum wallet address
- `signature` (query): Wallet signature

**Response**: Derived key

#### GET /api/v1/encrypt/health
Health check for encryption service

### Blockchain API

#### POST /api/v1/blockchain/verify
Verify asset on blockchain

**Parameters**:
- `assetId` (query): Asset ID (bytes32)
- `fileHash` (query): File hash (bytes32)

**Response**: Verification status

#### GET /api/v1/blockchain/asset/{assetId}
Get asset information

**Parameters**:
- `assetId` (path): Asset ID

**Response**: Asset information from blockchain

#### GET /api/v1/blockchain/issuer/{address}
Check if address is verified issuer

**Parameters**:
- `address` (path): Ethereum address

**Response**: Issuer status

#### GET /api/v1/blockchain/admin
Get contract admin address

**Response**: Admin address

#### GET /api/v1/blockchain/network
Get network information

**Response**: Network details

#### GET /api/v1/blockchain/health
Health check for blockchain service

## Security Implementation

### File Hashing Security

- **Algorithm**: SHA-256 (NIST standard, collision resistant)
- **Implementation**: Java MessageDigest
- **Validation**: Magic byte verification prevents file type spoofing
- **Output**: Hexadecimal string + bytes32 for Solidity

### Encryption Security

- **Algorithm**: AES-256-GCM (authenticated encryption)
- **Key Derivation**: PBKDF2 with 600,000 iterations (memory-hard)
- **IV**: Random 12-byte per encryption (prevents patterns)
- **Salt**: Random 16-byte per encryption (prevents rainbow tables)
- **No Key Reuse**: Each encryption generates new IV and salt

### Blockchain Security

- **Provider**: Web3j with network-level error handling
- **Validation**: All smart contract calls validated
- **Gas Management**: Automatic gas estimation
- **Access Control**: RBAC checked at smart contract level
- **No Private Keys**: All operations read-only or user-signed

## Performance Considerations

### File Hashing
- SHA-256 uses hardware acceleration (AES-NI) when available
- Streaming support for files > 50MB (optional enhancement)
- Typical throughput: 100-500 MB/s depending on hardware

### Encryption
- AES-256-GCM uses hardware acceleration when available
- PBKDF2 iterations: 600,000 (configurable)
- Single-threaded processing (configurable for concurrency)
- Typical time: 100-500ms per file depending on size

### Blockchain Calls
- Web3j caching for repeated calls
- Async support for concurrent operations
- Connection pooling for RPC calls

## Testing

### Unit Tests

```bash
mvn test
```

### Manual Testing

```bash
# Test file hashing
curl -X POST -F "file=@test.pdf" http://localhost:8080/api/v1/hash/file

# Test encryption
curl -X POST \
  -F "file=@test.pdf" \
  -F "password=testpassword123" \
  http://localhost:8080/api/v1/encrypt/file

# Test blockchain verification
curl -X POST http://localhost:8080/api/v1/blockchain/verify \
  -H "Content-Type: application/json" \
  -d '{"assetId":"0x0000000000000000000000000000000000000000000000000000000000000000","fileHash":"0x0000000000000000000000000000000000000000000000000000000000000000"}'
```

## Deployment

### Production Checklist

- [ ] Java 11+ installed on production server
- [ ] Environment variables configured securely
- [ ] RPC endpoint URL tested and verified
- [ ] Smart contract address verified on block explorer
- [ ] SSL/TLS certificates configured
- [ ] Logging level set to INFO
- [ ] Health check endpoints monitored
- [ ] Rate limiting configured (optional)
- [ ] Backup and recovery plan documented

### Performance Tuning

```bash
# Increase JVM heap size
export JAVA_OPTS="-Xmx4g -Xms4g"

# Enable GC logging
export JAVA_OPTS="${JAVA_OPTS} -Xlog:gc:gc.log"

# Run with optimizations
java ${JAVA_OPTS} -jar asset-registry-backend.jar
```

## Troubleshooting

### Cannot connect to blockchain

**Error**: `Failed to retrieve network info`

**Solution**:
1. Check RPC URL is correct
2. Verify network connectivity
3. Check contract address is valid
4. Review logs for detailed error

### Encryption failing

**Error**: `Encryption failed: invalid key`

**Solution**:
1. Ensure password is at least 8 characters
2. Check password contains no special characters that cause encoding issues
3. Verify file is not corrupted

### Hash generation slow

**Error**: File processing takes > 1 second

**Solution**:
1. This is normal for PBKDF2 (intentionally slow)
2. For hashing only, can reduce PBKDF2 iterations in EncryptionService
3. Use SSD for better file I/O

## API Examples

### Complete Verification Workflow

```bash
# 1. Upload and hash file
HASH_RESPONSE=$(curl -X POST -F "file=@deed.pdf" http://localhost:8080/api/v1/hash/file)
ASSET_ID=$(echo $HASH_RESPONSE | jq -r '.bytes32Hash')

# 2. Verify on blockchain
curl -X POST http://localhost:8080/api/v1/blockchain/verify \
  -H "Content-Type: application/json" \
  -d "{\"assetId\":\"$ASSET_ID\", \"fileHash\":\"$ASSET_ID\"}"

# 3. Get asset details
curl http://localhost:8080/api/v1/blockchain/asset/$ASSET_ID
```

### Complete Encryption Workflow

```bash
# 1. Encrypt file
ENC_RESPONSE=$(curl -X POST \
  -F "file=@sensitive.pdf" \
  -F "password=securepassword123" \
  http://localhost:8080/api/v1/encrypt/file)
ENCRYPTED=$(echo $ENC_RESPONSE | jq -r '.encryptedData')

# 2. Decrypt file
curl -X POST http://localhost:8080/api/v1/encrypt/decrypt \
  -H "Content-Type: application/json" \
  -d "{\"encryptedData\":\"$ENCRYPTED\", \"password\":\"securepassword123\"}"
```

## Monitoring

### Health Checks

```bash
# All services
curl http://localhost:8080/actuator/health

# Individual services
curl http://localhost:8080/api/v1/hash/health
curl http://localhost:8080/api/v1/encrypt/health
curl http://localhost:8080/api/v1/blockchain/health
```

### Logs

```bash
# View logs
tail -f logs/application.log

# Filter for errors
grep ERROR logs/application.log

# Filter for specific service
grep BlockchainService logs/application.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit pull request

## License

MIT License - See LICENSE file for details

## Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check logs for detailed error messages
- Review documentation in `/docs` directory
- Test with simple files first before complex workflows
