package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.common.constants.SQLQueryConstants;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.model.factory.FileFactory;
import ch.avocado.share.service.IDatabaseConnectionHandler;
import ch.avocado.share.service.ISearchEngineService;
import ch.avocado.share.service.exceptions.DataHandlerException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.IteratorChain;
import org.apache.solr.common.util.NamedList;

import java.io.*;
import java.io.File;
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
        try {
            SolrQuery query = new SolrQuery();
            query.setQuery(searchString);

            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();

            List<String> fileIds = new ArrayList<>();
            Iterator<SolrDocument> docIterator = results.iterator();

            while (docIterator.hasNext())
            {
                SolrDocument doc = docIterator.next();
                System.out.print(doc.keySet());

            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }


        return new ArrayList<String>();
    }

    @Override
    public boolean indexFile(ch.avocado.share.model.data.File file) {
        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
        try {
            up.addFile(new File(file.getPath()));
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

            PreparedStatement stmt = databaseService.getPreparedStatement(SQLQueryConstants.File.SELECT_ALL_FILES);
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
                ch.avocado.share.model.data.File file = FileFactory.getDefaultFile();
                file.setId(resultSet.getString(1));
                file.setTitle(resultSet.getString(2));
                file.setDescription(resultSet.getString(3));
                file.setLastChanged(new Date(resultSet.getTimestamp(4).getTime()));
                file.setCreationDate(new Date(resultSet.getTimestamp(5).getTime()));
                file.setPath(resultSet.getString(6));
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

        service.indexFile(new ch.avocado.share.model.data.File("80", new ArrayList<Category>(), new Date(), 0.0f, "1", "", "", file_docx, new Date(), "", "", ""));
        //service.reloadSearchIndex();
        service.search("*:*");

        SolrServer server = new CommonsHttpSolrServer("http://srv-lab-t-944:8983/solr/avocadoCollection");

        ContentStreamUpdateRequest up = new ContentStreamUpdateRequest("/update/extract");
        up.addFile(new File(file_pdf));
        up.setParam("literal.id", file_pdf);
        up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        NamedList<Object> result = server.request(up);

        QueryResponse rsp = server.query(new SolrQuery("*ERROR*"));
        System.out.println("Result: " + rsp.getResults().getNumFound());
    }
}