package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.FileStorageConstants;
import ch.avocado.share.common.constants.sql.FileConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.Rating;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.ISearchEngineService;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.SpellingParams;
import org.apache.solr.common.util.NamedList;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bergm on 28/04/2016.
 */
public class SolRJService implements ISearchEngineService {

    private SolrServer server;
    private IDatabaseConnectionHandler databaseService;

    public SolRJService(IDatabaseConnectionHandler databaseConnectionHandler) throws MalformedURLException {
        if (databaseConnectionHandler == null ) throw new IllegalArgumentException("databaseConnectionHandler is null");

        databaseService = databaseConnectionHandler;
        server = new CommonsHttpSolrServer("http://srv-lab-t-944:8983/solr/avocadoCollection");

        reloadSearchIndex();
    }

    @Override
    public List<String> search(String searchString) {
        List<String> fileIds = new ArrayList<>();

        try {
            SolrQuery qry = new SolrQuery(searchString);
            qry.setFields("id");
            qry.setShowDebugInfo(true);
            qry.setRows(100);
            QueryRequest qryReq = new QueryRequest(qry);
            QueryResponse resp = qryReq.process(server);

            SolrDocumentList results = resp.getResults();

            Iterator<SolrDocument> docIterator = results.iterator();

            while (docIterator.hasNext())
            {
                SolrDocument doc = docIterator.next();
                fileIds.add(doc.getFieldValue("id").toString());
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return fileIds;
    }

    @Override
    public boolean indexFile(ch.avocado.share.model.data.File file) {
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
        try {
            up.addFile(new File(ServiceLocator.getService(IFileStorageHandler.class).getStoreDirectory() + "\\" + file.getPath()));
            up.setParam("literal.id", file.getId());
            up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
            NamedList<Object> result = server.request(up);

            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void reloadSearchIndex() {
        try {
            server.deleteByQuery("*:*");
            server.commit();

            PreparedStatement stmt = databaseService.getPreparedStatement(FileConstants.SELECT_ALL_FILES);
            ResultSet rs = databaseService.executeQuery(stmt);

            List<ch.avocado.share.model.data.File> files = getMultipleFilesFromResultSet(rs);

            for (ch.avocado.share.model.data.File file : files) {
                indexFile(file);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DataHandlerException e) {
            e.printStackTrace();
        }
    }

    private List<ch.avocado.share.model.data.File> getMultipleFilesFromResultSet(ResultSet resultSet) throws DataHandlerException {
        try {
            List<ch.avocado.share.model.data.File> files = new ArrayList<>();
            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String title = resultSet.getString(2);
                String desc = resultSet.getString(3);
                Date lastChanged = new Date(resultSet.getTimestamp(4).getTime());
                Date creationDate = new Date(resultSet.getTimestamp(5).getTime());
                String path = resultSet.getString(6);

                ch.avocado.share.model.data.File file =  new ch.avocado.share.model.data.File(id, new ArrayList<Category>(), creationDate, new Rating(), "unknown", desc, title, path, lastChanged, "unknown", "unknown", "unknown");
                files.add(file);
            }
            return files;
        } catch (SQLException e) {
            throw new DataHandlerException(e);
        }
    }

    public static void main(String[] args) throws IOException, SolrServerException, ServiceNotFoundException {
        //location of source documents
        //later this will be switched to a database
        String path = "C:\\Users\\bergm\\OneDrive\\Documents\\";
        //String file_html = path + "mobydick.htm";
        String file_docx = path + "Reisevorschlag_Teneriffa.docx";
        String file_pdf = path + "pgadmin.log";

        SolRJService service = new SolRJService(ServiceLocator.getService(IDatabaseConnectionHandler.class));

        service.indexFile(new ch.avocado.share.model.data.File("80", new ArrayList<Category>(), new Date(), new Rating(), "1", "unknown", "unknown", file_docx, new Date(), "unknown", "unknown", "unknown"));
        //service.reloadSearchIndex();
        service.search("*:*");


    }
}