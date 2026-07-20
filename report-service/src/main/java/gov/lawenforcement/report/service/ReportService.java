package gov.lawenforcement.report.service;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import gov.lawenforcement.report.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter IST_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss 'IST'").withZone(IST);

    private static final Color BLACK = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY = Color.GRAY;
    private static final Color LIGHT_GRAY = new Color(220, 220, 220);

    // Typewriter-style Courier fonts (black only)
    private static final Font FONT_GOVT = new Font(Font.COURIER, 13, Font.BOLD, BLACK);
    private static final Font FONT_DEPT = new Font(Font.COURIER, 10, Font.BOLD, BLACK);
    private static final Font FONT_DEPT_SUB = new Font(Font.COURIER, 8, Font.NORMAL, BLACK);
    private static final Font FONT_FIR_TITLE = new Font(Font.COURIER, 14, Font.BOLD, BLACK);
    private static final Font FONT_CRIME_NO = new Font(Font.COURIER, 11, Font.BOLD, BLACK);
    private static final Font FONT_SECTION = new Font(Font.COURIER, 9, Font.BOLD, BLACK);
    private static final Font FONT_LABEL = new Font(Font.COURIER, 8, Font.BOLD, BLACK);
    private static final Font FONT_VALUE = new Font(Font.COURIER, 8, Font.NORMAL, BLACK);
    private static final Font FONT_SMALL = new Font(Font.COURIER, 7, Font.ITALIC, GRAY);
    private static final Font FONT_TABLE_HDR = new Font(Font.COURIER, 8, Font.BOLD, WHITE);
    private static final Font FONT_TABLE_CELL = new Font(Font.COURIER, 8, Font.NORMAL, BLACK);
    private static final Font FONT_BOLD_VAL = new Font(Font.COURIER, 8, Font.BOLD, BLACK);
    private static final Font FONT_FOOTER = new Font(Font.COURIER, 6, Font.NORMAL, GRAY);
    private static final Font FONT_CLASSIFY = new Font(Font.COURIER, 7, Font.BOLD, BLACK);
    private static final Font FONT_REF = new Font(Font.COURIER, 9, Font.BOLD, BLACK);
    private static final Font FONT_SIGN_LINE = new Font(Font.COURIER, 8, Font.NORMAL, BLACK);
    private static final Font FONT_SIGN_TITLE = new Font(Font.COURIER, 8, Font.BOLD, BLACK);

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    public byte[] generateFirReport(Integer caseId) {
        log.info("Generating FIR report for caseId: {}", caseId);

        try {
            CaseDetailResponse detail = fetchCaseDetail(caseId);
            Map<String, String> lookups = fetchLookups();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new SimplePageEvent());
            doc.open();

            addGovernmentHeader(doc);
            addFirTitleBlock(doc, detail.getCaseInfo());
            addCaseInfoSection(doc, detail.getCaseInfo(), lookups);
            addOccurrenceSection(doc, detail);
            addComplainantSection(doc, detail.getComplainants());
            addVictimSection(doc, detail.getVictims());
            addAccusedSection(doc, detail.getAccused());
            addActSectionTable(doc, detail.getActSections());
            addArrestSection(doc, detail.getArrests());
            addBriefFactsSection(doc, detail.getCaseInfo());
            addChargesheetSection(doc, detail.getChargesheets());
            addSignatureBlock(doc);
            addFooterDisclaimer(doc);

            doc.close();
            byte[] pdfBytes = baos.toByteArray();
            log.info("FIR report generated for caseId={}, size={} bytes", caseId, pdfBytes.length);
            return pdfBytes;
        } catch (Exception e) {
            log.error("Failed to generate FIR report for caseId={}", caseId, e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    // ─── GOVERNMENT HEADER ────────────────────────────────────────────

    private void addGovernmentHeader(Document doc) throws DocumentException {
        Paragraph gov = new Paragraph("GOVERNMENT OF KARNATAKA", FONT_GOVT);
        gov.setAlignment(Element.ALIGN_CENTER);
        doc.add(gov);

        Paragraph dept = new Paragraph("KARNATAKA STATE POLICE", FONT_DEPT);
        dept.setAlignment(Element.ALIGN_CENTER);
        doc.add(dept);

        Paragraph cid = new Paragraph("Criminal Investigation Department", FONT_DEPT_SUB);
        cid.setAlignment(Element.ALIGN_CENTER);
        doc.add(cid);

        Paragraph hq = new Paragraph("Police Headquarters, N.R. Square, Bengaluru - 560002", FONT_DEPT_SUB);
        hq.setAlignment(Element.ALIGN_CENTER);
        doc.add(hq);

        addSimpleRule(doc);
        doc.add(createSpacing(4));
    }

    // ─── FIR TITLE BLOCK ──────────────────────────────────────────────

    private void addFirTitleBlock(Document doc, CaseDetailResponse.CaseInfo c) throws DocumentException {
        Paragraph title = new Paragraph("FIRST INFORMATION REPORT", FONT_FIR_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(2);
        doc.add(title);

        Paragraph sub = new Paragraph("(Under Section 154 CrPC / Section 173 BNS)", FONT_SMALL);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(6);
        doc.add(sub);

        PdfPTable refTable = new PdfPTable(2);
        refTable.setWidthPercentage(100);
        refTable.setWidths(new float[]{60, 40});

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.addElement(new Paragraph("Crime No : " + nvl(c.getCrimeNo()), FONT_CRIME_NO));
        leftCell.addElement(createSpacing(2));
        leftCell.addElement(new Paragraph("Case No  : " + nvl(c.getCaseNo()), FONT_BOLD_VAL));
        leftCell.addElement(createSpacing(2));
        leftCell.addElement(new Paragraph("Date     : " + nvl(c.getCrimeRegisteredDate()), FONT_VALUE));

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        rightCell.addElement(new Paragraph("Ref. No.", FONT_LABEL));
        rightCell.addElement(new Paragraph(nvl(c.getCrimeNo()), FONT_REF));
        rightCell.addElement(createSpacing(2));
        rightCell.addElement(new Paragraph("Date of Issue", FONT_LABEL));
        rightCell.addElement(new Paragraph(nvl(c.getCrimeRegisteredDate()), FONT_REF));

        refTable.addCell(leftCell);
        refTable.addCell(rightCell);
        doc.add(refTable);

        addSimpleRule(doc);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: CASE INFORMATION ────────────────────────────────────

    private void addCaseInfoSection(Document doc, CaseDetailResponse.CaseInfo c, Map<String, String> lk) throws DocumentException {
        addSectionHeading(doc, "I", "CASE INFORMATION");

        PdfPTable t = createInfoTable();
        addInfoRow(t, "Crime Number", nvl(c.getCrimeNo()));
        addInfoRow(t, "Case Number", nvl(c.getCaseNo()));
        addInfoRow(t, "Date of Registration", nvl(c.getCrimeRegisteredDate()));
        addInfoRow(t, "Police Station", resolve(lk, "unit_", c.getPoliceStationId()));
        addInfoRow(t, "District", resolveDistrictFromUnit(lk, c.getPoliceStationId()));
        addInfoRow(t, "State", "Karnataka");
        addInfoRow(t, "Case Category", resolve(lk, "category_", c.getCaseCategoryId()));
        addInfoRow(t, "Gravity of Offence", resolve(lk, "gravity_", c.getGravityOffenceId()));
        addInfoRow(t, "Crime Major Head", resolve(lk, "crimeHead_", c.getCrimeMajorHeadId()));
        addInfoRow(t, "Crime Minor Head ID", c.getCrimeMinorHeadId() != null ? String.valueOf(c.getCrimeMinorHeadId()) : "N/A");
        addInfoRow(t, "Case Status", resolve(lk, "status_", c.getCaseStatusId()));
        addInfoRow(t, "Jurisdictional Court", resolve(lk, "court_", c.getCourtId()));
        addInfoRow(t, "Investigating Officer", resolve(lk, "employee_", c.getPolicePersonId()));
        if (c.getLatitude() != null && c.getLongitude() != null) {
            addInfoRow(t, "Location (Lat, Lng)", String.format("%.6f, %.6f", c.getLatitude(), c.getLongitude()));
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: OCCURRENCE DETAILS ──────────────────────────────────

    private void addOccurrenceSection(Document doc, CaseDetailResponse detail) throws DocumentException {
        addSectionHeading(doc, "II", "OCCURRENCE DETAILS");

        CaseDetailResponse.CaseInfo c = detail.getCaseInfo();
        PdfPTable t = createInfoTable();
        addInfoRow(t, "Incident Period (From)", formatDateTime(c.getIncidentFromDate()));
        addInfoRow(t, "Incident Period (To)", formatDateTime(c.getIncidentToDate()));
        addInfoRow(t, "Information Received at PS", formatDateTime(c.getInfoReceivedPsDate()));

        if (detail.getOccurrenceTime() != null) {
            CaseDetailResponse.OccurrenceTimeInfo ot = detail.getOccurrenceTime();
            if (ot.getOccurrenceFrom() != null) {
                addInfoRow(t, "Exact Occurrence (From)", formatDateTime(ot.getOccurrenceFrom()));
            }
            if (ot.getOccurrenceTo() != null) {
                addInfoRow(t, "Exact Occurrence (To)", formatDateTime(ot.getOccurrenceTo()));
            }
            if (c.getLatitude() != null && c.getLongitude() != null) {
                addInfoRow(t, "Occurrence Coordinates", String.format("%.6f, %.6f", c.getLatitude(), c.getLongitude()));
            }
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: COMPLAINANT DETAILS ─────────────────────────────────

    private void addComplainantSection(Document doc, List<CaseDetailResponse.ComplainantInfo> complainants) throws DocumentException {
        addSectionHeading(doc, "III", "COMPLAINANT DETAILS");

        if (complainants == null || complainants.isEmpty()) {
            addNoDataMessage(doc, "No complainant details recorded in the system.");
            return;
        }

        PdfPTable t = createDataTable(4, new float[]{6, 42, 26, 26});
        addTableHeaderRow(t, new String[]{"S.No.", "Name of Complainant", "Age (Years)", "Gender"});

        int idx = 1;
        for (CaseDetailResponse.ComplainantInfo ci : complainants) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    nvl(ci.getComplainantName()),
                    ci.getAgeYear() != null ? String.valueOf(ci.getAgeYear()) : "N/A",
                    genderName(ci.getGenderId())
            });
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: VICTIM DETAILS ──────────────────────────────────────

    private void addVictimSection(Document doc, List<CaseDetailResponse.VictimInfo> victims) throws DocumentException {
        addSectionHeading(doc, "IV", "VICTIM DETAILS");

        if (victims == null || victims.isEmpty()) {
            addNoDataMessage(doc, "No victim details recorded in the system.");
            return;
        }

        PdfPTable t = createDataTable(4, new float[]{6, 42, 26, 26});
        addTableHeaderRow(t, new String[]{"S.No.", "Name of Victim", "Age (Years)", "Gender"});

        int idx = 1;
        for (CaseDetailResponse.VictimInfo vi : victims) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    nvl(vi.getVictimName()),
                    vi.getAgeYear() != null ? String.valueOf(vi.getAgeYear()) : "N/A",
                    genderName(vi.getGenderId())
            });
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: ACCUSED DETAILS ─────────────────────────────────────

    private void addAccusedSection(Document doc, List<CaseDetailResponse.AccusedInfo> accused) throws DocumentException {
        addSectionHeading(doc, "V", "DETAILS OF ACCUSED / SUSPECT");

        if (accused == null || accused.isEmpty()) {
            addNoDataMessage(doc, "No accused / suspect details recorded in the system.");
            return;
        }

        PdfPTable t = createDataTable(5, new float[]{6, 32, 20, 22, 20});
        addTableHeaderRow(t, new String[]{"S.No.", "Name of Accused", "Age (Years)", "Person ID", "Gender"});

        int idx = 1;
        for (CaseDetailResponse.AccusedInfo ai : accused) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    nvl(ai.getAccusedName()),
                    ai.getAgeYear() != null ? String.valueOf(ai.getAgeYear()) : "N/A",
                    nvl(ai.getPersonId()),
                    genderName(ai.getGenderId())
            });
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: ACTS & SECTIONS ─────────────────────────────────────

    private void addActSectionTable(Document doc, List<CaseDetailResponse.ActSectionInfo> actSections) throws DocumentException {
        addSectionHeading(doc, "VI", "SECTIONS OF LAW INVOKED");

        if (actSections == null || actSections.isEmpty()) {
            addNoDataMessage(doc, "No sections of law have been invoked in this case.");
            return;
        }

        PdfPTable t = createDataTable(3, new float[]{8, 46, 46});
        addTableHeaderRow(t, new String[]{"S.No.", "Act / Code", "Section"});

        int idx = 1;
        for (CaseDetailResponse.ActSectionInfo as : actSections) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    nvl(as.getActCode()),
                    nvl(as.getSectionCode())
            });
        }
        doc.add(t);

        String charges = actSections.stream()
                .filter(a -> a.getActCode() != null && a.getSectionCode() != null)
                .map(a -> a.getActCode() + " Sec. " + a.getSectionCode())
                .collect(Collectors.joining(" | "));

        if (!charges.isEmpty()) {
            doc.add(createSpacing(2));
            PdfPTable summaryBox = new PdfPTable(1);
            summaryBox.setWidthPercentage(100);
            PdfPCell sc = new PdfPCell(new Paragraph("Sections Invoked: " + charges, FONT_BOLD_VAL));
            sc.setPadding(6);
            sc.setBorder(Rectangle.BOX);
            sc.setBorderColor(BLACK);
            sc.setBorderWidth(0.5f);
            summaryBox.addCell(sc);
            doc.add(summaryBox);
        }
        doc.add(createSpacing(4));
    }

    // ─── SECTION: ARREST DETAILS ──────────────────────────────────────

    private void addArrestSection(Document doc, List<CaseDetailResponse.ArrestInfo> arrests) throws DocumentException {
        addSectionHeading(doc, "VII", "ARREST / SURRENDER DETAILS");

        if (arrests == null || arrests.isEmpty()) {
            addNoDataMessage(doc, "No arrest or surrender details recorded in the system.");
            return;
        }

        PdfPTable t = createDataTable(4, new float[]{6, 32, 32, 30});
        addTableHeaderRow(t, new String[]{"S.No.", "Date of Arrest", "Investigating Officer", "Jurisdictional Court"});

        int idx = 1;
        for (CaseDetailResponse.ArrestInfo ar : arrests) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    ar.getArrestSurrenderDate() != null ? ar.getArrestSurrenderDate() : "N/A",
                    ar.getIoId() != null ? "IO ID: " + ar.getIoId() : "N/A",
                    ar.getCourtId() != null ? "Court ID: " + ar.getCourtId() : "N/A"
            });
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: BRIEF FACTS ─────────────────────────────────────────

    private void addBriefFactsSection(Document doc, CaseDetailResponse.CaseInfo c) throws DocumentException {
        if (c.getBriefFacts() == null || c.getBriefFacts().isBlank()) return;

        addSectionHeading(doc, "VIII", "BRIEF FACTS OF THE CASE");

        PdfPTable factBox = new PdfPTable(1);
        factBox.setWidthPercentage(100);
        PdfPCell fc = new PdfPCell();
        fc.setPadding(10);
        fc.setBorder(Rectangle.BOX);
        fc.setBorderColor(BLACK);
        fc.setBorderWidth(0.5f);
        fc.addElement(new Paragraph(c.getBriefFacts(), FONT_VALUE));
        factBox.addCell(fc);
        doc.add(factBox);
        doc.add(createSpacing(4));
    }

    // ─── SECTION: CHARGESHEET ─────────────────────────────────────────

    private void addChargesheetSection(Document doc, List<CaseDetailResponse.ChargesheetInfo> chargesheets) throws DocumentException {
        if (chargesheets == null || chargesheets.isEmpty()) return;

        addSectionHeading(doc, "IX", "CHARGESHEET DETAILS");

        PdfPTable t = createDataTable(3, new float[]{8, 46, 46});
        addTableHeaderRow(t, new String[]{"S.No.", "Chargesheet Date", "Type"});

        int idx = 1;
        for (CaseDetailResponse.ChargesheetInfo cs : chargesheets) {
            addTableDataRow(t, new String[]{
                    String.valueOf(idx++),
                    cs.getCsDate() != null ? cs.getCsDate() : "N/A",
                    cs.getCsType() != null ? cs.getCsType() : "N/A"
            });
        }
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── SIGNATURE BLOCK ──────────────────────────────────────────────

    private void addSignatureBlock(Document doc) throws DocumentException {
        doc.add(createSpacing(6));
        addSimpleRule(doc);
        doc.add(createSpacing(6));

        PdfPTable sigTable = new PdfPTable(3);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{34, 32, 34});

        // IO Signature
        PdfPCell ioSig = new PdfPCell();
        ioSig.setBorder(PdfPCell.NO_BORDER);
        ioSig.setPadding(8);
        ioSig.addElement(createSpacing(15));
        ioSig.addElement(createHorizontalLine(120));
        ioSig.addElement(createSpacing(2));
        ioSig.addElement(new Paragraph("Investigating Officer", FONT_SIGN_TITLE));
        ioSig.addElement(new Paragraph("Name & Designation", FONT_SIGN_LINE));
        ioSig.addElement(new Paragraph("Date: _______________", FONT_SMALL));
        ioSig.addElement(new Paragraph("Place: _______________", FONT_SMALL));
        sigTable.addCell(ioSig);

        // SHO / Seal
        PdfPCell shoSig = new PdfPCell();
        shoSig.setBorder(PdfPCell.NO_BORDER);
        shoSig.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        shoSig.setPadding(8);
        shoSig.addElement(createSpacing(15));
        shoSig.addElement(new Paragraph("[OFFICIAL SEAL]", new Font(Font.COURIER, 7, Font.ITALIC, GRAY)));
        shoSig.addElement(createSpacing(6));
        shoSig.addElement(createHorizontalLine(100));
        shoSig.addElement(createSpacing(2));
        shoSig.addElement(new Paragraph("Station House Officer", FONT_SIGN_TITLE));
        shoSig.addElement(new Paragraph("Name & Designation", FONT_SIGN_LINE));
        shoSig.addElement(new Paragraph("Date: _______________", FONT_SMALL));
        sigTable.addCell(shoSig);

        // ACP/DSP
        PdfPCell acpSig = new PdfPCell();
        acpSig.setBorder(PdfPCell.NO_BORDER);
        acpSig.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        acpSig.setPadding(8);
        acpSig.addElement(createSpacing(15));
        acpSig.addElement(createHorizontalLine(120));
        acpSig.addElement(createSpacing(2));
        acpSig.addElement(new Paragraph("ACP / DSP", FONT_SIGN_TITLE));
        acpSig.addElement(new Paragraph("Name & Designation", FONT_SIGN_LINE));
        acpSig.addElement(new Paragraph("Date: _______________", FONT_SMALL));
        sigTable.addCell(acpSig);

        doc.add(sigTable);
    }

    // ─── FOOTER DISCLAIMER ────────────────────────────────────────────

    private void addFooterDisclaimer(Document doc) throws DocumentException {
        doc.add(createSpacing(4));
        addSimpleRule(doc);
        doc.add(createSpacing(2));

        Paragraph disclaimer = new Paragraph(
                "RESTRICTED  -  This document is classified as RESTRICTED under the Karnataka Police Act, 1963 " +
                "and the Official Secrets Act, 1923. Unauthorized disclosure, reproduction, or distribution " +
                "of this document or its contents is strictly prohibited and punishable under law. " +
                "This report is generated through the Crime Analytics & Intelligence Platform for official " +
                "law enforcement use only.", FONT_CLASSIFY);
        disclaimer.setAlignment(Element.ALIGN_JUSTIFIED);
        disclaimer.setIndentationLeft(10);
        disclaimer.setIndentationRight(10);
        doc.add(disclaimer);

        doc.add(createSpacing(2));
        Paragraph note = new Paragraph(
                "Note: This report is computer-generated and does not require a physical signature to be valid. " +
                "The digital record is maintained in the Crime Analytics Platform with full audit trail.",
                FONT_SMALL);
        note.setAlignment(Element.ALIGN_CENTER);
        doc.add(note);
    }

    // ─── STYLING HELPERS ──────────────────────────────────────────────

    private void addSectionHeading(Document doc, String numeral, String title) throws DocumentException {
        Paragraph heading = new Paragraph(numeral + ".  " + title.toUpperCase(), FONT_SECTION);
        heading.setSpacingBefore(8);
        heading.setSpacingAfter(3);
        doc.add(heading);

        addSimpleRule(doc);
    }

    private PdfPTable createInfoTable() throws BadElementException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{32, 68});
        t.setSpacingBefore(1);
        t.setSpacingAfter(1);
        return t;
    }

    private void addInfoRow(PdfPTable table, String label, String value) throws BadElementException {
        PdfPCell lc = new PdfPCell(new Paragraph(label, FONT_LABEL));
        lc.setPadding(4);
        lc.setBorder(Rectangle.BOX);
        lc.setBorderColor(BLACK);
        lc.setBorderWidth(0.3f);

        PdfPCell vc = new PdfPCell(new Paragraph(value != null ? value : "N/A", FONT_VALUE));
        vc.setPadding(4);
        vc.setBorder(Rectangle.BOX);
        vc.setBorderColor(BLACK);
        vc.setBorderWidth(0.3f);

        table.addCell(lc);
        table.addCell(vc);
    }

    private PdfPTable createDataTable(int cols, float[] widths) throws BadElementException {
        PdfPTable t = new PdfPTable(cols);
        t.setWidthPercentage(100);
        t.setWidths(widths);
        t.setSpacingBefore(2);
        t.setSpacingAfter(2);
        return t;
    }

    private void addTableHeaderRow(PdfPTable table, String[] headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(h, FONT_TABLE_HDR));
            cell.setBackgroundColor(BLACK);
            cell.setPadding(4);
            cell.setBorder(Rectangle.BOX);
            cell.setBorderColor(BLACK);
            cell.setBorderWidth(0.5f);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableDataRow(PdfPTable table, String[] values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Paragraph(v, FONT_TABLE_CELL));
            cell.setPadding(4);
            cell.setBorder(Rectangle.BOX);
            cell.setBorderColor(BLACK);
            cell.setBorderWidth(0.3f);
            table.addCell(cell);
        }
    }

    private void addNoDataMessage(Document doc, String msg) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Paragraph(msg, new Font(Font.COURIER, 8, Font.ITALIC, GRAY)));
        c.setPadding(6);
        c.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        c.setBorder(Rectangle.BOX);
        c.setBorderColor(LIGHT_GRAY);
        c.setBorderWidth(0.3f);
        t.addCell(c);
        doc.add(t);
        doc.add(createSpacing(4));
    }

    // ─── GRAPHICS HELPERS ─────────────────────────────────────────────

    private void addSimpleRule(Document doc) throws DocumentException {
        PdfPTable rule = new PdfPTable(1);
        rule.setWidthPercentage(100);
        PdfPCell rc = new PdfPCell();
        rc.setBorder(PdfPCell.BOTTOM);
        rc.setBorderColor(BLACK);
        rc.setBorderWidth(1.0f);
        rc.setFixedHeight(1);
        rule.addCell(rc);
        doc.add(rule);
    }

    private PdfPTable createSpacing(float points) throws BadElementException {
        PdfPTable s = new PdfPTable(1);
        s.setWidthPercentage(100);
        PdfPCell sc = new PdfPCell();
        sc.setBorder(PdfPCell.NO_BORDER);
        sc.setFixedHeight(points);
        s.addCell(sc);
        return s;
    }

    private PdfPTable createHorizontalLine(float width) throws BadElementException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell lc = new PdfPCell();
        lc.setBorder(PdfPCell.BOTTOM);
        lc.setBorderColor(BLACK);
        lc.setBorderWidth(0.5f);
        lc.setFixedHeight(1);
        line.addCell(lc);
        return line;
    }

    // ─── DATA HELPERS ─────────────────────────────────────────────────

    private CaseDetailResponse fetchCaseDetail(Integer caseId) {
        return webClient.get()
                .uri("/api/v1/cases/{id}", caseId)
                .retrieve()
                .bodyToMono(CaseDetailResponse.class)
                .block();
    }

    private Map<String, String> fetchLookups() {
        Map<String, String> map = new HashMap<>();
        fetchLookupList(map, "unit", "/api/v1/lookups/units");
        fetchLookupList(map, "status", "/api/v1/lookups/statuses");
        fetchLookupList(map, "court", "/api/v1/lookups/courts");
        fetchLookupList(map, "category", "/api/v1/lookups/categories");
        fetchLookupList(map, "crimeHead", "/api/v1/lookups/crime-heads");
        fetchLookupList(map, "employee", "/api/v1/lookups/employees");
        fetchLookupList(map, "gravity", "/api/v1/lookups/gravity-offences");
        return map;
    }

    private void fetchLookupList(Map<String, String> map, String prefix, String uri) {
        try {
            List<LookupEntry> entries = webClient.get().uri(uri)
                    .retrieve().bodyToFlux(LookupEntry.class)
                    .collectList().block();
            if (entries != null) {
                entries.forEach(e -> map.put(prefix + "_" + e.getId(), e.getName()));
            }
        } catch (Exception e) {
            log.warn("Failed to fetch lookup: {}", uri, e);
        }
    }

    private String resolve(Map<String, String> lookups, String prefix, Integer id) {
        if (id == null) return "N/A";
        return lookups.getOrDefault(prefix + id, "ID: " + id);
    }

    private String nvl(String val) {
        return val != null && !val.isBlank() ? val : "N/A";
    }

    private String genderName(Integer genderId) {
        if (genderId == null) return "N/A";
        if (genderId == 1) return "Male";
        if (genderId == 2) return "Female";
        if (genderId == 3) return "Other";
        return "ID: " + genderId;
    }

    private String formatDateTime(String iso) {
        if (iso == null || iso.isBlank()) return "N/A";
        try {
            Instant instant = Instant.parse(iso);
            return IST_FMT.format(instant);
        } catch (Exception e) {
            return iso;
        }
    }

    private String resolveDistrictFromUnit(Map<String, String> lk, Integer unitId) {
        if (unitId == null) return "N/A";
        String unitName = lk.getOrDefault("unit_" + unitId, "");
        if (unitName.contains("Bengaluru")) return "Bengaluru Urban";
        if (unitName.contains("Kalaburgi")) return "Kalaburgi";
        if (unitName.contains("Mysuru")) return "Mysuru";
        if (unitName.contains("Mangaluru")) return "Mangaluru";
        if (unitName.contains("Hubballi") || unitName.contains("Dharwad")) return "Hubballi-Dharwad";
        if (unitName.contains("Belagavi")) return "Belagavi";
        if (unitName.contains("Shivamogga")) return "Shivamogga";
        if (unitName.contains("Ballari")) return "Ballari";
        if (unitName.contains("Vijayapura")) return "Vijayapura";
        if (unitName.contains("Raichur")) return "Raichur";
        return unitName;
    }

    // ─── LEGACY METHODS ───────────────────────────────────────────────

    public byte[] generateIncidentReport(String firNumber, String title, String description,
                                         String severity, String status, String district,
                                         String date, String address) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(document, baos);
            document.open();

            addGovernmentHeader(document);
            Paragraph titleP = new Paragraph("Crime Incident Report", FONT_FIR_TITLE);
            titleP.setAlignment(Element.ALIGN_CENTER);
            titleP.setSpacingAfter(10);
            document.add(titleP);

            PdfPTable detailsTable = createInfoTable();
            addInfoRow(detailsTable, "FIR Number", firNumber);
            addInfoRow(detailsTable, "Title", title);
            addInfoRow(detailsTable, "Date", date);
            addInfoRow(detailsTable, "District", district);
            addInfoRow(detailsTable, "Address", address);
            addInfoRow(detailsTable, "Severity", severity);
            addInfoRow(detailsTable, "Status", status);
            document.add(detailsTable);
            document.add(Chunk.NEWLINE);

            if (description != null && !description.isBlank()) {
                document.add(new Paragraph("Description", FONT_SECTION));
                document.add(Chunk.NEWLINE);
                Paragraph p = new Paragraph(description, FONT_VALUE);
                p.setIndentationLeft(10);
                document.add(p);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    public byte[] generateCriminalProfile(String personName, String personType,
                                          int convictionCount, double riskScore,
                                          String charges) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(document, baos);
            document.open();

            addGovernmentHeader(document);
            Paragraph titleP = new Paragraph("Criminal Profile Report", FONT_FIR_TITLE);
            titleP.setAlignment(Element.ALIGN_CENTER);
            titleP.setSpacingAfter(10);
            document.add(titleP);

            PdfPTable t = createInfoTable();
            addInfoRow(t, "Name", personName);
            addInfoRow(t, "Person Type", personType);
            document.add(t);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Risk Assessment", FONT_SECTION));
            document.add(createSpacing(2));
            PdfPTable riskTable = createInfoTable();
            addInfoRow(riskTable, "Risk Score", String.format("%.2f", riskScore));
            String riskLevel = riskScore >= 8.0 ? "CRITICAL" : riskScore >= 6.0 ? "HIGH" :
                    riskScore >= 4.0 ? "MEDIUM" : riskScore >= 2.0 ? "LOW" : "MINIMAL";
            addInfoRow(riskTable, "Risk Level", riskLevel);
            document.add(riskTable);
            document.add(createSpacing(4));

            if (charges != null && !charges.isBlank()) {
                document.add(new Paragraph("Charges", FONT_SECTION));
                document.add(createSpacing(2));
                PdfPTable ct = new PdfPTable(1);
                ct.setWidthPercentage(100);
                PdfPCell cc = new PdfPCell(new Paragraph(charges, FONT_VALUE));
                cc.setPadding(8);
                cc.setBorder(Rectangle.BOX);
                cc.setBorderColor(BLACK);
                ct.addCell(cc);
                document.add(ct);
                document.add(createSpacing(4));
            }

            document.add(new Paragraph("Conviction History", FONT_SECTION));
            document.add(createSpacing(2));
            PdfPTable convTable = createInfoTable();
            addInfoRow(convTable, "Total Convictions", String.valueOf(convictionCount));
            document.add(convTable);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    // ─── PAGE EVENT (Header/Footer on every page) ─────────────────────

    private static class SimplePageEvent extends PdfPageEventHelper {
        private BaseFont baseFont;
        private BaseFont boldFont;
        private int totalPages = 0;

        public SimplePageEvent() {
            try {
                this.baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
                this.boldFont = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            totalPages++;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            float pageWidth = document.getPageSize().getWidth();
            float pageHeight = document.getPageSize().getHeight();

            canvas.saveState();

            // Top rule line
            canvas.setColorStroke(BLACK);
            canvas.setLineWidth(0.5f);
            canvas.moveTo(50, pageHeight - 45);
            canvas.lineTo(pageWidth - 50, pageHeight - 45);
            canvas.stroke();

            // Header text
            canvas.beginText();
            canvas.setFontAndSize(boldFont, 7);
            canvas.setColorFill(BLACK);
            canvas.showTextAligned(Element.ALIGN_LEFT, "GOVERNMENT OF KARNATAKA  |  KARNATAKA STATE POLICE",
                    50, pageHeight - 40, 0);
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 7);
            canvas.setColorFill(BLACK);
            String pageLabel = "Page " + writer.getPageNumber() + " of " + totalPages;
            canvas.showTextAligned(Element.ALIGN_RIGHT, pageLabel,
                    pageWidth - 50, pageHeight - 40, 0);
            canvas.endText();

            // Footer rule line
            canvas.setLineWidth(0.5f);
            canvas.moveTo(50, 35);
            canvas.lineTo(pageWidth - 50, 35);
            canvas.stroke();

            // Footer text
            canvas.beginText();
            canvas.setFontAndSize(baseFont, 6);
            canvas.setColorFill(BLACK);
            canvas.showTextAligned(Element.ALIGN_LEFT, "RESTRICTED  |  Crime Analytics & Intelligence Platform",
                    50, 25, 0);
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 6);
            canvas.setColorFill(BLACK);
            canvas.showTextAligned(Element.ALIGN_CENTER, "Generated: " + IST_FMT.format(Instant.now()),
                    pageWidth / 2, 25, 0);
            canvas.endText();

            canvas.beginText();
            canvas.setFontAndSize(baseFont, 6);
            canvas.setColorFill(BLACK);
            canvas.showTextAligned(Element.ALIGN_RIGHT, "FOR OFFICIAL USE ONLY",
                    pageWidth - 50, 25, 0);
            canvas.endText();

            canvas.restoreState();
        }
    }
}
