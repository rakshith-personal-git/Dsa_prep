//package DSA_Interview_Questions.CompanyWisePrep.Toast.CodeReview;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//
//import io.dropwizard.hibernate.UnitOfWork;
//
///**
// * The purpose of this service is to gather all of the restaurant menus in the world by any means necessary.
// * We allow everyone and everything to use this service as we want all the menus in the world.
// *
// * Some other service will come along later to do some processing on this data.
// * Example use case: a service may use this data to figure out the most popular dish in restaurants in Boston.
// */
//@Path("/common")
//@Produces(MediaType.TEXT_PLAIN)
//public class MenuDataResource {
//
//    private final DataDAO dao;
//
//    public enum ReaderType {
//        FILE_BASED, URL;
//    }
//
//    public MenuDataResource(DataDAO dao) {
//        this.dao = dao;
//    }
//
//    // example: www.menus.com/common/loadFile
//    @GET
//    @Path("/loadFile")
//    @UnitOfWork
//    public String loadFile(@QueryParam("location") String location) {
//        /*
//        Validation of the input is missing, applies to all the methods below
//        */
//        byte[] bytes = Reader.retrieve(ReaderType.FILE_BASED, location);
//        if(loadByes(location, bytes)) {
//            return "success!";
//        } else {
//            return "error!";
//        }
//    }
//
//    @GET
//    @Path("/loadUrl")
//    @UnitOfWork
//    public String loadUrl(@QueryParam("location") String location) {
//        byte[] bytes = Reader.retrieve(ReaderType.URL, location);
//        if(loadByes(location, bytes)) {
//            return "success!";
//        } else {
//            return "error!";
//        }
//    }
//
//    @GET
//    @Path("/")
//    @Produces(MediaType.TEXT_HTML)
//    @UnitOfWork
//    public String list() {
//        StringBuilder html = new StringBuilder();
//        html.append("<html><body><list>");
//        List<MenuData> menuDatas = dao.findAll();
//        for(MenuData menuData : menuDatas) {
//            html.append("<li>");
//            html.append(menuData.getId() + "::<a href="+ menuData.getId()+">" + menuData.getLocator()+"</a>");
//            html.append("</li>");
//        }
//        html.append("</list></body></html>");
//        return html.toString();
//    }
//
//    @GET
//    @Path("{id}")
//    @Produces(MediaType.TEXT_PLAIN)
//    @UnitOfWork
//    public byte[] get(@PathParam("id") Long id) {
//        Optional<MenuData> od = dao.findById(id);
//        if(!od.isPresent()) {
//            throw new RuntimeException("Error 404 Not Found");
//        }
//        MenuData d = od.get();
//        return d.getBytes();
//    }
//
//    public static class Reader {
//        private static Logger logger = Logger.getLogger(Reader.class.getName());
//
//        public static byte[] retrieve (ReaderType type, String locator)  {
//            try {
//                switch (type) {
//                    case FILE_BASED: {
//                        return Files.readAllBytes(Paths.get(locator));
//                    }
//                    case URL: {
//                        URL url = new URL(locator);
//                        URLConnection conn = url.openConnection();
//                        InputStream is = conn.getInputStream();
//                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//                        int nRead;
//                        byte[] data = new byte[16384];
//
//                        while ((nRead = is.read(data, 0, data.length)) != -1) {
//                            buffer.write(data, 0, nRead);
//                        }
//
//                        buffer.flush();
//
//                        return buffer.toByteArray();
//                    }
//                    default: {
//                        throw new UnsupportedOperationException("cannot happen");
//                    }
//                }
//            }catch (IOException e) {
//                logger.log(Level.SEVERE, "caught io error", e);
//                return null;
//            }
//        }
//    }
//
//    private boolean loadByes (String locator, byte[] bytes) {
//        MenuData menuData = new MenuData();
//        menuData.setBytes(bytes);
//        menuData.setLocator(locator);
//        dao.create(menuData);
//        return true;
//    }
//
//
//}
//
//
//
//package com.toasttab.codereview.service;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.util.Objects;
//@Entity
//@Table(name = "RETRIEVED_DATA")
//public class MenuData {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//
//    @Column(name = "BYTE_DATA", nullable = false)
//    private byte[] bytes;
//
//    @Column(name = "LOCATOR", nullable = false)
//    private String locator;
//
//    public MenuData() {
//    }
//
//    public MenuData(byte[] bytes) {
//        this.bytes = bytes;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public byte[] getBytes() {
//        return bytes;
//    }
//
//    public void setBytes(byte[] bytes) {
//        this.bytes = bytes;
//    }
//
//    public String getLocator() {
//        return locator;
//    }
//
//    public void setLocator(String locator) {
//        this.locator = locator;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof MenuData)) {
//            return false;
//        }
//
//        final MenuData that = (MenuData) o;
//
//        return Objects.equals(this.id, that.id) &&
//                Objects.equals(this.locator, that.locator) &&
//                Objects.equals(this.bytes, that.bytes);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, bytes, locator);
//    }
//}
//
//
//package com.toasttab.codereview.service;
//
//
//import org.hibernate.Query;
//import org.hibernate.SessionFactory;
//
//import java.util.List;
//import java.util.Optional;
//
//import io.dropwizard.hibernate.AbstractDAO;
//
//public class DataDAO extends AbstractDAO<MenuData> {
//    public DataDAO(SessionFactory factory) {
//        super(factory);
//    }
//
//    public Optional<MenuData> findById(Long id) {
//        return Optional.ofNullable(get(id));
//    }
//
//    public MenuData create(MenuData menuData) {
//        return persist(menuData);
//    }
//
//    public List<MenuData> findAll() {
//        Query q = currentSession().createQuery("SELECT d FROM MenuData d");
//        return (List<MenuData>) q.list();
//    }
//}
//
//package com.toasttab.codereview.test;
//
//import com.toasttab.codereview.service.MenuDataResource.Reader;
//import com.toasttab.codereview.service.MenuDataResource.ReaderType;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class ReaderTest {
//
//    @Test
//    public void retrieveTest() throws IOException {
//        File f = File.createTempFile("retrieveTest", ".txt");
//        Files.write(Paths.get(f.getAbsolutePath()), "SomeText".getBytes());
//        byte[] actualB = MenuDataResource.Reader.retrieve(MenuDataResource.ReaderType.FILE_BASED, f.getAbsolutePath());
//        String actualS = new String(actualB, StandardCharsets.UTF_8);
//        Assert.assertEquals("SomeText", actualS);
//        Assert.assertNotNull(MenuDataResource.Reader.retrieve(MenuDataResource.ReaderType.URL, "http://www.example.com"));
//    }
//}