package org.nuxeo.ecm.platform.template.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.impl.LogEntryImpl;
import org.nuxeo.template.api.TemplateInput;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;
import org.nuxeo.template.fm.FMContextBuilder;
import org.nuxeo.template.processors.xdocreport.ZipXmlHelper;

import static org.junit.Assert.*;

public class TestAuditEntriesODTProcessing extends SimpleTemplateDocTestCase {

    @Test
    public void testRenderWithAuditEntries() throws Exception {

        // build fake AuditEntries
        ArrayList<LogEntry> auditEntries = new ArrayList<LogEntry>();
        for (int i = 0; i < 5; i++) {
            LogEntryImpl entry = new LogEntryImpl();
            entry.setId(i);
            entry.setComment("Comment" + i);
            entry.setCategory("Testing");
            entry.setEventId("TestEvent" + i);
            entry.setPrincipalName("TestingUser");
            auditEntries.add(entry);
        }
        FMContextBuilder.testAuditEntries = auditEntries;

        TemplateBasedDocument adapter = setupTestDocs();
        DocumentModel testDoc = adapter.getAdaptedDoc();
        assertNotNull(testDoc);

        TemplateSourceDocument template = adapter.getSourceTemplate(TEMPLATE_NAME);
        assertNotNull(template);

        List<TemplateInput> params = template.getParams();
        assertEquals(0, params.size());

        Blob rendered = adapter.renderWithTemplate(TEMPLATE_NAME);
        assertNotNull(rendered);

        String xmlContent = ZipXmlHelper.readXMLContent(rendered,
                ZipXmlHelper.OOO_MAIN_FILE);

        assertTrue(xmlContent.contains("TestEvent0"));
        assertTrue(xmlContent.contains("TestEvent1"));
        assertTrue(xmlContent.contains("TestEvent2"));
        assertTrue(xmlContent.contains("TestEvent3"));
        assertTrue(xmlContent.contains("TestEvent4"));
    }

    @Override
    protected Blob getTemplateBlob() {
        File file = FileUtils.getResourceFileFromContext("data/auditTest.odt");
        Blob fileBlob = new FileBlob(file);
        fileBlob.setFilename("auditTest.odt");
        return fileBlob;
    }

}