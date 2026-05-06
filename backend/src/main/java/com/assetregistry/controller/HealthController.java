package com.assetregistry.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>BLOCKCHAIN ASSET TRACKER</title>\n" +
            "    <style>\n" +
            "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
            "        :root { --neon: #00ff44; --concrete: #333333; --dark: #1a1a1a; }\n" +
            "        body {\n" +
            "            background: repeating-linear-gradient(0deg, rgba(0,255,68,0.03) 0px, rgba(0,255,68,0.03) 1px, transparent 1px, transparent 2px),\n" +
            "                        repeating-linear-gradient(90deg, rgba(0,255,68,0.03) 0px, rgba(0,255,68,0.03) 1px, transparent 1px, transparent 2px);\n" +
            "            background-color: var(--dark);\n" +
            "            font-family: 'Arial Black', 'Helvetica Neue', sans-serif;\n" +
            "            color: var(--neon);\n" +
            "            padding: 20px;\n" +
            "            min-height: 100vh;\n" +
            "        }\n" +
            "        .grid-container { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; max-width: 1400px; margin: 0 auto; }\n" +
            "        .panel {\n" +
            "            background: var(--concrete);\n" +
            "            border: 4px solid #000;\n" +
            "            padding: 30px;\n" +
            "            position: relative;\n" +
            "        }\n" +
            "        .panel::before {\n" +
            "            content: '';\n" +
            "            position: absolute;\n" +
            "            top: -2px; left: -2px; right: -2px; bottom: -2px;\n" +
            "            background: var(--neon);\n" +
            "            z-index: -1;\n" +
            "        }\n" +
            "        h1 {\n" +
            "            font-size: 3.5rem;\n" +
            "            letter-spacing: -3px;\n" +
            "            line-height: 1;\n" +
            "            margin-bottom: 10px;\n" +
            "            text-transform: uppercase;\n" +
            "            font-weight: 900;\n" +
            "        }\n" +
            "        h2 {\n" +
            "            font-size: 2rem;\n" +
            "            text-transform: uppercase;\n" +
            "            margin-bottom: 20px;\n" +
            "            border-bottom: 4px solid var(--neon);\n" +
            "            padding-bottom: 12px;\n" +
            "            letter-spacing: -1px;\n" +
            "        }\n" +
            "        .header {\n" +
            "            grid-column: 1 / -1;\n" +
            "            text-align: center;\n" +
            "            margin-bottom: 20px;\n" +
            "        }\n" +
            "        .status-indicator {\n" +
            "            display: inline-block;\n" +
            "            width: 20px;\n" +
            "            height: 20px;\n" +
            "            background: var(--neon);\n" +
            "            border: 4px solid #000;\n" +
            "            margin-right: 10px;\n" +
            "            animation: blink 1s infinite;\n" +
            "        }\n" +
            "        @keyframes blink { 0%, 50% { opacity: 1; } 51%, 100% { opacity: 0.3; } }\n" +
            "        .form-group {\n" +
            "            margin-bottom: 16px;\n" +
            "        }\n" +
            "        label {\n" +
            "            display: block;\n" +
            "            font-weight: 900;\n" +
            "            text-transform: uppercase;\n" +
            "            font-size: 12px;\n" +
            "            margin-bottom: 8px;\n" +
            "            letter-spacing: 2px;\n" +
            "        }\n" +
            "        input[type=\"text\"], input[type=\"file\"], textarea {\n" +
            "            width: 100%;\n" +
            "            background: #222;\n" +
            "            border: 3px solid var(--neon);\n" +
            "            color: var(--neon);\n" +
            "            padding: 12px;\n" +
            "            font-family: 'Courier New', monospace;\n" +
            "            font-size: 14px;\n" +
            "            font-weight: bold;\n" +
            "            margin-bottom: 8px;\n" +
            "        }\n" +
            "        input[type=\"file\"]::file-selector-button {\n" +
            "            background: var(--neon);\n" +
            "            color: #000;\n" +
            "            border: none;\n" +
            "            padding: 8px 16px;\n" +
            "            font-weight: bold;\n" +
            "            cursor: pointer;\n" +
            "        }\n" +
            "        input::placeholder, textarea::placeholder {\n" +
            "            color: rgba(0, 255, 68, 0.5);\n" +
            "        }\n" +
            "        input:focus, textarea:focus {\n" +
            "            outline: none;\n" +
            "            box-shadow: 0 0 20px var(--neon), inset 0 0 10px rgba(0, 255, 68, 0.2);\n" +
            "        }\n" +
            "        .btn-commit {\n" +
            "            width: 100%;\n" +
            "            background: var(--neon);\n" +
            "            color: #000;\n" +
            "            border: 4px solid #000;\n" +
            "            padding: 16px;\n" +
            "            font-size: 1.3rem;\n" +
            "            font-weight: 900;\n" +
            "            text-transform: uppercase;\n" +
            "            letter-spacing: -1px;\n" +
            "            cursor: pointer;\n" +
            "            margin-top: 10px;\n" +
            "            position: relative;\n" +
            "            transition: all 0.1s;\n" +
            "        }\n" +
            "        .btn-commit:active {\n" +
            "            transform: translate(2px, 2px);\n" +
            "            box-shadow: 0 0 0 0 var(--neon), inset 0 0 10px rgba(0, 0, 0, 0.5);\n" +
            "        }\n" +
            "        .btn-commit:hover {\n" +
            "            box-shadow: 0 0 30px var(--neon), inset 0 0 20px rgba(0, 255, 68, 0.3);\n" +
            "        }\n" +
            "        .asset-item {\n" +
            "            background: #222;\n" +
            "            border-left: 4px solid var(--neon);\n" +
            "            padding: 16px;\n" +
            "            margin-bottom: 12px;\n" +
            "            font-family: monospace;\n" +
            "            font-size: 12px;\n" +
            "        }\n" +
            "        .asset-name { font-weight: bold; color: var(--neon); margin-bottom: 6px; }\n" +
            "        .asset-hash { color: #aaa; word-break: break-all; margin-bottom: 6px; }\n" +
            "        .asset-info { color: #777; font-size: 11px; margin-bottom: 8px; }\n" +
            "        .asset-actions { display: flex; gap: 8px; }\n" +
            "        .btn-small {\n" +
            "            flex: 1;\n" +
            "            background: var(--neon);\n" +
            "            color: #000;\n" +
            "            border: 2px solid #000;\n" +
            "            padding: 8px;\n" +
            "            font-weight: bold;\n" +
            "            font-size: 11px;\n" +
            "            cursor: pointer;\n" +
            "            text-transform: uppercase;\n" +
            "        }\n" +
            "        .terminal {\n" +
            "            background: #000;\n" +
            "            border: 3px solid var(--neon);\n" +
            "            padding: 16px;\n" +
            "            font-family: 'Courier New', monospace;\n" +
            "            color: var(--neon);\n" +
            "            font-size: 12px;\n" +
            "            line-height: 1.6;\n" +
            "            max-height: 400px;\n" +
            "            overflow-y: auto;\n" +
            "            white-space: pre-wrap;\n" +
            "            word-break: break-all;\n" +
            "        }\n" +
            "        .info-block {\n" +
            "            background: #222;\n" +
            "            border-left: 4px solid var(--neon);\n" +
            "            padding: 12px 16px;\n" +
            "            margin-bottom: 12px;\n" +
            "            font-family: monospace;\n" +
            "            font-size: 13px;\n" +
            "        }\n" +
            "        .info-label { font-weight: bold; color: var(--neon); }\n" +
            "        .info-value { color: #aaa; }\n" +
            "        .success { color: #00ff44 !important; }\n" +
            "        .error { color: #ff4444 !important; }\n" +
            "        .warning { color: #ffaa00 !important; }\n" +
            "        @media (max-width: 768px) {\n" +
            "            h1 { font-size: 2.5rem; }\n" +
            "            h2 { font-size: 1.5rem; }\n" +
            "            .btn-commit { font-size: 1.1rem; padding: 12px; }\n" +
            "            .panel { padding: 20px; }\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"header panel\">\n" +
            "        <h1>⚡ BLOCKCHAIN<br/>ASSET TRACKER</h1>\n" +
            "        <p style=\"font-size: 18px; margin-top: 15px;\"><span class=\"status-indicator\"></span>SYSTEM ONLINE</p>\n" +
            "    </div>\n" +
            "    <div class=\"grid-container\">\n" +
            "        <div class=\"panel\">\n" +
            "            <h2>📝 UPLOAD NEW ASSET</h2>\n" +
            "            <form id=\"uploadForm\">\n" +
            "                <div class=\"form-group\">\n" +
            "                    <label for=\"assetFile\">📂 Select File</label>\n" +
            "                    <input type=\"file\" id=\"assetFile\" required>\n" +
            "                </div>\n" +
            "                <button type=\"button\" class=\"btn-commit\" onclick=\"commitAsset()\">COMMIT TO CHAIN</button>\n" +
            "                <div id=\"uploadStatus\" style=\"margin-top: 12px; font-size: 12px;\"></div>\n" +
            "            </form>\n" +
            "        </div>\n" +
            "        <div class=\"panel\">\n" +
            "            <h2>✓ VERIFY FILE SAFETY</h2>\n" +
            "            <form id=\"verifyForm\">\n" +
            "                <div class=\"form-group\">\n" +
            "                    <label for=\"verifyHash\">SHA-256 Hash (from upload)</label>\n" +
            "                    <input type=\"text\" id=\"verifyHash\" placeholder=\"Paste hash here\" required>\n" +
            "                </div>\n" +
            "                <div class=\"form-group\">\n" +
            "                    <label for=\"verifyFile\">Re-upload Same File</label>\n" +
            "                    <input type=\"file\" id=\"verifyFile\" required>\n" +
            "                </div>\n" +
            "                <button type=\"button\" class=\"btn-commit\" onclick=\"verifyFile()\">VERIFY NO CORRUPTION</button>\n" +
            "                <div id=\"verifyStatus\" style=\"margin-top: 12px; font-size: 12px;\"></div>\n" +
            "            </form>\n" +
            "        </div>\n" +
            "    </div>" +
            "    <div class=\"grid-container\" style=\"margin-top: 20px;\">\n" +
            "        <div class=\"panel\" style=\"grid-column: 1 / -1;\">\n" +
            "            <h2>📦 MY UPLOADED ASSETS</h2>\n" +
            "            <div class=\"form-group\">\n" +
            "                <label for=\"ownerToken\">🔑 Enter Your Owner Token (from upload)</label>\n" +
            "                <input type=\"text\" id=\"ownerToken\" placeholder=\"Paste your owner token here\" style=\"font-family: monospace; font-size: 11px;\">\n" +
            "                <button type=\"button\" class=\"btn-commit\" onclick=\"loadMyAssets()\" style=\"margin-top: 10px;\">LOAD MY ASSETS</button>\n" +
            "            </div>\n" +
            "            <div id=\"assetsList\" style=\"margin-top: 20px;\"></div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <div class=\"grid-container\" style=\"margin-top: 20px;\">\n" +
            "        <div class=\"panel\" style=\"grid-column: 1 / -1;\">\n" +
            "            <h2>📡 LIVE LEDGER</h2>\n" +
            "            <div class=\"terminal\" id=\"ledger\">Asset registry backend operational\nSpring Boot 3.5.0 - Spring 6.2.7\nJava 21.0.8 SHA-256 enabled\nTomcat/10.1.41 on port 8080\n\n[SYSTEM] Blockchain tracker ready\n[SYSTEM] Ownership verification active\n[SYSTEM] File corruption detection enabled\n[STATUS] Awaiting asset commits...</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <script>\n" +
            "        async function commitAsset() {\n" +
            "            const file = document.getElementById('assetFile').files[0];\n" +
            "            const statusDiv = document.getElementById('uploadStatus');\n" +
            "            const ledger = document.getElementById('ledger');\n" +
            "            \n" +
            "            if (!file) {\n" +
            "                statusDiv.innerHTML = '<span class=\"error\">❌ Select a file</span>';\n" +
            "                return;\n" +
            "            }\n" +
            "            \n" +
            "            statusDiv.innerHTML = '<span class=\"warning\">⏳ Processing...</span>';\n" +
            "            \n" +
            "            const formData = new FormData();\n" +
            "            formData.append('file', file);\n" +
            "            \n" +
            "            try {\n" +
            "                const response = await fetch('/api/assets/upload', { method: 'POST', body: formData });\n" +
            "                const data = await response.json();\n" +
            "                \n" +
            "                if (data.success) {\n" +
            "                    statusDiv.innerHTML = '<span class=\"success\">✓ COMMITTED!</span><br/><br/>' +\n" +
            "                        '<strong>🔑 OWNER TOKEN (SAVE THIS!):</strong><br/>' +\n" +
            "                        '<code style=\"color: var(--neon); word-break: break-all; padding: 8px; background: #222; display: block; margin: 8px 0;\">' + data.ownerToken + '</code>' +\n" +
            "                        '<button onclick=\"copyToClipboard(\\'' + data.ownerToken + '\\')\" class=\"btn-small\" style=\"margin-top: 8px;\">📋 Copy Token</button><br/><br/>' +\n" +
            "                        '<strong>SHA-256 Hash:</strong><br/>' +\n" +
            "                        '<code style=\"color: var(--neon); word-break: break-all; padding: 8px; background: #222; display: block; margin: 8px 0;\">' + data.hash + '</code>';\n" +
            "                    ledger.textContent += '\\n\\n[UPLOAD] File: ' + file.name + ' (' + (file.size/1024).toFixed(2) + 'KB)\\n[HASH] ' + data.hash.substring(0, 16) + '...\\n[TIME] ' + new Date().toLocaleTimeString() + '\\n[STATUS] ✓ Asset registered - token issued';\n" +
            "                    document.getElementById('assetFile').value = '';\n" +
            "                } else {\n" +
            "                    statusDiv.innerHTML = '<span class=\"error\">❌ Error: ' + data.error + '</span>';\n" +
            "                }\n" +
            "            } catch (err) {\n" +
            "                statusDiv.innerHTML = '<span class=\"error\">❌ Error: ' + err.message + '</span>';\n" +
            "            }\n" +
            "        }\n" +
            "            }\n" +
            "            if (!file) {\n" +
            "                statusDiv.innerHTML = '<span class=\"error\">❌ Select file to verify</span>';\n" +
            "                return;\n" +
            "            }\n" +
            "            \n" +
            "            statusDiv.innerHTML = '<span class=\"warning\">🔍 Verifying...</span>';\n" +
            "            \n" +
            "            const formData = new FormData();\n" +
            "            formData.append('file', file);\n" +
            "            formData.append('hash', hash);\n" +
            "            \n" +
            "            try {\n" +
            "                const response = await fetch('/api/assets/verify', { method: 'POST', body: formData });\n" +
            "                const data = await response.json();\n" +
            "                \n" +
            "                if (data.isValid) {\n" +
            "                    statusDiv.innerHTML = '<span class=\"success\">✓ FILE VERIFIED SAFE!</span><br/>File has not been corrupted or modified. It matches the original hash.';\n" +
            "                } else {\n" +
            "                    statusDiv.innerHTML = '<span class=\"error\">❌ HASH MISMATCH!</span><br/>File has changed or is corrupted. Hash does not match the original.';\n" +
            "                }\n" +
            "            } catch (err) {\n" +
            "                statusDiv.innerHTML = '<span class=\"error\">❌ Error: ' + err.message + '</span>';\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        async function loadMyAssets() {\n" +
            "            const token = document.getElementById('ownerToken').value.trim();\n" +
            "            const assetsList = document.getElementById('assetsList');\n" +
            "            \n" +
            "            if (!token) {\n" +
            "                assetsList.innerHTML = '<span class=\"error\">❌ Paste your owner token first</span>';\n" +
            "                return;\n" +
            "            }\n" +
            "            \n" +
            "            assetsList.innerHTML = '<span class=\"warning\">⏳ Loading...</span>';\n" +
            "            \n" +
            "            try {\n" +
            "                const response = await fetch('/api/assets/list?ownerToken=' + encodeURIComponent(token));\n" +
            "                const data = await response.json();\n" +
            "                \n" +
            "                if (data.success && data.assetCount > 0) {\n" +
            "                    let html = '<span class=\"success\">✓ Found ' + data.assetCount + ' asset(s)</span><br/><br/>';\n" +
            "                    data.assets.forEach(asset => {\n" +
            "                        html += '<div class=\"asset-item\">' +\n" +
            "                            '<div class=\"asset-name\">📄 ' + asset.fileName + '</div>' +\n" +
            "                            '<div class=\"asset-hash\">Hash: ' + asset.hash + '</div>' +\n" +
            "                            '<div class=\"asset-info\">Size: ' + (asset.fileSize/1024).toFixed(2) + ' KB | ' + new Date(asset.uploadedAt).toLocaleString() + '</div>' +\n" +
            "                            '<div class=\"asset-actions\">' +\n" +
            "                            '<button class=\"btn-small\" onclick=\"downloadAsset(\\'' + asset.hash + '\\', \\'' + token + '\\')\" title=\"Download your file\">📥 Download</button>' +\n" +
            "                            '<button class=\"btn-small\" onclick=\"copyHash(\\'' + asset.hash + '\\')\" title=\"Copy hash to clipboard\">📋 Copy Hash</button>' +\n" +
            "                            '<button class=\"btn-small\" onclick=\"deleteAsset(\\'' + asset.hash + '\\', \\'' + token + '\\')\" title=\"Delete this file\">🗑️ Delete</button>' +\n" +
            "                            '</div>' +\n" +
            "                            '</div>';\n" +
            "                    });\n" +
            "                    assetsList.innerHTML = html;\n" +
            "                } else {\n" +
            "                    assetsList.innerHTML = '<span class=\"error\">❌ No assets found for this owner token</span>';\n" +
            "                }\n" +
            "            } catch (err) {\n" +
            "                assetsList.innerHTML = '<span class=\"error\">❌ Error: ' + err.message + '</span>';\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        function downloadAsset(hash, token) {\n" +
            "            const link = document.createElement('a');\n" +
            "            link.href = '/api/assets/download/' + hash + '?ownerToken=' + encodeURIComponent(token);\n" +
            "            link.click();\n" +
            "        }\n" +
            "        \n" +
            "        async function deleteAsset(hash, token) {\n" +
            "            if (!confirm('⚠️  Are you sure you want to DELETE this file? This cannot be undone!')) {\n" +
            "                return;\n" +
            "            }\n" +
            "            \n" +
            "            try {\n" +
            "                const response = await fetch('/api/assets/delete/' + hash + '?ownerToken=' + encodeURIComponent(token), {\n" +
            "                    method: 'DELETE'\n" +
            "                });\n" +
            "                const data = await response.json();\n" +
            "                \n" +
            "                if (data.success) {\n" +
            "                    alert('✓ ' + data.message);\n" +
            "                    loadMyAssets();\n" +
            "                } else {\n" +
            "                    alert('❌ Error: ' + data.error);\n" +
            "                }\n" +
            "            } catch (err) {\n" +
            "                alert('❌ Error: ' + err.message);\n" +
            "            }\n" +
            "        }\n" +
            "        \n" +
            "        function copyHash(hash) {\n" +
            "            navigator.clipboard.writeText(hash).then(() => {\n" +
            "                alert('✓ Hash copied to clipboard!');\n" +
            "            });\n" +
            "        }\n" +
            "        \n" +
            "        function copyToClipboard(text) {\n" +
            "            navigator.clipboard.writeText(text).then(() => {\n" +
            "                alert('✓ Owner Token copied! Save it safely.');\n" +
            "            });\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("framework", "Spring Boot 3.5.0");
        response.put("message", "Asset Registry Backend - Blockchain based asset registry service");
        response.put("version", "1.0.0");
        response.put("status", "running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Asset Registry Backend");
        return ResponseEntity.ok(response);
    }
}
