package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.process.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class Manager {
    public String version;
    public DataConverter getter = new DataConverter();
    protected Logger logger = Logger.getLogger("status");

    public static void main(String[] args) throws Exception{
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        try{
            Manager manager = (Manager)(bf.getBean("manager"));
            manager.run(args);
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    void run(String[] args) throws Exception{
        logger.info(getVersion());
        SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long pipeStart = System.currentTimeMillis();
        logger.info("   Pipeline started at "+sdt.format(new Date(pipeStart))+"\n");
        String url;
        String file;
        getter.createReferences();
        if (checkArgsForOnt(args)){
            getter.createOntologies();
        }
        try {
            for (int i = 0; i < args.length; i++){
                logger.info("======================");
                switch (args[i]){
                    case "--rgdRef":
                        url = "https://rgd.mcw.edu/rgdweb/report/reference/main.html?id={temp}";
                        file = "RGDReferences.xml.gz";
                        create(url,file);
                        break;
                    case "--genes":
                        url = "https://rgd.mcw.edu/rgdweb/report/gene/main.html?id={temp}";
                        file = "RGDgenes.xml.gz";
                        create(url,file);
                        break;
                    case "--strains":
                        url = "https://rgd.mcw.edu/rgdweb/report/strain/main.html?id={temp}";
                        file = "RGDstrains.xml.gz";
                        create(url,file);
                        break;
                    case "--qtls":
                        url = "https://rgd.mcw.edu/rgdweb/report/qtl/main.html?id={temp}";
                        file = "RGDqtls.xml.gz";
                        create(url,file);
                        break;
                    case "--ontRDO":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}&species=All";
                        file = "RGDdiseaseOntologies.xml.gz";
                        create(url,file);
                        break;
                    case "--ontGO":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}";
                        file = "RGDgeneOntology.xml.gz";
                        create(url,file);
                        break;
                    case "--ontMamPhen":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}";
                        file = "RGDmammalianPhenotype.xml.gz";
                        create(url,file);
                        break;
                    case "--ontHumPhen":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}&species=Human";
                        file = "RGDhumanPhenotype.xml.gz";
                        create(url,file);
                        break;
                    case "--ontPathway":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}";
                        file = "RGDpathwayOntology.xml.gz";
                        create(url,file);
                        break;
                }
            }
        }
        catch (Exception e){
            logger.info(e);
            e.printStackTrace();
        }
        logger.info("Pipeline runtime -- elapsed time: "+ Utils.formatElapsedTime(pipeStart,System.currentTimeMillis()));
    }

    void create(String url, String file) throws Exception{
        logger.info("\tCreating file \"" + file + "\" start!");
        List<DataConverter> list = new ArrayList<>();
        boolean ontology = false;
        // get data
        switch (file){
            case "RGDReferences.xml.gz":
                list = getter.getReferences();
                break;
            case "RGDgenes.xml.gz":
                list = getter.getGenes();
                break;
            case "RGDstrains.xml.gz":
                list = getter.getStrains();
                break;
            case "RGDqtls.xml.gz":
                list = getter.getQTLs();
                break;
            case "RGDdiseaseOntologies.xml.gz":
                list = getter.getOntology("DOID");
                ontology = true;
                break;
            case "RGDgeneOntology.xml.gz":
                list = getter.getOntology("GO");
                ontology = true;
                break;
            case "RGDmammalianPhenotype.xml.gz":
                list = getter.getOntology("MP");
                ontology = true;
                break;
            case "RGDhumanPhenotype.xml.gz":
                list = getter.getOntology("HP");
                ontology = true;
                break;
            case "RGDpathwayOntology.xml.gz":
                list = getter.getOntology("PW");
                ontology = true;
                break;
        }



        BufferedWriter out = openOutputFile(file);
        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?> \n");
        out.write("<links>\n");

        for (DataConverter dataConverter : list) {
            String title = dataConverter.getTitle().replaceAll("&", "&amp;");
            title = title.replaceAll("<", "&lt;");
            title = title.replaceAll(">", "&gt;");
            out.write("\t<link providerId=\"2134\">\n");
            out.write("\t\t<resource>\n");
            if (ontology) {
                String tempUrl;
                tempUrl = url.replace("{temp}", dataConverter.getAccId());
                out.write("\t\t\t<url>" + tempUrl + "</url>\n");
            } else {
                String tempUrl;
                tempUrl = url.replace("{temp}", Integer.toString(dataConverter.getRgdId()));
                out.write("\t\t\t<url>" + tempUrl + "</url>\n");
            }
            out.write("\t\t\t<title>" + title + "</title>\n");
            out.write("\t\t</resource>\n");
            out.write("\t\t<record>\n");
            out.write("\t\t\t<source>" + "MED" + "</source>\n");
            out.write("\t\t\t<id>" + dataConverter.getPmid() + "</id>\n");
            out.write("\t\t</record>\n");
            out.write("\t</link>\n");
        }
        out.write("</links>");
        out.close();
        logger.info("\t\tCreated file: "+file);

        logger.info("\tCreating file \"" + file + "\" end\n");
    }

    BufferedWriter openOutputFile(String outputFile) throws IOException {
        if( outputFile.endsWith(".gz") ) {
            return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("data/"+outputFile))));
        } else {
            return new BufferedWriter(new FileWriter("data/"+outputFile));
        }
    }

    private boolean checkArgsForOnt(String[] args) {
        for (String arg : args){
            if (arg.contains("ont"))
                return true;
        }
        return false;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
