package com.samsung.sec.dexter.eclipse.ui.util;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.view.DefectHelpView;

import org.eclipse.ui.IViewPart;

public class CheckerDescriptionUtil {
    public static void openCheckerDescriptionView(final Defect defect) {
        IViewPart view = EclipseUtil.findView(DefectHelpView.ID);
        IDexterClient client = DexterUIActivator.getDefault().getDexterClient();
        final DefectHelpView helpView = (DefectHelpView) view;

        if (client.isLogin()) {
            String url = CheckerDescriptionUtil.createUrlForDefect(client, defect);
            helpView.setUrl(url);
        } else {
            String html = CheckerDescriptionUtil.createHtmlContentsForWebSites(defect);
            helpView.setHtmlContents(html);
        }

        EclipseUtil.showView(DefectHelpView.ID);
    }

    private static String createUrlForDefect(final IDexterClient client, final Defect defect) {
        StringBuilder url = new StringBuilder(1024);

        url.append("http://").append(client.getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
                .append(client.getServerPort()).append(DexterConfig.DEFECT_HELP_BASE).append("/") //$NON-NLS-1$
                .append(defect.getToolName()).append("/").append(defect.getLanguage()) //$NON-NLS-1$
                .append(DexterConfig.DEFECT_HELP)
                .append("/").append(defect.getCheckerCode()).append(".html"); //$NON-NLS-1$ //$NON-NLS-2$

        if (defect.getOccurences() != null && defect.getOccurences().size() == 1) {
            url.append("#").append(defect.getFirstOccurence().getCode());
        }

        if (client.hasSupportedHelpHtmlFile(url) == false) {
            url.setLength(0);
            url.append("http://").append(client.getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
                    .append(client.getServerPort()).append(DexterConfig.DEFECT_HELP_BASE).append("/") //$NON-NLS-1$
                    .append(DexterConfig.NOT_FOUND_CHECKER_DESCRIPTION).append("/") //$NON-NLS-1$
                    .append(DexterConfig.EMPTY_HTML_FILE_NAME).append(".html"); //$NON-NLS-1$
        }

        return url.toString();
    }

    private static String createHtmlContentsForWebSites(final Defect defect) {
        StringBuilder html = new StringBuilder(1024);

        html.append("<html><body>");
        html.append("<h2> Please refer to following search sites:</h2>");
        html.append("<a href='http://stackoverflow.com/search?q=");
        html.append(defect.getToolName()).append("+").append(defect.getCheckerCode()).append("+in+");
        html.append(defect.getLanguage()).append("'>find in stackoverflow</a><br/>");
        html.append("<a href='http://www.google.com/search?q=");
        html.append(defect.getToolName()).append("+").append(defect.getCheckerCode()).append("+in+");
        html.append(defect.getLanguage()).append("'>find in google</a><br/>");
        html.append("</body></html>");

        return html.toString();
    }
}
