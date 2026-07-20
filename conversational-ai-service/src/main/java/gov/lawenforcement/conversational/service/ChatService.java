package gov.lawenforcement.conversational.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private final JdbcTemplate jdbc;

    public ChatService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> processMessage(String sessionId, String message) {
        try {
        String lower = message.toLowerCase().trim();
        String intent;
        try {
            intent = detectIntent(lower);
        } catch (Exception e) {
            intent = "fallback";
        }
        Map<String, String> slots = extractSlots(lower);

        String response;
        String responseType;
        Object data = null;

            switch (intent) {
                case "case_list":
                    response = handleCaseList(lower, slots);
                    responseType = "text";
                    data = getCaseListData(slots);
                    break;
                case "case_detail":
                    response = handleCaseDetail(slots);
                    responseType = "detail";
                    data = getCaseDetailData(slots);
                    break;
            case "stats":
                response = handleStats();
                responseType = "stats";
                data = getStatsData();
                break;
            case "district_stats":
                response = handleDistrictStats();
                responseType = "chart";
                data = getDistrictStatsData();
                break;
            case "crime_head_stats":
                response = handleCrimeHeadStats();
                responseType = "chart";
                data = getCrimeHeadStatsData();
                break;
            case "financial":
                response = handleFinancial(slots);
                responseType = "table";
                data = getFinancialData(slots);
                break;
            case "report":
                response = handleReport(slots);
                responseType = "report";
                data = Map.of("caseId", slots.getOrDefault("caseId", "1"));
                break;
            case "crime_number_search":
                response = handleCrimeNumberSearch(slots);
                responseType = "detail";
                data = getCrimeNumberSearchData(slots);
                break;
            case "accused_list":
                response = handleAccusedList();
                responseType = "table";
                data = getAccusedData();
                break;
            case "victim_list":
                response = handleVictimList();
                responseType = "table";
                data = getVictimData();
                break;
            case "complainant_list":
                response = handleComplainantList();
                responseType = "table";
                data = getComplainantData();
                break;
            case "chargesheet":
                response = handleChargesheetCases();
                responseType = "text";
                data = getChargesheetData();
                break;
            case "arrest_stats":
                response = handleArrestStats();
                responseType = "stats";
                data = getArrestStatsData();
                break;
            case "search_person":
                response = handlePersonSearch(slots);
                responseType = "table";
                data = getPersonSearchData(slots);
                break;
            case "help":
                response = getHelpText();
                responseType = "text";
                break;
            case "greeting":
                response = getGreeting();
                responseType = "text";
                break;
            case "flagged":
                response = handleFlaggedTransactions();
                responseType = "table";
                data = getFlaggedData();
                break;
            default:
                response = handleFallback(message);
                responseType = "text";
                break;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("response", response);
        result.put("intent", intent);
        result.put("responseType", responseType);
        if (data != null) {
            result.put("data", data);
        }
        result.put("sessionId", sessionId);
        result.put("timestamp", java.time.Instant.now().toString());
        return result;
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("response", "Sorry, an error occurred: " + e.getMessage());
            err.put("intent", "error");
            err.put("responseType", "text");
            err.put("sessionId", sessionId);
            err.put("timestamp", java.time.Instant.now().toString());
            return err;
        }
    }

    private String detectIntent(String msg) {
        String m = msg.toLowerCase();
        // Greeting
        if (m.matches(".*(hi|hello|hey|namaskara|namaste|good (morning|afternoon|evening)|ನಮಸ್ಕಾರ|ಹಾಯ್|ಹಲೋ).*")) return "greeting";
        // Help
        if (m.matches(".*(help|what can you|commands|features|options|guide|ಸಹಾಯ|ಹೆಲ್ಪ್|ಏನು ಮಾಡಬಹುದು|ಮಾರ್ಗದರ್ಶನ).*")) return "help";
        // Report
        if (m.matches(".*(report|fir|download|generate|pdf|ವರದಿ|ಎಫ್‌ಐಆರ್|ಡೌನ್‌ಲೋಡ್).*")) return "report";
        // Flagged
        if (m.matches(".*(flagged|suspicious|risk|alert).*transaction.*") || m.matches(".*(transaction|financial|money|fund).*flagged.*") || m.matches(".*(ಗುರುತಿಸಲಾದ|ಸಂಶಯಾಸ್ಪದ).*ವ್ಯವಹಾರ.*") || m.matches(".*(ವ್ಯವಹಾರ|ಹಣ|ಹಣಕಾಸು).*ಗುರುತಿಸಲಾದ.*")) return "flagged";
        // Financial
        if (m.matches(".*(transaction|financial|money|fund|payment|ವ್ಯವಹಾರ|ಹಣ|ಹಣಕಾಸು|ಪಾವತಿ|ವಹಿವಾಟು).*")) return "financial";
        // Accused
        if (m.matches(".*(accused|suspect|criminal|offender|ಅಪರಾಧಿ|ಶಂಕಿತ|ಖಳನಾಯಕ).*")) return "accused_list";
        // Victim
        if (m.matches(".*(victim|victims|ಸಂತ್ರಸ್ತ|ಬಾಧಿತ).*")) return "victim_list";
        // Complainant
        if (m.matches(".*(complainant|plaintiff|reporter|ದೂರುದಾರ|ಪರಿಹಾರ).*")) return "complainant_list";
        // Arrest
        if (m.matches(".*(arrest|arrested|surrender|ಬಂಧನ|ಸರೆಂಡರ್|ಬಂಧಿಸಲಾಗಿದೆ).*")) return "arrest_stats";
        // Chargesheet
        if (m.matches(".*(chargesheet|charge.?sheet|ಚಾರ್ಜ್.?ಶೀಟ್|ಚಾರ್ಜ್‌ಶೀಟ್).*")) return "chargesheet";
        // Crime head
        if (m.matches(".*(crime head|crime type|crime group|category distribution|pie chart|ಅಪರಾಧ ವರ್ಗ|ಅಪರಾಧ ಬಗೆ|ವರ್ಗೀಕರಣ).*")) return "crime_head_stats";
        // District
        if (m.matches(".*(district|districts|by district|location|region|area).*distribution.*") || m.matches(".*(which district|district.*case|cases.*district).*") || m.matches(".*(ಜಿಲ್ಲೆ|ಜಿಲ್ಲೆಗಳು|ಪ್ರದೇಶ).*(ವಿತರಣೆ|ಹಂಚಿಕೆ|ಪಟ್ಟಿ).*") || m.matches(".*(ಯಾವ.*ಜಿಲ್ಲೆ|ಜಿಲ್ಲೆ.*ಪ್ರಕರಣ).*")) return "district_stats";
        // Stats
        if (m.matches(".*(statistic|stats|summary|overview|dashboard|count|total|number of|ಅಂಕೆ|ಸಂಖ್ಯಾ|ಒಟ್ಟು|ಸಾರಾಂಶ|ಡ್ಯಾಶ್‌ಬೋರ್ಡ್).*")) return "stats";
        // Search person
        if (m.matches(".*(person|search|find|lookup|ವ್ಯಕ್ತಿ|ಹುಡುಕಿ|ಶೋಧಿಸಿ|ಪತ್ತೆ).*")) return "search_person";
        // Crime number search (pure number)
        if (m.matches("^\\d{10,20}$")) return "crime_number_search";
        // Crime number embedded
        if (m.matches(".*(\\d{10,20}).*")) return "crime_number_search";
        // Case detail
        if (m.matches(".*(case \\d+|case#\\d+|#\\d+|case detail|case info|view case|ಪ್ರಕರಣ.*ವಿವರ|ಪ್ರಕರಣ.*ನೋಡಿ|ಪ್ರಕರಣ.*ಮಾಹಿತಿ).*")) return "case_detail";
        // Case list
        if (m.matches(".*(list.*case|all case|recent case|show.*case|case.*list|open.*case|under.*investigation|cases|case.*all|case.*filter).*")) return "case_list";
        if (m.matches(".*(ಎಲ್ಲಾ.*ಪ್ರಕರಣ|ಪ್ರಕರಣ.*ಪಟ್ಟಿ|ತೋರಿಸಿ.*ಪ್ರಕರಣ|ಪ್ರಕರಣ.*ತೋರಿಸಿ|ತೆರೆದ.*ಪ್ರಕರಣ|ಪ್ರಕರಣ.*ಎಲ್ಲಾ|ಪ್ರಕರಣ).*")) return "case_list";
        if (m.matches(".*(ಪ್ರಕರಣ|case|cases).*")) return "case_list";
        return "fallback";
    }

    private Map<String, String> extractSlots(String msg) {
        Map<String, String> slots = new HashMap<>();
        String m = msg.toLowerCase();
        Matcher idMatcher = Pattern.compile("(?:case|id|#)\\s*(\\d+)").matcher(msg);
        if (idMatcher.find()) slots.put("caseId", idMatcher.group(1));

        Matcher crimeNoMatcher = Pattern.compile("(\\d{10,20})").matcher(msg);
        if (crimeNoMatcher.find()) slots.put("crimeNo", crimeNoMatcher.group(1));

        String[] districts = {"bengaluru urban", "bengaluru rural", "mysuru", "kalaburgi", "hubli-dharwad", "mangaluru", "belagavi", "ballari", "davangere", "shimoga",
            "ಬೆಂಗಳೂರು", "ಮೈಸೂರು", "ಕಲಬುರಗಿ", "ಹುಬ್ಬಳ್ಳಿ", "ಮಂಗಳೂರು", "ಬೆಳಗಾವಿ", "ಬಳ್ಳಾರಿ", "ದಾವಣಗೆರೆ", "ಶಿವಮೊಗ್ಗ"};
        for (String d : districts) {
            if (m.contains(d)) {
                String normalized = d;
                if (d.equals("ಬೆಂಗಳೂರು")) normalized = "bengaluru";
                else if (d.equals("ಮೈಸೂರು")) normalized = "mysuru";
                else if (d.equals("ಕಲಬುರಗಿ")) normalized = "kalaburgi";
                else if (d.equals("ಹುಬ್ಬಳ್ಳಿ")) normalized = "hubli-dharwad";
                else if (d.equals("ಮಂಗಳೂರು")) normalized = "mangaluru";
                else if (d.equals("ಬೆಳಗಾವಿ")) normalized = "belagavi";
                else if (d.equals("ಬಳ್ಳಾರಿ")) normalized = "ballari";
                else if (d.equals("ದಾವಣಗೆರೆ")) normalized = "davangere";
                else if (d.equals("ಶಿವಮೊಗ್ಗ")) normalized = "shimoga";
                slots.put("district", normalized);
                break;
            }
        }
        if (m.contains("bengaluru") || m.contains("ಬೆಂಗಳೂರು")) slots.put("district", "bengaluru");

        Matcher nameMatcher = Pattern.compile("(?:search|find|lookup|person|for|accused|victim|complainant|named|called)\\s+(?:for\\s+)?([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)", Pattern.CASE_INSENSITIVE).matcher(msg);
        if (nameMatcher.find()) {
            String extracted = nameMatcher.group(1).trim();
            if (!extracted.isEmpty()) {
                slots.put("name", extracted);
            }
        }

        if (m.contains("open") || m.contains("registered") || m.contains("new") || m.contains("ತೆರೆದ") || m.contains("ನೋಂದಾಯಿಸಲಾಗಿದೆ") || m.contains("ಹೊಸ")) slots.put("status", "open");
        if (m.contains("under") && m.contains("investigation") || m.contains("ವಿಚಾರಣೆ") || m.contains("ತನಿಖೆ")) slots.put("status", "investigation");
        if (m.contains("charge") && m.contains("sheet") || m.contains("ಚಾರ್ಜ್") || m.contains("ಶೀಟ್")) slots.put("status", "chargesheet");
        if (m.contains("closed") || m.contains("resolved") || m.contains("ಮುಚ್ಚಲ್ಪಟ್ಟಿದೆ") || m.contains("ಪೂರ್ಣ")) slots.put("status", "closed");

        return slots;
    }

    private String handleCaseList(String msg, Map<String, String> slots) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Case Registry**\n\n");

        List<Map<String, Object>> cases = queryCases(slots);
        if (cases.isEmpty()) {
            sb.append("No cases found matching your criteria.");
            return sb.toString();
        }

        sb.append("Found **").append(cases.size()).append("** cases");
        if (slots.containsKey("district")) sb.append(" in **").append(capitalize(slots.get("district"))).append("**");
        if (slots.containsKey("status")) sb.append(" with status **").append(slots.get("status")).append("**");
        sb.append(":\n\n");

        for (Map<String, Object> c : cases) {
            sb.append("**[#").append(c.get("id")).append("](/incidents/").append(c.get("id")).append(")**");
            sb.append(" `").append(c.get("crime_no")).append("`\n");
            sb.append("Status: ").append(c.get("status")).append(" | ").append(c.get("category")).append(" | ").append(c.get("date")).append("\n");
            if (c.get("brief_facts") != null) {
                String facts = c.get("brief_facts").toString();
                if (facts.length() > 100) facts = facts.substring(0, 100) + "...";
                sb.append("> ").append(facts).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String handleCaseDetail(Map<String, String> slots) {
        String caseId = slots.get("caseId");
        if (caseId == null) return "Please specify a case ID. For example: _Show case 1_";

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT cm.*, d.district_name, csm.case_status_name, ch.crime_group_name, co.court_name " +
                "FROM case_master cm " +
                "LEFT JOIN unit u ON cm.police_station_id = u.unit_id " +
                "LEFT JOIN district d ON u.district_id = d.district_id " +
                "LEFT JOIN case_status_master csm ON cm.case_status_id = csm.case_status_id " +
                "LEFT JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id " +
                "LEFT JOIN court co ON cm.court_id = co.court_id " +
                "WHERE cm.case_master_id = ?", Integer.parseInt(caseId));

            if (rows.isEmpty()) return "Case #" + caseId + " not found.";

            Map<String, Object> c = rows.get(0);
            StringBuilder sb = new StringBuilder();
            sb.append("**Case Detail - #").append(caseId).append("**\n\n");
            sb.append("| Field | Value |\n|---|---|\n");
            sb.append("| Crime No | `").append(nvl(c, "crime_no")).append("` |\n");
            sb.append("| Case No | ").append(nvl(c, "case_no")).append(" |\n");
            sb.append("| Status | **").append(nvl(c, "case_status_name", "Unknown")).append("** |\n");
            sb.append("| Date Registered | ").append(nvl(c, "crime_registered_date")).append(" |\n");
            sb.append("| District | ").append(nvl(c, "district_name", "Unknown")).append(" |\n");
            sb.append("| Crime Head | ").append(nvl(c, "crime_group_name", "Unknown")).append(" |\n");
            sb.append("| Court | ").append(nvl(c, "court_name", "Unknown")).append(" |\n");
            if (c.get("brief_facts") != null) {
                sb.append("| Brief Facts | ").append(c.get("brief_facts")).append(" |\n");
            }

            int caseIdInt = Integer.parseInt(caseId);
            List<Map<String, Object>> complainants = jdbc.queryForList("SELECT * FROM complainant_details WHERE case_master_id = ?", caseIdInt);
            List<Map<String, Object>> victims = jdbc.queryForList("SELECT * FROM victim WHERE case_master_id = ?", caseIdInt);
            List<Map<String, Object>> accused = jdbc.queryForList("SELECT * FROM accused WHERE case_master_id = ?", caseIdInt);
            List<Map<String, Object>> acts = jdbc.queryForList("SELECT * FROM act_section_association WHERE case_master_id = ?", caseIdInt);

            if (!complainants.isEmpty()) {
                sb.append("\n**Complainants:**\n");
                for (Map<String, Object> p : complainants) sb.append("- ").append(p.get("complainant_name")).append(" (age ").append(p.get("age_year")).append(")\n");
            }
            if (!victims.isEmpty()) {
                sb.append("\n**Victims:**\n");
                for (Map<String, Object> v : victims) sb.append("- ").append(v.get("victim_name")).append(" (age ").append(v.get("age_year")).append(")\n");
            }
            if (!accused.isEmpty()) {
                sb.append("\n**Accused:**\n");
                for (Map<String, Object> a : accused) sb.append("- ").append(a.get("accused_name")).append(" (age ").append(a.get("age_year")).append(")\n");
            }
            if (!acts.isEmpty()) {
                sb.append("\n**Act Sections:** ");
                List<String> sections = new ArrayList<>();
                for (Map<String, Object> a : acts) sections.add(a.get("act_code") + " " + a.get("section_code"));
                sb.append(String.join(", ", sections)).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return "Error fetching case details: " + e.getMessage();
        }
    }

    private String handleStats() {
        Map<String, Object> stats = getStatsData();
        StringBuilder sb = new StringBuilder();
        sb.append("**Crime Analytics Overview**\n\n");
        sb.append("| Metric | Count |\n|---|---|\n");
        sb.append("| Total Cases | **").append(stats.get("totalCases")).append("** |\n");
        sb.append("| Open Cases | ").append(stats.get("openCases")).append(" |\n");
        sb.append("| Under Investigation | ").append(stats.get("underInvestigation")).append(" |\n");
        sb.append("| Charge Sheeted | ").append(stats.get("chargeSheeted")).append(" |\n");
        sb.append("| Closed | ").append(stats.get("closed")).append(" |\n");
        sb.append("\n**Involvement Summary:**\n");
        sb.append("| Type | Count |\n|---|---|\n");
        sb.append("| Complainants | ").append(stats.get("totalComplainants")).append(" |\n");
        sb.append("| Victims | ").append(stats.get("totalVictims")).append(" |\n");
        sb.append("| Accused | ").append(stats.get("totalAccused")).append(" |\n");
        sb.append("| Arrests | ").append(stats.get("totalArrests")).append(" |\n");
        sb.append("| Chargesheets Filed | ").append(stats.get("totalChargesheets")).append(" |\n");
        return sb.toString();
    }

    private String handleDistrictStats() {
        List<Map<String, Object>> data = getDistrictStatsData();
        StringBuilder sb = new StringBuilder();
        sb.append("**Cases by District**\n\n");
        sb.append("| District | Cases |\n|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| ").append(row.get("district")).append(" | **").append(row.get("count")).append("** |\n");
        }
        return sb.toString();
    }

    private String handleCrimeHeadStats() {
        List<Map<String, Object>> data = getCrimeHeadStatsData();
        StringBuilder sb = new StringBuilder();
        sb.append("**Crime Head Distribution**\n\n");
        sb.append("| Crime Head | Cases |\n|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| ").append(row.get("crime_head")).append(" | **").append(row.get("count")).append("** |\n");
        }
        return sb.toString();
    }

    private String handleCrimeNumberSearch(Map<String, String> slots) {
        String crimeNo = slots.get("crimeNo");
        if (crimeNo == null) return "Please provide a crime number to search.";

        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT cm.*, d.district_name, csm.case_status_name, ch.crime_group_name, co.court_name " +
                "FROM case_master cm " +
                "LEFT JOIN unit u ON cm.police_station_id = u.unit_id " +
                "LEFT JOIN district d ON u.district_id = d.district_id " +
                "LEFT JOIN case_status_master csm ON cm.case_status_id = csm.case_status_id " +
                "LEFT JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id " +
                "LEFT JOIN court co ON cm.court_id = co.court_id " +
                "WHERE cm.crime_no = ?", crimeNo);

            if (rows.isEmpty()) return "No case found with crime number `" + crimeNo + "`.";

            Map<String, Object> c = rows.get(0);
            Object caseId = c.get("case_master_id");
            StringBuilder sb = new StringBuilder();
            sb.append("**Case Found**\n\n");
            sb.append("| Field | Value |\n|---|---|\n");
            sb.append("| Crime No | `").append(nvl(c, "crime_no")).append("` |\n");
            sb.append("| Case No | ").append(nvl(c, "case_no")).append(" |\n");
            sb.append("| Status | **").append(nvl(c, "case_status_name", "Unknown")).append("** |\n");
            sb.append("| Date Registered | ").append(nvl(c, "crime_registered_date")).append(" |\n");
            sb.append("| District | ").append(nvl(c, "district_name", "Unknown")).append(" |\n");
            sb.append("| Crime Head | ").append(nvl(c, "crime_group_name", "Unknown")).append(" |\n");
            sb.append("| Court | ").append(nvl(c, "court_name", "Unknown")).append(" |\n");
            if (c.get("brief_facts") != null) {
                sb.append("| Brief Facts | ").append(c.get("brief_facts")).append(" |\n");
            }

            int cid = ((Number) caseId).intValue();
            List<Map<String, Object>> accused = jdbc.queryForList("SELECT * FROM accused WHERE case_master_id = ?", cid);
            if (!accused.isEmpty()) {
                sb.append("\n**Accused:**\n");
                for (Map<String, Object> a : accused) sb.append("- ").append(a.get("accused_name")).append(" (age ").append(a.get("age_year")).append(")\n");
            }

            sb.append("\n[View Full Case Details ->](/incidents/").append(cid).append(")");
            return sb.toString();
        } catch (Exception e) {
            return "Error searching for crime number: " + e.getMessage();
        }
    }

    private Map<String, Object> getCrimeNumberSearchData(Map<String, String> slots) {
        String crimeNo = slots.get("crimeNo");
        if (crimeNo == null) return Map.of();
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM case_master WHERE crime_no = ?", crimeNo);
            return rows.isEmpty() ? Map.of() : rows.get(0);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String handleFinancial(Map<String, String> slots) {
        String caseId = slots.getOrDefault("caseId", null);
        StringBuilder sb = new StringBuilder();

        if (caseId != null) {
            sb.append("**Financial Transactions for Case #").append(caseId).append("**\n\n");
            List<Map<String, Object>> txns = jdbc.queryForList(
                "SELECT * FROM financial_transactions WHERE related_case_id = ?", Integer.parseInt(caseId));
            if (txns.isEmpty()) {
                sb.append("No financial transactions linked to this case.");
            } else {
                sb.append("| Ref | Type | Amount | Flagged |\n|---|---|---|---|\n");
                for (Map<String, Object> t : txns) {
                    sb.append("| ").append(t.get("transaction_ref")).append(" | ").append(t.get("transaction_type"));
                    sb.append(" | ₹").append(t.get("amount"));
                    sb.append(" | ").append(Boolean.TRUE.equals(t.get("is_flagged")) ? "**YES**" : "No").append(" |\n");
                }
            }
        } else {
            List<Map<String, Object>> txns = jdbc.queryForList(
                "SELECT ft.*, cm.crime_no FROM financial_transactions ft LEFT JOIN case_master cm ON ft.related_case_id = cm.case_master_id ORDER BY ft.transaction_date DESC LIMIT 15");
            sb.append("**Recent Financial Transactions**\n\n");
            sb.append("| Ref | Case | Type | Amount | Flagged |\n|---|---|---|---|---|\n");
            for (Map<String, Object> t : txns) {
                sb.append("| ").append(t.get("transaction_ref")).append(" | ").append(nvl(t, "crimeNo", "N/A"));
                sb.append(" | ").append(t.get("transaction_type")).append(" | ₹").append(t.get("amount"));
                sb.append(" | ").append(Boolean.TRUE.equals(t.get("is_flagged")) ? "**YES**" : "No").append(" |\n");
            }
        }
        return sb.toString();
    }

    private String handleReport(Map<String, String> slots) {
        String caseId = slots.getOrDefault("caseId", "1");
        return "**FIR Report Generated**\n\n" +
               "FIR report for Case #" + caseId + " is ready for download.\n\n" +
               "Use the **Download FIR Report** button on the case detail page, or click below.";
    }

    private String handleAccusedList() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT a.accused_name, a.age_year, a.person_id, cm.crime_no, a.case_master_id " +
            "FROM accused a JOIN case_master cm ON a.case_master_id = cm.case_master_id ORDER BY a.accused_master_id");

        StringBuilder sb = new StringBuilder();
        sb.append("**All Accused Persons** (").append(data.size()).append(" total)\n\n");
        sb.append("| Name | Age | Person ID | Crime No | Case |\n|---|---|---|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| ").append(row.get("accused_name")).append(" | ").append(nvl(row, "age_year", "N/A"));
            sb.append(" | ").append(nvl(row, "personId", "N/A")).append(" | `").append(row.get("crime_no")).append("`");
            sb.append(" | #").append(row.get("case_master_id")).append(" |\n");
        }
        return sb.toString();
    }

    private String handleVictimList() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT v.victim_name, v.age_year, cm.crime_no, v.case_master_id " +
            "FROM victim v JOIN case_master cm ON v.case_master_id = cm.case_master_id ORDER BY v.victim_master_id");

        StringBuilder sb = new StringBuilder();
        sb.append("**All Victims** (").append(data.size()).append(" total)\n\n");
        sb.append("| Name | Age | Crime No | Case |\n|---|---|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| ").append(row.get("victim_name")).append(" | ").append(nvl(row, "age_year", "N/A"));
            sb.append(" | `").append(row.get("crime_no")).append("` | #").append(row.get("case_master_id")).append(" |\n");
        }
        return sb.toString();
    }

    private String handleComplainantList() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT cd.complainant_name, cd.age_year, cm.crime_no, cd.case_master_id " +
            "FROM complainant_details cd JOIN case_master cm ON cd.case_master_id = cm.case_master_id ORDER BY cd.complainant_id");

        StringBuilder sb = new StringBuilder();
        sb.append("**All Complainants** (").append(data.size()).append(" total)\n\n");
        sb.append("| Name | Age | Crime No | Case |\n|---|---|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| ").append(row.get("complainant_name")).append(" | ").append(nvl(row, "age_year", "N/A"));
            sb.append(" | `").append(row.get("crime_no")).append("` | #").append(row.get("case_master_id")).append(" |\n");
        }
        return sb.toString();
    }

    private String handleChargesheetCases() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT cm.case_master_id, cm.crime_no, cm.crime_registered_date, csm.case_status_name, " +
            "ch.crime_group_name, cs.cs_date, cs.cs_type " +
            "FROM chargesheet_details cs " +
            "JOIN case_master cm ON cs.case_master_id = cm.case_master_id " +
            "LEFT JOIN case_status_master csm ON cm.case_status_id = csm.case_status_id " +
            "LEFT JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id " +
            "ORDER BY cs.cs_date DESC");

        StringBuilder sb = new StringBuilder();
        sb.append("**Cases with Chargesheets** (").append(data.size()).append(" total)\n\n");
        sb.append("| Case | Crime No | CS Date | Type | Status |\n|---|---|---|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| #").append(row.get("case_master_id")).append(" | `").append(row.get("crime_no")).append("`");
            sb.append(" | ").append(nvl(row, "cs_date", "N/A")).append(" | ").append(nvl(row, "cs_type", "N/A"));
            sb.append(" | ").append(nvl(row, "case_status_name", "N/A")).append(" |\n");
        }
        return sb.toString();
    }

    private String handleArrestStats() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT cm.case_master_id, cm.crime_no, a.arrest_surrender_date, a.arrest_surrender_type_id " +
            "FROM arrest_surrender a JOIN case_master cm ON a.case_master_id = cm.case_master_id " +
            "ORDER BY a.arrest_surrender_date DESC");

        StringBuilder sb = new StringBuilder();
        sb.append("**Arrest Records** (").append(data.size()).append(" total)\n\n");
        sb.append("| Case | Crime No | Arrest Date | Type ID |\n|---|---|---|---|\n");
        for (Map<String, Object> row : data) {
            sb.append("| #").append(row.get("case_master_id")).append(" | `").append(row.get("crime_no")).append("`");
            sb.append(" | ").append(nvl(row, "arrest_surrender_date", "N/A"));
            sb.append(" | ").append(nvl(row, "arrest_surrender_type_id", "N/A")).append(" |\n");
        }
        return sb.toString();
    }

    private String handlePersonSearch(Map<String, String> slots) {
        String name = slots.get("name");
        if (name == null) return "Please specify a person name. For example: _Search for person Ravi_";

        StringBuilder sb = new StringBuilder();
        sb.append("**Search Results for \"").append(name).append("\"**\n\n");

        List<Map<String, Object>> accused = jdbc.queryForList(
            "SELECT a.*, cm.crime_no FROM accused a JOIN case_master cm ON a.case_master_id = cm.case_master_id " +
            "WHERE LOWER(a.accused_name) LIKE ?", "%" + name.toLowerCase() + "%");
        if (!accused.isEmpty()) {
            sb.append("**As Accused:**\n");
            for (Map<String, Object> a : accused) {
                sb.append("- ").append(a.get("accused_name")).append(" in case `").append(a.get("crime_no")).append("`\n");
            }
        }

        List<Map<String, Object>> victims = jdbc.queryForList(
            "SELECT v.*, cm.crime_no FROM victim v JOIN case_master cm ON v.case_master_id = cm.case_master_id " +
            "WHERE LOWER(v.victim_name) LIKE ?", "%" + name.toLowerCase() + "%");
        if (!victims.isEmpty()) {
            sb.append("**As Victim:**\n");
            for (Map<String, Object> v : victims) {
                sb.append("- ").append(v.get("victim_name")).append(" in case `").append(v.get("crime_no")).append("`\n");
            }
        }

        if (accused.isEmpty() && victims.isEmpty()) {
            sb.append("No persons found matching \"").append(name).append("\".");
        }
        return sb.toString();
    }

    private String handleFlaggedTransactions() {
        List<Map<String, Object>> data = jdbc.queryForList(
            "SELECT ft.*, cm.crime_no FROM financial_transactions ft " +
            "LEFT JOIN case_master cm ON ft.related_case_id = cm.case_master_id " +
            "WHERE ft.is_flagged = true ORDER BY ft.risk_score DESC");

        StringBuilder sb = new StringBuilder();
        sb.append("**Flagged Transactions** (").append(data.size()).append(" total)\n\n");
        if (data.isEmpty()) {
            sb.append("No flagged transactions found.");
        } else {
            sb.append("| Ref | Case | Type | Amount | Risk | Reason |\n|---|---|---|---|---|---|\n");
            for (Map<String, Object> t : data) {
                sb.append("| ").append(t.get("transaction_ref")).append(" | ").append(nvl(t, "crimeNo", "N/A"));
                sb.append(" | ").append(t.get("transaction_type")).append(" | ₹").append(t.get("amount"));
                sb.append(" | ").append(nvl(t, "riskScore", "N/A")).append(" | ").append(nvl(t, "flagReason", "N/A")).append(" |\n");
            }
        }
        return sb.toString();
    }

    private String getGreeting() {
        return "Welcome to **KSP Crime Analytics AI Assistant**\n\n" +
               "I'm your intelligent crime analytics assistant for the Karnataka State Police.\n" +
               "ನಾನು ಕರ್ನಾಟಕ ರಾಜ್ಯ ಪೊಲೀಸ್‌ನ ಕ್ರೈಮ್ ಅನಾಲಿಟಿಕ್ಸ್ ಸಹಾಯಕ.\n\n" +
               "I can help you with:\n\n" +
               "- **Case Management** / **ಪ್ರಕರಣ ನಿರ್ವಹಣೆ** - Search, filter, and view case details\n" +
               "- **Crime Statistics** / **ಅಪರಾಧ ಅಂಕೆ** - Overview, district-wise, and crime-type analysis\n" +
               "- **Person Lookup** / **ವ್ಯಕ್ತಿ ಹುಡುಕಾಟ** - Find accused, victims, and complainants\n" +
               "- **Financial Analysis** / **ಹಣಕಾಸು ವಿಶ್ಲೇಷಣೆ** - Track and flag suspicious transactions\n" +
               "- **Report Generation** / **ವರದಿ ತಯಾರಿಕೆ** - Generate FIR and incident reports\n\n" +
               "_Try: \"Show me all open cases\" | \"ತೆರೆದ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ\" | \"Crime statistics\" | \"ಅಂಕೆ ಸಂಖ್ಯಾ\"_";
    }

    private String getHelpText() {
        return "**Available Commands / ಲಭ್ಯವಿರುವ ಆದೇಶಗಳು**\n\n" +
               "**Cases / ಪ್ರಕರಣಗಳು:**\n" +
               "- _Show me all cases_ / _ಎಲ್ಲಾ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ_ - List all cases\n" +
               "- _Show open cases_ / _ತೆರೆದ ಪ್ರಕರಣಗಳು_ - Filter by status\n" +
               "- _Cases in Bengaluru_ / _ಬೆಂಗಳೂರಿನ ಪ್ರಕರಣಗಳು_ - Filter by district\n" +
               "- _Show case 3_ / _ಪ್ರಕರಣ 3 ತೋರಿಸಿ_ - View case details\n" +
               "- _104430006202600001_ - Search by crime number\n\n" +
               "**Analytics / ಅಂಕೆಗಳು:**\n" +
               "- _Crime statistics_ / _ಅಂಕೆ ಸಂಖ್ಯಾ_ - Overall stats\n" +
               "- _District distribution_ / _ಜಿಲ್ಲೆ ವಿತರಣೆ_ - Cases by district\n" +
               "- _Crime head distribution_ / _ಅಪರಾಧ ವರ್ಗ ವಿತರಣೆ_ - Cases by crime type\n\n" +
               "**Persons / ವ್ಯಕ್ತಿಗಳು:**\n" +
               "- _List all accused_ / _ಎಲ್ಲಾ ಅಪರಾಧಿಗಳು_ - All accused\n" +
               "- _List all victims_ / _ಎಲ್ಲಾ ಸಂತ್ರಸ್ತರು_ - All victims\n" +
               "- _Search for Ravi_ / _ರವಿ ಹುಡುಕಿ_ - Find by name\n\n" +
               "**Financial / ಹಣಕಾಸು:**\n" +
               "- _Show transactions_ / _ವ್ಯವಹಾರಗಳು_ - Financial transactions\n" +
               "- _Transactions for case 3_ - Case-specific\n" +
               "- _Flagged transactions_ / _ಗುರುತಿಸಲಾದ ವ್ಯವಹಾರ_ - Suspicious ones\n\n" +
               "**Reports / ವರದಿಗಳು:**\n" +
               "- _Generate FIR report for case 1_ - Generate FIR PDF";
    }

    private String handleFallback(String message) {
        return "I'm not sure I understand that. / ನಾನು ಅರ್ಥಮಾಡಿಕೊಳ್ಳುವುದಿಲ್ಲ.\n\n" +
               "Here are some things I can help with:\n\n" +
               "- _Show me all cases_ / _ಎಲ್ಲಾ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ_\n" +
               "- _Crime statistics_ / _ಅಂಕೆ ಸಂಖ್ಯಾ_\n" +
               "- _Generate FIR report for case 1_\n" +
               "- _List all accused_ / _ಎಲ್ಲಾ ಅಪರಾಧಿಗಳು_\n" +
               "- _Show transactions_ / _ವ್ಯವಹಾರಗಳು_\n" +
               "- _Search for Ravi_ / _ರವಿ ಹುಡುಕಿ_\n\n" +
               "Type **help** / **ಸಹಾಯ** to see all available commands.";
    }

    private List<Map<String, Object>> queryCases(Map<String, String> slots) {
        StringBuilder sql = new StringBuilder(
            "SELECT cm.case_master_id as id, cm.crime_no as crime_no, cm.crime_registered_date as date, cm.brief_facts as brief_facts, " +
            "csm.case_status_name as status, ch.crime_group_name as category, d.district_name as district " +
            "FROM case_master cm " +
            "LEFT JOIN case_status_master csm ON cm.case_status_id = csm.case_status_id " +
            "LEFT JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id " +
            "LEFT JOIN unit u ON cm.police_station_id = u.unit_id " +
            "LEFT JOIN district d ON u.district_id = d.district_id " +
            "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (slots.containsKey("district")) {
            sql.append(" AND LOWER(d.district_name) LIKE ?");
            params.add("%" + slots.get("district") + "%");
        }
        if (slots.containsKey("status")) {
            switch (slots.get("status")) {
                case "open": sql.append(" AND cm.case_status_id = 1"); break;
                case "investigation": sql.append(" AND cm.case_status_id = 2"); break;
                case "chargesheet": sql.append(" AND cm.case_status_id = 3"); break;
                case "closed": sql.append(" AND cm.case_status_id >= 4"); break;
            }
        }
        if (slots.containsKey("crimeNo")) {
            sql.append(" AND cm.crime_no = ?");
            params.add(slots.get("crimeNo"));
        }

        sql.append(" ORDER BY cm.crime_registered_date DESC LIMIT 20");
        return jdbc.queryForList(sql.toString(), params.toArray());
    }

    private List<Map<String, Object>> getCaseListData(Map<String, String> slots) {
        return queryCases(slots);
    }

    private Map<String, Object> getCaseDetailData(Map<String, String> slots) {
        String caseId = slots.get("caseId");
        if (caseId == null) return Map.of();
        try {
            List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM case_master WHERE case_master_id = ?", Integer.parseInt(caseId));
            return rows.isEmpty() ? Map.of() : rows.get(0);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private Map<String, Object> getStatsData() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCases", jdbc.queryForObject("SELECT COUNT(*) FROM case_master", Integer.class));
        stats.put("openCases", jdbc.queryForObject("SELECT COUNT(*) FROM case_master WHERE case_status_id = 1", Integer.class));
        stats.put("underInvestigation", jdbc.queryForObject("SELECT COUNT(*) FROM case_master WHERE case_status_id = 2", Integer.class));
        stats.put("chargeSheeted", jdbc.queryForObject("SELECT COUNT(*) FROM case_master WHERE case_status_id = 3", Integer.class));
        stats.put("closed", jdbc.queryForObject("SELECT COUNT(*) FROM case_master WHERE case_status_id >= 4", Integer.class));
        stats.put("totalComplainants", jdbc.queryForObject("SELECT COUNT(*) FROM complainant_details", Integer.class));
        stats.put("totalVictims", jdbc.queryForObject("SELECT COUNT(*) FROM victim", Integer.class));
        stats.put("totalAccused", jdbc.queryForObject("SELECT COUNT(*) FROM accused", Integer.class));
        stats.put("totalArrests", jdbc.queryForObject("SELECT COUNT(*) FROM arrest_surrender", Integer.class));
        stats.put("totalChargesheets", jdbc.queryForObject("SELECT COUNT(*) FROM chargesheet_details", Integer.class));
        return stats;
    }

    private List<Map<String, Object>> getDistrictStatsData() {
        return jdbc.queryForList(
            "SELECT d.district_name as district, COUNT(cm.case_master_id) as count " +
            "FROM case_master cm " +
            "JOIN unit u ON cm.police_station_id = u.unit_id " +
            "JOIN district d ON u.district_id = d.district_id " +
            "GROUP BY d.district_name ORDER BY count DESC");
    }

    private List<Map<String, Object>> getCrimeHeadStatsData() {
        return jdbc.queryForList(
            "SELECT ch.crime_group_name as crime_head, COUNT(cm.case_master_id) as count " +
            "FROM case_master cm " +
            "JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id " +
            "GROUP BY ch.crime_group_name ORDER BY count DESC");
    }

    private List<Map<String, Object>> getFinancialData(Map<String, String> slots) {
        if (slots.containsKey("caseId")) {
            return jdbc.queryForList("SELECT * FROM financial_transactions WHERE related_case_id = ?", Integer.parseInt(slots.get("caseId")));
        }
        return jdbc.queryForList("SELECT * FROM financial_transactions ORDER BY transaction_date DESC LIMIT 15");
    }

    private List<Map<String, Object>> getAccusedData() {
        return jdbc.queryForList(
            "SELECT a.accused_name, a.age_year, a.person_id, cm.crime_no, a.case_master_id " +
            "FROM accused a JOIN case_master cm ON a.case_master_id = cm.case_master_id");
    }

    private List<Map<String, Object>> getVictimData() {
        return jdbc.queryForList(
            "SELECT v.victim_name, v.age_year, cm.crime_no, v.case_master_id " +
            "FROM victim v JOIN case_master cm ON v.case_master_id = cm.case_master_id");
    }

    private List<Map<String, Object>> getComplainantData() {
        return jdbc.queryForList(
            "SELECT cd.complainant_name, cd.age_year, cm.crime_no, cd.case_master_id " +
            "FROM complainant_details cd JOIN case_master cm ON cd.case_master_id = cm.case_master_id");
    }

    private List<Map<String, Object>> getChargesheetData() {
        return jdbc.queryForList(
            "SELECT cm.case_master_id, cm.crime_no, cs.cs_date, cs.cs_type, csm.case_status_name " +
            "FROM chargesheet_details cs " +
            "JOIN case_master cm ON cs.case_master_id = cm.case_master_id " +
            "LEFT JOIN case_status_master csm ON cm.case_status_id = csm.case_status_id");
    }

    private Map<String, Object> getArrestStatsData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalArrests", jdbc.queryForObject("SELECT COUNT(*) FROM arrest_surrender", Integer.class));
        data.put("totalAccused", jdbc.queryForObject("SELECT COUNT(*) FROM accused", Integer.class));
        data.put("arrestedAccused", jdbc.queryForObject(
            "SELECT COUNT(DISTINCT accused_master_id) FROM inv_arrest_surrender_accused", Integer.class));
        return data;
    }

    private List<Map<String, Object>> getPersonSearchData(Map<String, String> slots) {
        String name = slots.getOrDefault("name", "");
        return jdbc.queryForList(
            "SELECT a.accused_name as name, 'ACCUSED' as type, cm.crime_no, a.case_master_id " +
            "FROM accused a JOIN case_master cm ON a.case_master_id = cm.case_master_id " +
            "WHERE LOWER(a.accused_name) LIKE ? " +
            "UNION ALL " +
            "SELECT v.victim_name as name, 'VICTIM' as type, cm.crime_no, v.case_master_id " +
            "FROM victim v JOIN case_master cm ON v.case_master_id = cm.case_master_id " +
            "WHERE LOWER(v.victim_name) LIKE ? " +
            "UNION ALL " +
            "SELECT cd.complainant_name as name, 'COMPLAINANT' as type, cm.crime_no, cd.case_master_id " +
            "FROM complainant_details cd JOIN case_master cm ON cd.case_master_id = cm.case_master_id " +
            "WHERE LOWER(cd.complainant_name) LIKE ?",
            "%" + name.toLowerCase() + "%", "%" + name.toLowerCase() + "%", "%" + name.toLowerCase() + "%");
    }

    private List<Map<String, Object>> getFlaggedData() {
        return jdbc.queryForList(
            "SELECT ft.*, cm.crime_no FROM financial_transactions ft " +
            "LEFT JOIN case_master cm ON ft.related_case_id = cm.case_master_id " +
            "WHERE ft.is_flagged = true ORDER BY ft.risk_score DESC");
    }

    private String nvl(Map<String, Object> map, String key, String def) {
        Object v = map.get(key);
        return v != null ? v.toString() : def;
    }

    private String nvl(Map<String, Object> map, String key) {
        return nvl(map, key, "—");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
