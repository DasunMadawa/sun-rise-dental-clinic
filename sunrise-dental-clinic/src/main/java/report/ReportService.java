package report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

public class ReportService {

    public static JasperPrint fill(String reportResourcePath, Map<String, Object> params, JRDataSource dataSource) throws JRException {
        InputStream in = ReportService.class.getResourceAsStream(reportResourcePath);
        JasperReport report = JasperCompileManager.compileReport(in);
        return JasperFillManager.fillReport(report, params, dataSource);
    }

    public static JasperPrint fill(String reportResourcePath, Map<String, Object> params, Collection<?> rows) throws JRException {
        return fill(reportResourcePath, params, new JRBeanCollectionDataSource(rows));
    }

    public static JasperPrint fill(String reportResourcePath, Map<String, Object> params) throws JRException {
        return fill(reportResourcePath, params, new JREmptyDataSource());
    }

    public static void view(JasperPrint print) {
        SwingUtilities.invokeLater(() -> JasperViewer.viewReport(print, false));
    }

    public static File exportToPdf(JasperPrint print, String fileName) throws JRException {
        File file = new File(System.getProperty("java.io.tmpdir"), fileName);
        JasperExportManager.exportReportToPdfFile(print, file.getAbsolutePath());
        return file;
    }

}
