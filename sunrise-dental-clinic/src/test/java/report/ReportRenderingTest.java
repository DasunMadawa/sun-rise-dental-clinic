package report;

import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import report.dto.RevenueRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Regression test for the "Multiple decimal separators in pattern 'Rs. #,##0.00'"
 * crash reported while printing/emailing a bill.
 * <p>
 * This is the one bug in this session that is fully safe to pin down with an
 * automated test, because rendering a report never touches the live database or
 * mailbox - {@link ReportService#fill} just needs a compiled .jrxml and a params
 * map/data source, both of which we can fabricate here. Every currency textField
 * in bill_report.jrxml and revenue_report.jrxml used the pattern
 * {@code "Rs. #,##0.00"}, which java.text.DecimalFormat rejects because the "."
 * in the literal "Rs." text is itself read as a second decimal separator. The
 * fix quotes the literal text as {@code "'Rs.' #,##0.00"}.
 * <p>
 * <b>How this test was actually used (TDD, applied after the fact):</b>
 * with the old pattern still in the .jrxml files, this test failed with the exact
 * exception the user pasted from the running app (see docs/TestPlan.md for the
 * red/green transcript). Re-quoting the pattern turned it green with no other
 * change required, which is exactly the kind of confirmation a stack trace alone
 * can't give you - it proves the *fix* is what's holding, not a coincidence of
 * whatever data happened to be in the database at the time.
 */
class ReportRenderingTest {

    @Test
    @DisplayName("bill_report.jrxml fills without throwing (regression test for the currency-pattern crash)")
    void billReport_fillsWithoutThrowing() {
        Map<String, Object> params = new HashMap<>();
        params.put("paymentId", "PAY0001");
        params.put("appointmentNo", "APT0001");
        params.put("paymentDate", "2026-09-05");
        params.put("patientId", "PT001");
        params.put("patientName", "Test Patient");
        params.put("dentistName", "Dr. Test");
        params.put("treatmentName", "Filling");
        params.put("noTooth", 2);
        params.put("consultationFee", 500.0);
        params.put("treatmentCost", 6000.0);
        params.put("discount", 0.0);
        params.put("total", 6500.0);
        params.put("paymentMethod", "CASH");

        JasperPrint print = assertDoesNotThrow(
                () -> ReportService.fill("/reports/bill_report.jrxml", params),
                "bill_report.jrxml must render a bill without a DecimalFormat error"
        );
        assertFalse(print.getPages().isEmpty());
    }

    @Test
    @DisplayName("revenue_report.jrxml fills without throwing, including the TOTAL summary row")
    void revenueReport_fillsWithoutThrowing() {
        Map<String, Object> params = new HashMap<>();
        params.put("year", "2026");

        List<RevenueRow> rows = new ArrayList<>();
        rows.add(new RevenueRow("January", 12345.67));
        rows.add(new RevenueRow("February", 8900.0));

        JasperPrint print = assertDoesNotThrow(
                () -> ReportService.fill("/reports/revenue_report.jrxml", params, rows),
                "revenue_report.jrxml must render, including its TOTAL summary band"
        );
        assertFalse(print.getPages().isEmpty());
    }
}
