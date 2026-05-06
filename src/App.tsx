import { useState } from 'react';
import { Shield, FileCheck, Database, Upload, CheckCircle, XCircle, Loader2, Clock, ArrowRight } from 'lucide-react';

type Tab = 'verify' | 'register' | 'custody';

interface HashResult {
  success: boolean;
  hash: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  timestamp: number;
  bytes32Hash: string;
}

interface VerificationResult {
  verified: boolean;
  asset?: any;
  timestamp: number;
}

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('verify');
  const [file, setFile] = useState<File | null>(null);
  const [hashResult, setHashResult] = useState<HashResult | null>(null);
  const [verificationResult, setVerificationResult] = useState<VerificationResult | null>(null);
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

  const handleHashFile = async () => {
    if (!file) return;

    setLoading(true);
    setError(null);
    setHashResult(null);
    setVerificationResult(null);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await fetch(`${BACKEND_URL}/api/v1/hash/file`, {
        method: 'POST',
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Hash generation failed');
      }

      const data: HashResult = await response.json();
      setHashResult(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Hash generation failed');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyFile = async () => {
    if (!hashResult) return;

    setLoading(true);
    setError(null);
    setVerificationResult(null);

    try {
      const response = await fetch(`${BACKEND_URL}/api/v1/blockchain/verify`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          assetId: hashResult.bytes32Hash,
          fileHash: hashResult.bytes32Hash,
        }),
      });

      if (!response.ok) {
        throw new Error('Verification failed');
      }

      const data: VerificationResult = await response.json();
      setVerificationResult(data);
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
                <p className="font-mono mb-4">
                  Java backend handles secure file hashing and encryption for asset registration.
                  Connect your wallet and use the REST APIs exposed by the backend service.
                </p>
              </div>
            )}

            {activeTab === 'custody' && (
              <div className="border-4 border-black bg-white p-8">
                <h2 className="text-3xl font-black mb-6 uppercase">CHAIN OF CUSTODY</h2>
                <p className="font-mono">
                  Query blockchain via Java backend APIs to retrieve complete asset custody history.
                </p>
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
