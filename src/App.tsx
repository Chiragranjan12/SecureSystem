import { useState } from 'react';
import { Shield, FileCheck, Database, Upload, CheckCircle, XCircle, Loader2 } from 'lucide-react';

type Tab = 'verify' | 'register' | 'custody';

interface HashResult {
  success: boolean;
  hash: string;
  ownerToken?: string;
  fileName?: string;
  fileSize?: number;
  mimeType?: string;
  uploadedAt?: string;
  message?: string;
  timestamp?: number;
  bytes32Hash?: string;
}

interface VerificationResult {
  verified: boolean;
  asset?: any;
  timestamp: number;
}

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('verify');
  const [file, setFile] = useState<File | null>(null);
  const [registerFile, setRegisterFile] = useState<File | null>(null);
  const [hashResult, setHashResult] = useState<HashResult | null>(null);
  const [registerResult, setRegisterResult] = useState<{
    hash: string;
    ownerToken: string;
    fileName: string;
    timestamp: string;
  } | null>(null);
  const [verificationResult, setVerificationResult] = useState<VerificationResult | null>(null);
  const [custodyHash, setCustodyHash] = useState('');
  const [custodyHistory, setCustodyHistory] = useState<
    Array<{ from: string; to: string; timestamp: string }>
  >([]);
  const [transferHash, setTransferHash] = useState('');
  const [transferCurrentOwnerToken, setTransferCurrentOwnerToken] = useState('');
  const [transferNewOwnerName, setTransferNewOwnerName] = useState('');
  const [transferResult, setTransferResult] = useState<{ newOwnerToken: string } | null>(null);
  const [listOwnerToken, setListOwnerToken] = useState('');
  const [ownedAssets, setOwnedAssets] = useState<
    Array<{ hash: string; fileName: string; timestamp: string }>
  >([]);
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const [assetsLoaded, setAssetsLoaded] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      setHashResult(null);
      setVerificationResult(null);
      setError(null);
    }
  };

  const handleRegisterFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setRegisterFile(selectedFile);
      setRegisterResult(null);
      setError(null);
    }
  };

  const handleRegisterUpload = async () => {
    if (!registerFile) return;

    setLoading(true);
    setError(null);
    setRegisterResult(null);

    try {
      const formData = new FormData();
      formData.append('file', registerFile);

      const response = await fetch(`${BACKEND_URL}/api/assets/upload`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Asset registration failed');
      }

      const data = await response.json();
      setRegisterResult({
        hash: data.hash,
        ownerToken: data.ownerToken,
        fileName: data.fileName,
        timestamp: data.timestamp,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Asset registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleFetchCustodyHistory = async () => {
    if (!custodyHash) return;

    setLoading(true);
    setError(null);
    setCustodyHistory([]);
    setHistoryLoaded(false);

    try {
      const response = await fetch(`${BACKEND_URL}/api/assets/history?hash=${encodeURIComponent(custodyHash)}`);
      if (!response.ok) {
        throw new Error('Failed to load custody history');
      }

      const data = await response.json();
      setCustodyHistory(data.history || []);
      setHistoryLoaded(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load custody history');
      setHistoryLoaded(true);
    } finally {
      setLoading(false);
    }
  };

  const handleTransferOwnership = async () => {
    if (!transferHash || !transferCurrentOwnerToken || !transferNewOwnerName) return;

    setLoading(true);
    setError(null);
    setTransferResult(null);

    try {
      const response = await fetch(`${BACKEND_URL}/api/assets/transfer`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          hash: transferHash,
          currentOwnerToken: transferCurrentOwnerToken,
          newOwnerName: transferNewOwnerName,
        }),
      });

      if (!response.ok) {
        throw new Error('Ownership transfer failed');
      }

      const data = await response.json();
      setTransferResult({ newOwnerToken: data.newOwnerToken });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ownership transfer failed');
    } finally {
      setLoading(false);
    }
  };

  const handleListOwnedAssets = async () => {
    if (!listOwnerToken) return;

    setLoading(true);
    setError(null);
    setOwnedAssets([]);
    setAssetsLoaded(false);

    try {
      const response = await fetch(`${BACKEND_URL}/api/assets/list?ownerToken=${encodeURIComponent(listOwnerToken)}`);
      if (!response.ok) {
        throw new Error('Failed to load owned assets');
      }

      const data = await response.json();
      setOwnedAssets(data.assets || []);
      setAssetsLoaded(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load owned assets');
      setAssetsLoaded(true);
    } finally {
      setLoading(false);
    }
  };

  const handleHashFile = async () => {
    if (!file) return;

    setLoading(true);
    setError(null);
    setHashResult(null);
    setVerificationResult(null);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(`${BACKEND_URL}/api/assets/upload`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Hash generation failed');
      }

      const data: HashResult = await response.json();
      setHashResult({
        ...data,
        bytes32Hash: data.hash,
        ownerToken: data.ownerToken,
        mimeType: file.type,
        timestamp: Date.now(),
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Hash generation failed');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyFile = async () => {
    if (!hashResult || !file) return;

    setLoading(true);
    setError(null);
    setVerificationResult(null);

    try {
      const formData = new FormData();
      formData.append('hash', hashResult.hash);
      formData.append('file', file);

      const response = await fetch(`${BACKEND_URL}/api/assets/verify`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Verification failed');
      }

      const data = await response.json();
      setVerificationResult({
        verified: data.isValid === true,
        asset: data,
        timestamp: Date.now(),
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Verification failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-200">
      <header className="border-b-4 border-black bg-white">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <h1 className="text-4xl font-black uppercase tracking-tighter leading-none">
            ASSET REGISTRY
          </h1>
          <p className="text-sm font-mono mt-1 uppercase">
            Decentralized Truth System - Powered by Java Backend
          </p>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="mb-8 border-4 border-black bg-white p-6">
          <h2 className="text-2xl font-black uppercase mb-4">DECENTRALIZED TRUTH SYSTEM</h2>
          <p className="font-mono text-sm leading-relaxed">
            A HIGH-INTEGRITY ASSET REGISTRY FOR SOCIAL TRANSPARENCY. VERIFY PROPERTY DEEDS,
            SUPPLY CHAIN AUTHENTICITY, MEDICAL CREDENTIALS, AND MORE. BLOCKCHAIN-BACKED.
            TAMPER-PROOF. PERMANENT.
          </p>
        </div>

        <div className="border-4 border-black bg-white mb-8">
          <div className="flex border-b-4 border-black">
            <button
              onClick={() => setActiveTab('verify')}
              className={`flex-1 py-4 px-6 font-black text-lg uppercase border-r-4 border-black transition-colors ${
                activeTab === 'verify'
                  ? 'bg-black text-white'
                  : 'bg-white text-black hover:bg-gray-100'
              }`}
            >
              <FileCheck className="w-6 h-6 inline mr-2" />
              VERIFY
            </button>
            <button
              onClick={() => setActiveTab('register')}
              className={`flex-1 py-4 px-6 font-black text-lg uppercase border-r-4 border-black transition-colors ${
                activeTab === 'register'
                  ? 'bg-black text-white'
                  : 'bg-white text-black hover:bg-gray-100'
              }`}
            >
              <Shield className="w-6 h-6 inline mr-2" />
              REGISTER
            </button>
            <button
              onClick={() => setActiveTab('custody')}
              className={`flex-1 py-4 px-6 font-black text-lg uppercase transition-colors ${
                activeTab === 'custody'
                  ? 'bg-black text-white'
                  : 'bg-white text-black hover:bg-gray-100'
              }`}
            >
              <Database className="w-6 h-6 inline mr-2" />
              CUSTODY
            </button>
          </div>

          <div className="p-8">
            {activeTab === 'verify' && (
              <div className="border-4 border-black bg-white p-8">
                <h2 className="text-3xl font-black mb-6 uppercase tracking-tight">
                  PUBLIC VERIFICATION
                </h2>

                <div className="space-y-6">
                  <div className="border-2 border-black p-6">
                    <label className="block">
                      <div className="flex items-center gap-3 mb-4 cursor-pointer">
                        <Upload className="w-6 h-6" />
                        <span className="text-lg font-bold uppercase">Upload File</span>
                      </div>
                      <input
                        type="file"
                        accept=".pdf,.png,.jpg,.jpeg"
                        onChange={handleFileSelect}
                        className="block w-full text-sm file:mr-4 file:py-3 file:px-6 file:border-2 file:border-black file:bg-white file:text-black file:font-bold file:uppercase hover:file:bg-black hover:file:text-white transition-colors cursor-pointer"
                      />
                    </label>

                    {file && (
                      <div className="mt-4 p-4 border-2 border-black bg-gray-50">
                        <p className="font-mono text-sm">
                          <span className="font-bold">FILE:</span> {file.name}
                        </p>
                        <p className="font-mono text-sm">
                          <span className="font-bold">SIZE:</span> {(file.size / 1024).toFixed(2)} KB
                        </p>
                      </div>
                    )}
                  </div>

                  <button
                    onClick={handleHashFile}
                    disabled={!file || loading}
                    className="w-full py-4 px-8 border-4 border-black bg-black text-white font-black text-xl uppercase hover:bg-white hover:text-black transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
                  >
                    {loading ? (
                      <>
                        <Loader2 className="w-6 h-6 animate-spin" />
                        GENERATING HASH...
                      </>
                    ) : (
                      'GENERATE HASH'
                    )}
                  </button>

                  {hashResult && (
                    <div className="border-4 border-blue-600 bg-blue-50 p-6">
                      <h3 className="text-xl font-black text-blue-600 uppercase mb-4">HASH GENERATED</h3>
                      <div className="space-y-3 font-mono text-sm">
                        <div className="border-2 border-black p-3 bg-white">
                          <p className="font-bold uppercase mb-1">SHA-256 Hash</p>
                          <p className="break-all text-xs">{hashResult.hash}</p>
                        </div>
                        <div className="border-2 border-black p-3 bg-white">
                          <p className="font-bold uppercase mb-1">Bytes32 (Blockchain)</p>
                          <p className="break-all text-xs">{hashResult.bytes32Hash}</p>
                        </div>
                        {hashResult.ownerToken && (
                          <div className="border-2 border-black p-3 bg-white">
                            <p className="font-bold uppercase mb-1">Owner Token</p>
                            <p className="break-all text-xs">{hashResult.ownerToken}</p>
                          </div>
                        )}
                      </div>

                      <button
                        onClick={handleVerifyFile}
                        disabled={loading}
                        className="w-full mt-4 py-4 px-8 border-4 border-blue-600 bg-blue-600 text-white font-black text-xl uppercase hover:bg-white hover:text-blue-600 transition-colors disabled:opacity-50 flex items-center justify-center gap-3"
                      >
                        {loading ? (
                          <>
                            <Loader2 className="w-6 h-6 animate-spin" />
                            VERIFYING...
                          </>
                        ) : (
                          'CHECK BLOCKCHAIN'
                        )}
                      </button>
                    </div>
                  )}

                  {verificationResult && (
                    <div
                      className={`border-4 p-6 ${
                        verificationResult.verified
                          ? 'border-green-600 bg-green-50'
                          : 'border-red-600 bg-red-50'
                      }`}
                    >
                      <div className="flex items-start gap-4">
                        {verificationResult.verified ? (
                          <CheckCircle className="w-12 h-12 text-green-600 flex-shrink-0" />
                        ) : (
                          <XCircle className="w-12 h-12 text-red-600 flex-shrink-0" />
                        )}
                        <div className="flex-1">
                          <h3
                            className={`text-3xl font-black uppercase mb-4 ${
                              verificationResult.verified ? 'text-green-600' : 'text-red-600'
                            }`}
                          >
                            {verificationResult.verified ? 'VERIFIED' : 'NOT FOUND'}
                          </h3>
                          {!verificationResult.verified && (
                            <p className="text-sm font-mono">
                              This file does not match any registered asset in the blockchain registry.
                            </p>
                          )}
                        </div>
                      </div>
                    </div>
                  )}

                  {error && (
                    <div className="border-4 border-red-600 bg-red-50 p-6">
                      <h3 className="text-xl font-black text-red-600 uppercase mb-2">ERROR</h3>
                      <p className="text-sm font-mono">{error}</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'register' && (
              <div className="border-4 border-black bg-white p-8">
                <h2 className="text-3xl font-black mb-6 uppercase">ASSET REGISTRATION</h2>
                <div className="space-y-6">
                  <div className="border-2 border-black p-6">
                    <label className="block">
                      <div className="flex items-center gap-3 mb-4 cursor-pointer">
                        <Upload className="w-6 h-6" />
                        <span className="text-lg font-bold uppercase">Upload Asset File</span>
                      </div>
                      <input
                        type="file"
                        accept=".pdf,.png,.jpg,.jpeg"
                        onChange={handleRegisterFileSelect}
                        className="block w-full text-sm file:mr-4 file:py-3 file:px-6 file:border-2 file:border-black file:bg-white file:text-black file:font-bold file:uppercase hover:file:bg-black hover:file:text-white transition-colors cursor-pointer"
                      />
                    </label>

                    {registerFile && (
                      <div className="mt-4 p-4 border-2 border-black bg-gray-50">
                        <p className="font-mono text-sm">
                          <span className="font-bold">FILE:</span> {registerFile.name}
                        </p>
                        <p className="font-mono text-sm">
                          <span className="font-bold">SIZE:</span> {(registerFile.size / 1024).toFixed(2)} KB
                        </p>
                      </div>
                    )}
                  </div>

                  <button
                    onClick={handleRegisterUpload}
                    disabled={!registerFile || loading}
                    className="w-full py-4 px-8 border-4 border-black bg-black text-white font-black text-xl uppercase hover:bg-white hover:text-black transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? 'REGISTERING ASSET...' : 'REGISTER ASSET'}
                  </button>

                  {registerResult && (
                    <div className="border-4 border-green-600 bg-green-50 p-6">
                      <h3 className="text-xl font-black text-green-600 uppercase mb-4">REGISTRATION SUCCESS</h3>
                      <div className="space-y-3 font-mono text-sm">
                        <p>
                          <span className="font-bold uppercase">File Name:</span> {registerResult.fileName}
                        </p>
                        <p>
                          <span className="font-bold uppercase">Hash:</span> {registerResult.hash}
                        </p>
                        <p>
                          <span className="font-bold uppercase">Owner Token:</span> {registerResult.ownerToken}
                        </p>
                        <p>
                          <span className="font-bold uppercase">Timestamp:</span> {registerResult.timestamp}
                        </p>
                      </div>
                    </div>
                  )}

                  {error && (
                    <div className="border-4 border-red-600 bg-red-50 p-6">
                      <h3 className="text-xl font-black text-red-600 uppercase mb-2">ERROR</h3>
                      <p className="text-sm font-mono">{error}</p>
                    </div>
                  )}
                </div>
              </div>
            )}

            {activeTab === 'custody' && (
              <div className="border-4 border-black bg-white p-8 space-y-8">
                <h2 className="text-3xl font-black mb-6 uppercase">CHAIN OF CUSTODY</h2>

                <div className="border-2 border-black p-6">
                  <h3 className="text-xl font-black uppercase mb-4">Lookup Custody History</h3>
                  <div className="grid gap-4 md:grid-cols-3">
                    <input
                      type="text"
                      value={custodyHash}
                      onChange={(e) => setCustodyHash(e.target.value)}
                      placeholder="Asset hash"
                      className="border-2 border-black p-3 text-sm w-full"
                    />
                    <button
                      onClick={handleFetchCustodyHistory}
                      disabled={!custodyHash || loading}
                      className="py-3 px-6 border-2 border-black bg-black text-white font-black uppercase hover:bg-white hover:text-black transition-colors disabled:opacity-50"
                    >
                      {loading ? 'LOADING...' : 'FETCH HISTORY'}
                    </button>
                  </div>

                  {historyLoaded ? (
                    custodyHistory.length > 0 ? (
                      <div className="mt-6 space-y-4 font-mono text-sm">
                        {custodyHistory.map((event, index) => (
                          <div key={`${event.timestamp}-${index}`} className="border-2 border-black p-4 bg-gray-50">
                            <p className="font-bold uppercase">Transfer {index + 1}</p>
                            <p>From: {event.from}</p>
                            <p>To: {event.to}</p>
                            <p>When: {new Date(event.timestamp).toLocaleString()}</p>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="mt-6 border-2 border-yellow-600 bg-yellow-50 p-4 font-mono text-sm">
                        No custody transfers found for this asset yet. The registration event is recorded in the backend.
                      </div>
                    )
                  ) : (
                    <div className="mt-6 border-2 border-gray-300 bg-white p-4 font-mono text-sm">
                      Enter an asset hash and click "Fetch History" to view custody events.
                    </div>
                  )}
                </div>

                <div className="border-2 border-black p-6">
                  <h3 className="text-xl font-black uppercase mb-4">Transfer Ownership</h3>
                  <div className="space-y-4">
                    <input
                      type="text"
                      value={transferHash}
                      onChange={(e) => setTransferHash(e.target.value)}
                      placeholder="Asset hash"
                      className="border-2 border-black p-3 text-sm w-full"
                    />
                    <input
                      type="text"
                      value={transferCurrentOwnerToken}
                      onChange={(e) => setTransferCurrentOwnerToken(e.target.value)}
                      placeholder="Current owner token"
                      className="border-2 border-black p-3 text-sm w-full"
                    />
                    <input
                      type="text"
                      value={transferNewOwnerName}
                      onChange={(e) => setTransferNewOwnerName(e.target.value)}
                      placeholder="New owner name"
                      className="border-2 border-black p-3 text-sm w-full"
                    />
                    <button
                      onClick={handleTransferOwnership}
                      disabled={!transferHash || !transferCurrentOwnerToken || !transferNewOwnerName || loading}
                      className="w-full py-4 px-6 border-2 border-black bg-black text-white font-black uppercase hover:bg-white hover:text-black transition-colors disabled:opacity-50"
                    >
                      {loading ? 'TRANSFERRING...' : 'TRANSFER OWNERSHIP'}
                    </button>

                    {transferResult && (
                      <div className="border-2 border-green-600 bg-green-50 p-4 font-mono text-sm">
                        <p className="font-bold uppercase mb-2">New Owner Token</p>
                        <p>{transferResult.newOwnerToken}</p>
                      </div>
                    )}
                  </div>
                </div>

                <div className="border-2 border-black p-6">
                  <h3 className="text-xl font-black uppercase mb-4">List Assets by Owner Token</h3>
                  <div className="grid gap-4 md:grid-cols-3">
                    <input
                      type="text"
                      value={listOwnerToken}
                      onChange={(e) => setListOwnerToken(e.target.value)}
                      placeholder="Owner token"
                      className="border-2 border-black p-3 text-sm w-full"
                    />
                    <button
                      onClick={handleListOwnedAssets}
                      disabled={!listOwnerToken || loading}
                      className="py-3 px-6 border-2 border-black bg-black text-white font-black uppercase hover:bg-white hover:text-black transition-colors disabled:opacity-50"
                    >
                      {loading ? 'LOADING...' : 'LIST ASSETS'}
                    </button>
                  </div>

                  {assetsLoaded ? (
                    ownedAssets.length > 0 ? (
                      <div className="mt-6 space-y-3 font-mono text-sm">
                        {ownedAssets.map((asset) => (
                          <div key={asset.hash} className="border-2 border-black p-4 bg-gray-50">
                            <p className="font-bold uppercase">{asset.fileName}</p>
                            <p>Hash: {asset.hash}</p>
                            <p>Timestamp: {asset.timestamp}</p>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div className="mt-6 border-2 border-yellow-600 bg-yellow-50 p-4 font-mono text-sm">
                        No assets were found for this owner token. Make sure you are using the current owner token returned by registration or transfer.
                      </div>
                    )
                  ) : (
                    <div className="mt-6 border-2 border-gray-300 bg-white p-4 font-mono text-sm">
                      Enter an owner token and click "List Assets" to see assets you currently own.
                    </div>
                  )}
                </div>

                {error && (
                  <div className="border-4 border-red-600 bg-red-50 p-6">
                    <h3 className="text-xl font-black text-red-600 uppercase mb-2">ERROR</h3>
                    <p className="text-sm font-mono">{error}</p>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="border-4 border-black bg-white p-6">
            <div className="w-12 h-12 border-2 border-black bg-black text-white flex items-center justify-center mb-4">
              <Shield className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-black uppercase mb-3">JAVA BACKEND</h3>
            <p className="text-sm font-mono leading-relaxed">
              HIGH-PERFORMANCE SERVICE WITH SPRING BOOT. SHA-256 HASHING, AES-256 ENCRYPTION,
              AND WEB3J BLOCKCHAIN INTEGRATION.
            </p>
          </div>

          <div className="border-4 border-black bg-white p-6">
            <div className="w-12 h-12 border-2 border-black bg-black text-white flex items-center justify-center mb-4">
              <FileCheck className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-black uppercase mb-3">CRYPTOGRAPHIC PROOF</h3>
            <p className="text-sm font-mono leading-relaxed">
              SHA-256 HASHING CREATES DIGITAL FINGERPRINTS. AES-256 ENCRYPTION PROTECTS
              PRIVACY. BLOCKCHAIN PROVIDES IMMUTABLE VERIFICATION.
            </p>
          </div>

          <div className="border-4 border-black bg-white p-6">
            <div className="w-12 h-12 border-2 border-black bg-black text-white flex items-center justify-center mb-4">
              <Database className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-black uppercase mb-3">CHAIN OF CUSTODY</h3>
            <p className="text-sm font-mono leading-relaxed">
              COMPLETE OWNERSHIP HISTORY FROM GENESIS BLOCK. EVERY TRANSFER PERMANENTLY
              RECORDED. TRANSPARENT. AUDITABLE. TAMPER-PROOF.
            </p>
          </div>
        </div>
      </main>

      <footer className="border-t-4 border-black bg-white mt-16">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <p className="text-center font-mono text-sm uppercase">
            POWERED BY ETHEREUM • JAVA BACKEND • SECURED BY CRYPTOGRAPHY • BUILT FOR TRUTH
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
