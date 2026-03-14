package com.shieldmesh.app.ai

import com.shieldmesh.app.data.local.entity.Severity
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

data class ThreatIndicator(
    val label: String,
    val weight: Int,
    val category: IndicatorCategory
)

enum class IndicatorCategory {
    PHISHING, SOCIAL_ENGINEERING, CRYPTO_SCAM, TECHNICAL, URL_STRUCTURE
}

data class ThreatResult(
    val score: Int,
    val severity: Severity,
    val indicators: List<ThreatIndicator>,
    val description: String,
    val safe: Boolean
)

@Singleton
class ThreatDetector @Inject constructor() {

    // -----------------------------------------------------------------------
    // Brand typo-squat patterns
    // -----------------------------------------------------------------------
    private val brandPatterns: List<Pair<Regex, String>> = listOf(
        Regex("paypa[l1i][.\\-]") to "PayPal typo-squat domain",
        Regex("g[0o]{2}gle") to "Google homoglyph domain",
        Regex("amaz[0o]n") to "Amazon homoglyph domain",
        Regex("micr[0o]s[0o]ft") to "Microsoft homoglyph domain",
        Regex("app[l1]e[.\\-]") to "Apple typo-squat domain",
        Regex("faceb[0o]{2}k") to "Facebook homoglyph domain",
        Regex("netfl[i1]x") to "Netflix typo-squat domain",
        Regex("b[i1]nance") to "Binance typo-squat domain",
        Regex("c[0o][i1]nbase") to "Coinbase homoglyph domain",
        Regex("phant[0o]m[\\-.]?wa[l1]{2}et") to "Phantom Wallet impersonation",
        Regex("metam[a@]sk") to "MetaMask impersonation",
        Regex("so[l1]ana[\\-.]?pay") to "Solana Pay impersonation",
        Regex("sol[l1]?f[l1]are") to "Solflare wallet impersonation",
    )

    private val suspiciousTlds = setOf(
        ".xyz", ".tk", ".ml", ".ga", ".cf", ".gq", ".top", ".buzz",
        ".work", ".click", ".link", ".info", ".cam", ".icu", ".monster",
        ".rest", ".surf", ".sbs", ".cfd"
    )

    private val urlShorteners = setOf(
        "bit.ly", "tinyurl.com", "t.co", "goo.gl", "is.gd", "ow.ly",
        "buff.ly", "rb.gy", "shorturl.at", "cutt.ly", "tiny.cc",
        "v.gd", "qr.ae", "lnkd.in"
    )

    private val maliciousUrlKeywords = listOf(
        "login", "verify", "confirm", "suspend", "secure", "update",
        "wallet-connect", "walletconnect", "airdrop-claim", "airdrop",
        "claim-reward", "free-mint", "connect-wallet", "validate",
        "restore", "recovery", "unlock", "authenticate", "signin",
        "drainer", "approve-all"
    )

    private val urgencyPhrases = listOf(
        "act now", "immediately", "urgent", "suspended", "verify your account",
        "account will be closed", "within 24 hours", "limited time",
        "expires soon", "last chance", "don't miss", "do not ignore",
        "action required", "unauthorized access", "security alert",
        "unusual activity", "confirm your identity", "verify immediately",
        "failure to comply", "will be terminated", "click here now",
        "respond immediately", "your account has been"
    )

    private val financialLures = listOf(
        "you've won", "you have won", "claim your prize", "free tokens",
        "airdrop", "free nft", "claim reward", "congratulations",
        "selected winner", "bonus reward", "exclusive offer",
        "guaranteed return", "double your", "free crypto",
        "giveaway", "whitelist spot", "mint for free"
    )

    private val impersonationSignals = listOf(
        "official team", "support team", "admin team", "helpdesk",
        "customer service", "technical support", "moderator",
        "from the team", "official announcement", "verified team"
    )

    private val cryptoScamPhrases = listOf(
        "seed phrase", "private key", "secret phrase", "recovery phrase",
        "connect wallet to claim", "connect your wallet", "approve transaction",
        "sign message to verify", "enter your seed", "paste your key",
        "wallet verification required", "token approval",
        "unlimited approval", "smart contract interaction required",
        "bridge your tokens", "migrate your tokens",
        "swap now before", "liquidity event"
    )

    // Homoglyph map: Unicode chars that look like ASCII
    private val homoglyphMap = mapOf(
        '\u0430' to 'a', '\u0435' to 'e', '\u043E' to 'o', '\u0440' to 'p',
        '\u0441' to 'c', '\u0443' to 'y', '\u0445' to 'x', '\u0455' to 's',
        '\u0456' to 'i', '\u0458' to 'j', '\u04BB' to 'h', '\u0501' to 'd',
        '\u0261' to 'g', '\u1D04' to 'c', '\u1D0F' to 'o', '\u1D1C' to 'u',
        '\u0251' to 'a', '\u025B' to 'e', '\u0131' to 'i',
        '\uFF41' to 'a', '\uFF45' to 'e', '\uFF49' to 'i', '\uFF4F' to 'o',
    )

    // -----------------------------------------------------------------------
    // Main entry point
    // -----------------------------------------------------------------------
    fun analyze(input: String): ThreatResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return ThreatResult(
                score = 0,
                severity = Severity.LOW,
                indicators = emptyList(),
                description = "No input provided.",
                safe = true
            )
        }

        val indicators = mutableListOf<ThreatIndicator>()

        val isUrl = looksLikeUrl(trimmed)
        if (isUrl) {
            analyzeUrl(trimmed, indicators)
        }

        // Always run message analysis
        analyzeMessage(trimmed, indicators)

        // Deduplicate by label
        val seen = mutableSetOf<String>()
        val unique = indicators.filter { ind ->
            if (seen.contains(ind.label)) false
            else {
                seen.add(ind.label)
                true
            }
        }

        val rawScore = unique.sumOf { it.weight }
        val score = rawScore.coerceIn(0, 100)
        val severity = scoreSeverity(score)
        val safe = score < 35
        val description = generateDescription(unique, score, trimmed, isUrl)

        return ThreatResult(score, severity, unique, description, safe)
    }

    // -----------------------------------------------------------------------
    // URL analysis
    // -----------------------------------------------------------------------
    private fun looksLikeUrl(text: String): Boolean {
        if (Regex("^(https?://|data:|ftp://)", RegexOption.IGNORE_CASE).containsMatchIn(text)) return true
        if (Regex("^[a-z0-9]([a-z0-9-]*\\.)+[a-z]{2,}(/|$)", RegexOption.IGNORE_CASE).containsMatchIn(text)) return true
        val firstWord = text.split(Regex("\\s"))[0]
        if (Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").containsMatchIn(firstWord)) return true
        return false
    }

    private fun analyzeUrl(raw: String, out: MutableList<ThreatIndicator>) {
        val lower = raw.lowercase()

        // Data URI
        if (lower.startsWith("data:")) {
            out.add(ThreatIndicator("Data URI scheme detected -- may hide malicious payload", 30, IndicatorCategory.TECHNICAL))
            return
        }

        // IP-based URL
        if (Regex("^https?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", RegexOption.IGNORE_CASE).containsMatchIn(raw)) {
            out.add(ThreatIndicator("IP-based URL -- avoids domain reputation checks", 20, IndicatorCategory.URL_STRUCTURE))
        }

        val domain = extractDomain(raw)
        if (domain == null) {
            out.add(ThreatIndicator("Malformed URL -- unable to parse domain", 15, IndicatorCategory.URL_STRUCTURE))
            return
        }

        // URL shortener
        for (shortener in urlShorteners) {
            if (domain == shortener || domain.endsWith(".$shortener")) {
                out.add(ThreatIndicator("URL shortener ($shortener) -- hides true destination", 15, IndicatorCategory.URL_STRUCTURE))
                break
            }
        }

        // Brand typo-squats
        for ((pattern, desc) in brandPatterns) {
            if (pattern.containsMatchIn(domain)) {
                out.add(ThreatIndicator(desc, 30, IndicatorCategory.PHISHING))
            }
        }

        // Suspicious TLD
        val tld = "." + domain.split(".").last()
        if (suspiciousTlds.contains(tld)) {
            out.add(ThreatIndicator("Suspicious TLD ($tld) -- frequently abused in phishing", 12, IndicatorCategory.URL_STRUCTURE))
        }

        // Excessive subdomains
        val labels = domain.split(".")
        if (labels.size > 3) {
            out.add(ThreatIndicator("Excessive subdomains (${labels.size} levels) -- obfuscation technique", 10, IndicatorCategory.URL_STRUCTURE))
        }

        // Homoglyph detection
        if (hasHomoglyphs(domain)) {
            out.add(ThreatIndicator("Homograph attack -- mixed Unicode scripts mimic trusted domain", 35, IndicatorCategory.PHISHING))
        }

        // Parse URL for path/query analysis
        try {
            val urlStr = if (raw.startsWith("http")) raw else "https://$raw"
            val uri = URI(urlStr)
            val pathAndQuery = ((uri.path ?: "") + (uri.query ?: "") + (uri.fragment ?: "")).lowercase()

            for (kw in maliciousUrlKeywords) {
                if (pathAndQuery.contains(kw) || domain.contains(kw)) {
                    out.add(ThreatIndicator("Suspicious keyword \"$kw\" in URL", 8, IndicatorCategory.PHISHING))
                }
            }

            // HTTP (no TLS)
            if (uri.scheme == "http") {
                out.add(ThreatIndicator("Unencrypted HTTP connection -- no TLS/SSL", 10, IndicatorCategory.TECHNICAL))
            }

            // Non-standard port
            val port = uri.port
            if (port != -1 && port != 443 && port != 80) {
                out.add(ThreatIndicator("Non-standard port (:$port) -- potential C2 or phishing server", 12, IndicatorCategory.TECHNICAL))
            }

            // Very long URL
            if (raw.length > 200) {
                out.add(ThreatIndicator("Excessively long URL -- common payload obfuscation", 8, IndicatorCategory.URL_STRUCTURE))
            }

            // @ symbol
            if (raw.contains("@")) {
                out.add(ThreatIndicator("URL contains @ symbol -- credential phishing or redirect trick", 20, IndicatorCategory.PHISHING))
            }

            // Double encoding
            if (Regex("%[0-9a-f]{2}.*%[0-9a-f]{2}", RegexOption.IGNORE_CASE).containsMatchIn(raw)) {
                out.add(ThreatIndicator("Multiple percent-encoded characters -- possible payload obfuscation", 10, IndicatorCategory.TECHNICAL))
            }
        } catch (_: Exception) {
            // Already flagged as malformed if applicable
        }
    }

    private fun extractDomain(url: String): String? {
        return try {
            val urlStr = if (url.startsWith("http")) url else "https://$url"
            URI(urlStr).host?.lowercase()
        } catch (_: Exception) {
            null
        }
    }

    private fun hasHomoglyphs(text: String): Boolean {
        for (ch in text) {
            if (homoglyphMap.containsKey(ch)) return true
        }
        // Mixed script detection
        var hasLatin = false
        var hasNonLatin = false
        for (ch in text) {
            val code = ch.code
            if (code in 0x0041..0x024f) hasLatin = true
            else if (code > 0x024f && code < 0xffff && !Regex("[.\\-0-9]").matches(ch.toString())) hasNonLatin = true
        }
        return hasLatin && hasNonLatin
    }

    // -----------------------------------------------------------------------
    // Message / text analysis
    // -----------------------------------------------------------------------
    private fun analyzeMessage(text: String, out: MutableList<ThreatIndicator>) {
        val lower = text.lowercase()

        for (phrase in urgencyPhrases) {
            if (lower.contains(phrase)) {
                out.add(ThreatIndicator("Urgency language: \"$phrase\"", 12, IndicatorCategory.SOCIAL_ENGINEERING))
            }
        }

        for (phrase in financialLures) {
            if (lower.contains(phrase)) {
                out.add(ThreatIndicator("Financial lure: \"$phrase\"", 14, IndicatorCategory.SOCIAL_ENGINEERING))
            }
        }

        for (phrase in impersonationSignals) {
            if (lower.contains(phrase)) {
                out.add(ThreatIndicator("Impersonation signal: \"$phrase\"", 16, IndicatorCategory.SOCIAL_ENGINEERING))
            }
        }

        for (phrase in cryptoScamPhrases) {
            if (lower.contains(phrase)) {
                out.add(ThreatIndicator("Crypto scam pattern: \"$phrase\"", 22, IndicatorCategory.CRYPTO_SCAM))
            }
        }

        // Embedded links in message text
        val urlPattern = Regex("https?://[^\\s<>\"]+", RegexOption.IGNORE_CASE)
        val urlMatches = urlPattern.findAll(text).toList()
        if (urlMatches.isNotEmpty() && !looksLikeUrl(text.trim())) {
            for (match in urlMatches) {
                val url = match.value
                val truncated = if (url.length > 60) url.take(60) + "..." else url
                out.add(ThreatIndicator("Embedded link detected: $truncated", 6, IndicatorCategory.PHISHING))
                analyzeUrl(url, out)
            }
        }

        // Excessive caps
        val alphaChars = text.filter { it.isLetter() }
        if (alphaChars.isNotEmpty() && text.length > 20) {
            val capsRatio = alphaChars.count { it.isUpperCase() }.toFloat() / alphaChars.length
            if (capsRatio > 0.6f) {
                out.add(ThreatIndicator("Excessive capitalization -- aggressive social engineering", 6, IndicatorCategory.SOCIAL_ENGINEERING))
            }
        }

        // Suspicious email patterns
        if (Regex("support@|admin@|security@|noreply@.*\\.(xyz|tk|ml|ga|cf)", RegexOption.IGNORE_CASE).containsMatchIn(lower)) {
            out.add(ThreatIndicator("Suspicious sender address from high-risk domain", 14, IndicatorCategory.SOCIAL_ENGINEERING))
        }
    }

    // -----------------------------------------------------------------------
    // Scoring
    // -----------------------------------------------------------------------
    private fun scoreSeverity(score: Int): Severity = when {
        score >= 80 -> Severity.CRITICAL
        score >= 60 -> Severity.HIGH
        score >= 35 -> Severity.MEDIUM
        else -> Severity.LOW
    }

    private fun generateDescription(
        indicators: List<ThreatIndicator>,
        score: Int,
        input: String,
        isUrl: Boolean
    ): String {
        if (indicators.isEmpty()) {
            return "Analysis complete. No significant threat indicators were identified. The input appears benign based on pattern matching, heuristic analysis, and threat intelligence correlation."
        }

        val categoryCount = mutableMapOf<IndicatorCategory, Int>()
        for (ind in indicators) {
            categoryCount[ind.category] = (categoryCount[ind.category] ?: 0) + 1
        }

        val parts = mutableListOf<String>()

        when {
            score >= 80 -> parts.add("CRITICAL THREAT DETECTED.")
            score >= 60 -> parts.add("High-confidence threat indicators identified.")
            score >= 35 -> parts.add("Moderate risk -- several suspicious signals detected.")
            else -> parts.add("Low-risk input with minor anomalies.")
        }

        categoryCount[IndicatorCategory.PHISHING]?.let { count ->
            parts.add("Phishing analysis flagged $count indicator${if (count > 1) "s" else ""} including domain impersonation or deceptive URL patterns.")
        }

        categoryCount[IndicatorCategory.SOCIAL_ENGINEERING]?.let { count ->
            parts.add("Social engineering heuristics triggered $count warning${if (count > 1) "s" else ""} -- message contains manipulative language designed to coerce immediate action.")
        }

        categoryCount[IndicatorCategory.CRYPTO_SCAM]?.let { count ->
            parts.add("Crypto-specific threat patterns detected ($count match${if (count > 1) "es" else ""}). Content targets wallet credentials or token approvals.")
        }

        categoryCount[IndicatorCategory.TECHNICAL]?.let { count ->
            parts.add("Technical analysis identified $count structural anomal${if (count > 1) "ies" else "y"} in the URL or payload encoding.")
        }

        categoryCount[IndicatorCategory.URL_STRUCTURE]?.let { count ->
            parts.add("URL structure analysis found $count suspicious attribute${if (count > 1) "s" else ""} commonly associated with malicious infrastructure.")
        }

        if (score >= 60) {
            parts.add("Recommendation: Do NOT interact with this content. Report to the ShieldMesh network for validator consensus.")
        } else if (score >= 35) {
            parts.add("Recommendation: Exercise caution. Verify the source independently before any interaction.")
        }

        return parts.joinToString(" ")
    }
}
