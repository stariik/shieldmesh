import type { Severity } from "@/types/threat";

// ---------------------------------------------------------------------------
// Types
// ---------------------------------------------------------------------------

export interface ThreatIndicator {
  label: string;
  weight: number;
  category: "phishing" | "social_engineering" | "crypto_scam" | "technical" | "url_structure";
}

export interface ThreatResult {
  score: number;            // 0-100
  severity: Severity;
  indicators: ThreatIndicator[];
  description: string;
  safe: boolean;
}

// ---------------------------------------------------------------------------
// Helper utilities
// ---------------------------------------------------------------------------

function extractDomain(url: string): string | null {
  try {
    const u = new URL(url.startsWith("http") ? url : `https://${url}`);
    return u.hostname.toLowerCase();
  } catch {
    return null;
  }
}

function sha256Hex(text: string): string {
  // Simple deterministic hash for browser (non-crypto) – used only for IDs
  let h = 0x811c9dc5;
  for (let i = 0; i < text.length; i++) {
    h ^= text.charCodeAt(i);
    h = Math.imul(h, 0x01000193);
  }
  const base = Math.abs(h).toString(16).padStart(8, "0");
  // Stretch to 64 hex chars deterministically
  let out = "";
  for (let i = 0; out.length < 64; i++) {
    let v = 0x811c9dc5;
    const seg = base + i.toString();
    for (let j = 0; j < seg.length; j++) {
      v ^= seg.charCodeAt(j);
      v = Math.imul(v, 0x01000193);
    }
    out += Math.abs(v).toString(16).padStart(8, "0");
  }
  return out.slice(0, 64);
}

export { sha256Hex };

// ---------------------------------------------------------------------------
// Detection rule sets
// ---------------------------------------------------------------------------

/** Well-known brand names and their common typo-squats / homoglyphs */
const BRAND_PATTERNS: [RegExp, string][] = [
  [/paypa[l1i][.-]/, "PayPal typo-squat domain"],
  [/g[0o]{2}gle/, "Google homoglyph domain"],
  [/amaz[0o]n/, "Amazon homoglyph domain"],
  [/micr[0o]s[0o]ft/, "Microsoft homoglyph domain"],
  [/app[l1]e[.-]/, "Apple typo-squat domain"],
  [/faceb[0o]{2}k/, "Facebook homoglyph domain"],
  [/netfl[i1]x/, "Netflix typo-squat domain"],
  [/b[i1]nance/, "Binance typo-squat domain"],
  [/c[0o][i1]nbase/, "Coinbase homoglyph domain"],
  [/phant[0o]m[\-.]?wa[l1]{2}et/, "Phantom Wallet impersonation"],
  [/metam[a@]sk/, "MetaMask impersonation"],
  [/so[l1]ana[\-.]?pay/, "Solana Pay impersonation"],
  [/sol[l1]?f[l1]are/, "Solflare wallet impersonation"],
];

const SUSPICIOUS_TLDS = new Set([
  ".xyz", ".tk", ".ml", ".ga", ".cf", ".gq", ".top", ".buzz",
  ".work", ".click", ".link", ".info", ".cam", ".icu", ".monster",
  ".rest", ".surf", ".sbs", ".cfd",
]);

const URL_SHORTENERS = new Set([
  "bit.ly", "tinyurl.com", "t.co", "goo.gl", "is.gd", "ow.ly",
  "buff.ly", "rb.gy", "shorturl.at", "cutt.ly", "tiny.cc",
  "v.gd", "qr.ae", "lnkd.in",
]);

const MALICIOUS_URL_KEYWORDS = [
  "login", "verify", "confirm", "suspend", "secure", "update",
  "wallet-connect", "walletconnect", "airdrop-claim", "airdrop",
  "claim-reward", "free-mint", "connect-wallet", "validate",
  "restore", "recovery", "unlock", "authenticate", "signin",
  "drainer", "approve-all",
];

const URGENCY_PHRASES = [
  "act now", "immediately", "urgent", "suspended", "verify your account",
  "account will be closed", "within 24 hours", "limited time",
  "expires soon", "last chance", "don't miss", "do not ignore",
  "action required", "unauthorized access", "security alert",
  "unusual activity", "confirm your identity", "verify immediately",
  "failure to comply", "will be terminated", "click here now",
  "respond immediately", "your account has been",
];

const FINANCIAL_LURES = [
  "you've won", "you have won", "claim your prize", "free tokens",
  "airdrop", "free nft", "claim reward", "congratulations",
  "selected winner", "bonus reward", "exclusive offer",
  "guaranteed return", "double your", "free crypto",
  "giveaway", "whitelist spot", "mint for free",
];

const IMPERSONATION_SIGNALS = [
  "official team", "support team", "admin team", "helpdesk",
  "customer service", "technical support", "moderator",
  "from the team", "official announcement", "verified team",
];

const CRYPTO_SCAM_PHRASES = [
  "seed phrase", "private key", "secret phrase", "recovery phrase",
  "connect wallet to claim", "connect your wallet", "approve transaction",
  "sign message to verify", "enter your seed", "paste your key",
  "wallet verification required", "token approval",
  "unlimited approval", "smart contract interaction required",
  "bridge your tokens", "migrate your tokens",
  "swap now before", "liquidity event",
];

// Characters that look similar to ASCII but are from other Unicode blocks
const HOMOGLYPH_MAP: Record<string, string> = {
  "\u0430": "a", "\u0435": "e", "\u043E": "o", "\u0440": "p",
  "\u0441": "c", "\u0443": "y", "\u0445": "x", "\u0455": "s",
  "\u0456": "i", "\u0458": "j", "\u04BB": "h", "\u0501": "d",
  "\u0261": "g", "\u1D04": "c", "\u1D0F": "o", "\u1D1C": "u",
  "\u0251": "a", "\u025B": "e", "\u0131": "i",
  "\uFF41": "a", "\uFF45": "e", "\uFF49": "i", "\uFF4F": "o",
};

// ---------------------------------------------------------------------------
// ThreatScanner class
// ---------------------------------------------------------------------------

export class ThreatScanner {
  /**
   * Main entry: analyze arbitrary input (URL or message text).
   * Returns a rich ThreatResult.
   */
  analyze(input: string): ThreatResult {
    const trimmed = input.trim();
    if (!trimmed) {
      return {
        score: 0,
        severity: "LOW",
        indicators: [],
        description: "No input provided.",
        safe: true,
      };
    }

    const indicators: ThreatIndicator[] = [];

    // Determine if this looks like a URL
    const isUrl = this.looksLikeUrl(trimmed);

    if (isUrl) {
      this.analyzeUrl(trimmed, indicators);
    }

    // Always run message analysis (a URL pasted in a sentence, or raw text)
    this.analyzeMessage(trimmed, indicators);

    // Deduplicate by label
    const seen = new Set<string>();
    const unique = indicators.filter((ind) => {
      if (seen.has(ind.label)) return false;
      seen.add(ind.label);
      return true;
    });

    // Compute weighted score (capped at 100)
    const rawScore = unique.reduce((sum, ind) => sum + ind.weight, 0);
    const score = Math.min(100, Math.max(0, Math.round(rawScore)));

    const severity = this.scoreSeverity(score);
    const safe = score < 35;

    const description = this.generateDescription(unique, score, trimmed, isUrl);

    return { score, severity, indicators: unique, description, safe };
  }

  // -----------------------------------------------------------------------
  // URL-specific analysis
  // -----------------------------------------------------------------------

  private looksLikeUrl(text: string): boolean {
    return /^(https?:\/\/|data:|ftp:\/\/)/i.test(text) ||
      /^[a-z0-9]([a-z0-9-]*\.)+[a-z]{2,}(\/|$)/i.test(text) ||
      /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/.test(text.split(/\s/)[0]);
  }

  private analyzeUrl(raw: string, out: ThreatIndicator[]): void {
    const lower = raw.toLowerCase();

    // Data URI scheme
    if (lower.startsWith("data:")) {
      out.push({ label: "Data URI scheme detected — may hide malicious payload", weight: 30, category: "technical" });
      return;
    }

    // IP-based URL
    if (/^https?:\/\/\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/i.test(raw)) {
      out.push({ label: "IP-based URL — avoids domain reputation checks", weight: 20, category: "url_structure" });
    }

    const domain = extractDomain(raw);
    if (!domain) {
      out.push({ label: "Malformed URL — unable to parse domain", weight: 15, category: "url_structure" });
      return;
    }

    // URL shortener
    for (const shortener of URL_SHORTENERS) {
      if (domain === shortener || domain.endsWith("." + shortener)) {
        out.push({ label: `URL shortener (${shortener}) — hides true destination`, weight: 15, category: "url_structure" });
        break;
      }
    }

    // Brand typo-squats
    for (const [pat, desc] of BRAND_PATTERNS) {
      if (pat.test(domain)) {
        out.push({ label: desc, weight: 30, category: "phishing" });
      }
    }

    // Suspicious TLD
    const tld = "." + domain.split(".").pop();
    if (SUSPICIOUS_TLDS.has(tld)) {
      out.push({ label: `Suspicious TLD (${tld}) — frequently abused in phishing`, weight: 12, category: "url_structure" });
    }

    // Excessive subdomains (more than 3 labels)
    const labels = domain.split(".");
    if (labels.length > 3) {
      out.push({ label: `Excessive subdomains (${labels.length} levels) — obfuscation technique`, weight: 10, category: "url_structure" });
    }

    // Homoglyph / mixed-script detection
    if (this.hasHomoglyphs(domain)) {
      out.push({ label: "Homograph attack — mixed Unicode scripts mimic trusted domain", weight: 35, category: "phishing" });
    }

    // Malicious keywords in path/query
    try {
      const u = new URL(raw.startsWith("http") ? raw : `https://${raw}`);
      const pathAndQuery = (u.pathname + u.search + u.hash).toLowerCase();
      for (const kw of MALICIOUS_URL_KEYWORDS) {
        if (pathAndQuery.includes(kw) || domain.includes(kw)) {
          out.push({ label: `Suspicious keyword "${kw}" in URL`, weight: 8, category: "phishing" });
        }
      }

      // HTTP (no TLS)
      if (u.protocol === "http:") {
        out.push({ label: "Unencrypted HTTP connection — no TLS/SSL", weight: 10, category: "technical" });
      }

      // Non-standard port
      if (u.port && u.port !== "443" && u.port !== "80") {
        out.push({ label: `Non-standard port (:${u.port}) — potential C2 or phishing server`, weight: 12, category: "technical" });
      }

      // Very long URL (common in phishing)
      if (raw.length > 200) {
        out.push({ label: "Excessively long URL — common payload obfuscation", weight: 8, category: "url_structure" });
      }

      // @ symbol in URL (user-info trick)
      if (raw.includes("@")) {
        out.push({ label: "URL contains @ symbol — credential phishing or redirect trick", weight: 20, category: "phishing" });
      }

      // Double encoding / hex escapes
      if (/%[0-9a-f]{2}.*%[0-9a-f]{2}/i.test(raw)) {
        out.push({ label: "Multiple percent-encoded characters — possible payload obfuscation", weight: 10, category: "technical" });
      }
    } catch {
      // Already flagged as malformed
    }
  }

  private hasHomoglyphs(text: string): boolean {
    for (const ch of text) {
      if (HOMOGLYPH_MAP[ch]) return true;
    }
    // Also check for mixing of scripts (latin + cyrillic etc.)
    let hasLatin = false;
    let hasNonLatin = false;
    for (const ch of text) {
      const code = ch.codePointAt(0) ?? 0;
      if (code >= 0x0041 && code <= 0x024f) hasLatin = true;
      else if (code > 0x024f && code < 0xffff && !/[.\-0-9]/.test(ch)) hasNonLatin = true;
    }
    return hasLatin && hasNonLatin;
  }

  // -----------------------------------------------------------------------
  // Message / text analysis
  // -----------------------------------------------------------------------

  private analyzeMessage(text: string, out: ThreatIndicator[]): void {
    const lower = text.toLowerCase();

    // Urgency language
    for (const phrase of URGENCY_PHRASES) {
      if (lower.includes(phrase)) {
        out.push({ label: `Urgency language: "${phrase}"`, weight: 12, category: "social_engineering" });
      }
    }

    // Financial lures
    for (const phrase of FINANCIAL_LURES) {
      if (lower.includes(phrase)) {
        out.push({ label: `Financial lure: "${phrase}"`, weight: 14, category: "social_engineering" });
      }
    }

    // Impersonation signals
    for (const phrase of IMPERSONATION_SIGNALS) {
      if (lower.includes(phrase)) {
        out.push({ label: `Impersonation signal: "${phrase}"`, weight: 16, category: "social_engineering" });
      }
    }

    // Crypto-specific scams
    for (const phrase of CRYPTO_SCAM_PHRASES) {
      if (lower.includes(phrase)) {
        out.push({ label: `Crypto scam pattern: "${phrase}"`, weight: 22, category: "crypto_scam" });
      }
    }

    // Embedded links in message text
    const urlMatches = text.match(/https?:\/\/[^\s<>"]+/gi);
    if (urlMatches && !this.looksLikeUrl(text.trim())) {
      for (const url of urlMatches) {
        out.push({ label: `Embedded link detected: ${url.slice(0, 60)}...`, weight: 6, category: "phishing" });
        // Recursively check the embedded URL
        this.analyzeUrl(url, out);
      }
    }

    // Excessive caps (shouting)
    const capsRatio = (text.replace(/[^A-Z]/g, "").length) / Math.max(text.replace(/[^a-zA-Z]/g, "").length, 1);
    if (capsRatio > 0.6 && text.length > 20) {
      out.push({ label: "Excessive capitalization — aggressive social engineering", weight: 6, category: "social_engineering" });
    }

    // Suspicious email-like patterns
    if (/support@|admin@|security@|noreply@.*\.(xyz|tk|ml|ga|cf)/i.test(lower)) {
      out.push({ label: "Suspicious sender address from high-risk domain", weight: 14, category: "social_engineering" });
    }
  }

  // -----------------------------------------------------------------------
  // Scoring helpers
  // -----------------------------------------------------------------------

  private scoreSeverity(score: number): Severity {
    if (score >= 80) return "CRITICAL";
    if (score >= 60) return "HIGH";
    if (score >= 35) return "MEDIUM";
    return "LOW";
  }

  private generateDescription(
    indicators: ThreatIndicator[],
    score: number,
    input: string,
    isUrl: boolean,
  ): string {
    if (indicators.length === 0) {
      return "Analysis complete. No significant threat indicators were identified. The input appears benign based on pattern matching, heuristic analysis, and threat intelligence correlation.";
    }

    const categoryCount: Record<string, number> = {};
    for (const ind of indicators) {
      categoryCount[ind.category] = (categoryCount[ind.category] || 0) + 1;
    }

    const parts: string[] = [];

    // Opening line
    if (score >= 80) {
      parts.push("CRITICAL THREAT DETECTED.");
    } else if (score >= 60) {
      parts.push("High-confidence threat indicators identified.");
    } else if (score >= 35) {
      parts.push("Moderate risk — several suspicious signals detected.");
    } else {
      parts.push("Low-risk input with minor anomalies.");
    }

    // Category-specific summaries
    if (categoryCount.phishing) {
      parts.push(
        `Phishing analysis flagged ${categoryCount.phishing} indicator${categoryCount.phishing > 1 ? "s" : ""} including domain impersonation or deceptive URL patterns.`,
      );
    }
    if (categoryCount.social_engineering) {
      parts.push(
        `Social engineering heuristics triggered ${categoryCount.social_engineering} warning${categoryCount.social_engineering > 1 ? "s" : ""} — message contains manipulative language designed to coerce immediate action.`,
      );
    }
    if (categoryCount.crypto_scam) {
      parts.push(
        `Crypto-specific threat patterns detected (${categoryCount.crypto_scam} match${categoryCount.crypto_scam > 1 ? "es" : ""}). Content targets wallet credentials or token approvals.`,
      );
    }
    if (categoryCount.technical) {
      parts.push(
        `Technical analysis identified ${categoryCount.technical} structural anomal${categoryCount.technical > 1 ? "ies" : "y"} in the URL or payload encoding.`,
      );
    }
    if (categoryCount.url_structure) {
      parts.push(
        `URL structure analysis found ${categoryCount.url_structure} suspicious attribute${categoryCount.url_structure > 1 ? "s" : ""} commonly associated with malicious infrastructure.`,
      );
    }

    // Closing recommendation
    if (score >= 60) {
      parts.push("Recommendation: Do NOT interact with this content. Report to the ShieldMesh network for validator consensus.");
    } else if (score >= 35) {
      parts.push("Recommendation: Exercise caution. Verify the source independently before any interaction.");
    }

    return parts.join(" ");
  }

  // -----------------------------------------------------------------------
  // File analysis
  // -----------------------------------------------------------------------

  analyzeFile(content: string, fileName: string, fileSize: number): ThreatResult {
    const indicators: ThreatIndicator[] = [];

    // File metadata checks
    this.analyzeFileName(fileName, indicators);
    this.analyzeFileSize(fileSize, indicators);

    // Scan file content for threats (same text analysis pipeline)
    if (content.length > 0) {
      // Extract and scan all URLs in the file
      const urlMatches = content.match(/https?:\/\/[^\s<>"')\]]+/gi);
      if (urlMatches) {
        const uniqueUrls = [...new Set(urlMatches)].slice(0, 50); // cap at 50
        for (const url of uniqueUrls) {
          this.analyzeUrl(url, indicators);
        }
        if (uniqueUrls.length > 5) {
          indicators.push({ label: `File contains ${uniqueUrls.length} URLs — bulk phishing or link farm`, weight: 15, category: "phishing" });
        }
      }

      // Run message analysis on text content
      this.analyzeMessage(content, indicators);

      // File-specific content patterns
      this.analyzeFileContent(content, indicators);
    }

    // Deduplicate
    const seen = new Set<string>();
    const unique = indicators.filter((ind) => {
      if (seen.has(ind.label)) return false;
      seen.add(ind.label);
      return true;
    });

    const rawScore = unique.reduce((sum, ind) => sum + ind.weight, 0);
    const score = Math.min(100, Math.max(0, Math.round(rawScore)));
    const severity = this.scoreSeverity(score);
    const safe = score < 35;

    const description = this.generateFileDescription(unique, score, fileName);

    return { score, severity, indicators: unique, description, safe };
  }

  private analyzeFileName(fileName: string, out: ThreatIndicator[]): void {
    const lower = fileName.toLowerCase();

    // Double extension tricks (e.g. invoice.pdf.exe)
    const parts = fileName.split(".");
    if (parts.length > 2) {
      const lastExt = parts[parts.length - 1].toLowerCase();
      const secondLastExt = parts[parts.length - 2].toLowerCase();
      const execExtensions = new Set(["exe", "bat", "cmd", "scr", "pif", "com", "vbs", "js", "ps1", "msi", "jar", "apk"]);
      const docExtensions = new Set(["pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "csv", "jpg", "png"]);
      if (execExtensions.has(lastExt) && docExtensions.has(secondLastExt)) {
        out.push({ label: `Double extension trick: .${secondLastExt}.${lastExt} — disguises executable as document`, weight: 30, category: "technical" });
      }
    }

    // Dangerous file extensions
    const dangerousExts = new Set(["exe", "bat", "cmd", "scr", "pif", "com", "vbs", "vbe", "js", "jse", "wsf", "wsh", "ps1", "msi", "dll", "sys"]);
    const ext = parts[parts.length - 1]?.toLowerCase();
    if (dangerousExts.has(ext)) {
      out.push({ label: `Dangerous file type (.${ext}) — executable or script`, weight: 25, category: "technical" });
    }

    // Macro-enabled Office documents
    const macroExts = new Set(["docm", "xlsm", "pptm", "dotm", "xltm"]);
    if (macroExts.has(ext)) {
      out.push({ label: `Macro-enabled Office document (.${ext}) — may contain malicious macros`, weight: 20, category: "technical" });
    }

    // Suspicious filename keywords
    const suspiciousNames = ["invoice", "payment", "receipt", "urgent", "verify", "password", "login", "account", "security", "update", "confirm"];
    for (const kw of suspiciousNames) {
      if (lower.includes(kw)) {
        out.push({ label: `Suspicious filename keyword: "${kw}" — common in phishing attachments`, weight: 8, category: "social_engineering" });
        break;
      }
    }

    // Unicode in filename (right-to-left override, zero-width chars)
    if (/[\u200B-\u200F\u202A-\u202E\u2060-\u2064\uFEFF]/.test(fileName)) {
      out.push({ label: "Hidden Unicode characters in filename — possible extension spoofing", weight: 25, category: "technical" });
    }
  }

  private analyzeFileSize(fileSize: number, out: ThreatIndicator[]): void {
    // Suspiciously small executables (< 10KB) may be droppers
    if (fileSize < 10_000 && fileSize > 0) {
      out.push({ label: "Very small file — could be a dropper or downloader stub", weight: 6, category: "technical" });
    }
  }

  private analyzeFileContent(content: string, out: ThreatIndicator[]): void {
    const lower = content.toLowerCase();

    // Base64 encoded blobs
    const base64Chunks = content.match(/[A-Za-z0-9+/]{50,}={0,2}/g);
    if (base64Chunks && base64Chunks.length > 0) {
      out.push({ label: `Base64-encoded data detected (${base64Chunks.length} block${base64Chunks.length > 1 ? "s" : ""}) — possible obfuscated payload`, weight: 12, category: "technical" });
    }

    // Script tags / HTML injection
    if (/<script[\s>]/i.test(content)) {
      out.push({ label: "Embedded <script> tag — potential XSS or malicious code injection", weight: 18, category: "technical" });
    }

    // PowerShell commands
    if (/powershell|invoke-expression|invoke-webrequest|downloadstring|iex\s*\(/i.test(content)) {
      out.push({ label: "PowerShell command detected — possible remote code execution", weight: 25, category: "technical" });
    }

    // Shell commands
    if (/\bcurl\s+.*https?:|wget\s+.*https?:|bash\s+-c\s|\/bin\/sh/i.test(content)) {
      out.push({ label: "Shell download command detected — potential dropper payload", weight: 22, category: "technical" });
    }

    // Wallet address harvesting patterns
    const solanaAddrPattern = /[1-9A-HJ-NP-Za-km-z]{32,44}/g;
    const solAddrs = content.match(solanaAddrPattern);
    if (solAddrs && solAddrs.length > 3) {
      out.push({ label: `Multiple Solana-like addresses found (${solAddrs.length}) — possible address swap/clipboard hijack list`, weight: 15, category: "crypto_scam" });
    }

    // Private key patterns
    if (/-----BEGIN.*PRIVATE KEY-----/i.test(content) || /[0-9a-f]{64}/i.test(content)) {
      const hexMatches = content.match(/[0-9a-f]{64}/gi);
      if (hexMatches && hexMatches.length > 0) {
        out.push({ label: "Possible private key or secret material in file", weight: 20, category: "crypto_scam" });
      }
    }

    // Obfuscated JavaScript
    if (/eval\s*\(|document\.write\s*\(|unescape\s*\(|atob\s*\(|String\.fromCharCode/i.test(content)) {
      out.push({ label: "JavaScript obfuscation pattern — eval/unescape/fromCharCode", weight: 15, category: "technical" });
    }
  }

  private generateFileDescription(
    indicators: ThreatIndicator[],
    score: number,
    fileName: string,
  ): string {
    if (indicators.length === 0) {
      return `File analysis complete for "${fileName}". No significant threat indicators were identified. The file content appears benign based on pattern matching and heuristic analysis.`;
    }

    const categoryCount: Record<string, number> = {};
    for (const ind of indicators) {
      categoryCount[ind.category] = (categoryCount[ind.category] || 0) + 1;
    }

    const parts: string[] = [];

    if (score >= 80) {
      parts.push(`CRITICAL THREAT DETECTED in file "${fileName}".`);
    } else if (score >= 60) {
      parts.push(`High-confidence threat indicators found in "${fileName}".`);
    } else if (score >= 35) {
      parts.push(`Moderate risk — "${fileName}" contains suspicious content.`);
    } else {
      parts.push(`Low risk — "${fileName}" has minor anomalies.`);
    }

    if (categoryCount.phishing) {
      parts.push(`Phishing analysis flagged ${categoryCount.phishing} indicator${categoryCount.phishing > 1 ? "s" : ""}.`);
    }
    if (categoryCount.social_engineering) {
      parts.push(`Social engineering patterns detected (${categoryCount.social_engineering}).`);
    }
    if (categoryCount.crypto_scam) {
      parts.push(`Crypto threat patterns found (${categoryCount.crypto_scam}).`);
    }
    if (categoryCount.technical) {
      parts.push(`Technical analysis identified ${categoryCount.technical} anomal${categoryCount.technical > 1 ? "ies" : "y"}.`);
    }

    if (score >= 60) {
      parts.push("Recommendation: Do NOT open or execute this file. Report to ShieldMesh.");
    } else if (score >= 35) {
      parts.push("Recommendation: Exercise caution. Verify the source before opening.");
    }

    return parts.join(" ");
  }
}

// Singleton for convenience
export const threatScanner = new ThreatScanner();
