package edu.mcw.rgd.europepmc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class Manager {
    public String version;

    protected Logger logger = Logger.getLogger("status");

    public static void main(String[] args) throws Exception{
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        try{
            Manager manager = (Manager)(bf.getBean("manager"));
            for (int i = 0; i < args.length; i++){
                String url;
                String file;
                switch (args[i]){
                    case "--rgdRef":
                        url = "https://rgd.mcw.edu/rgdweb/report/reference/main.html?id={temp}";
                        file = "RGDReferences.xml.gz";
                        manager.run(url,file);
                        break;
                    case "--genes":
                        url = "https://rgd.mcw.edu/rgdweb/report/gene/main.html?id={temp}";
                        file = "RGDgenes.xml.gz";
                        manager.run(url,file);
                        break;
                    case "--strains":
                        url = "https://rgd.mcw.edu/rgdweb/report/strain/main.html?id={temp}";
                        file = "RGDstrains.xml.gz";
                        manager.run(url,file);
                        break;
                    case "--qtls":
                        url = "https://rgd.mcw.edu/rgdweb/report/qtl/main.html?id={temp}";
                        file = "RGDqtls.xml.gz";
                        manager.run(url,file);
                        break;
                    case "--ontRDO":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}&species=All";
                        file = "RGDdiseaseOntologies.xml.gz";
                        manager.run(url,file);
                        break;
                    case "--ontGO":
                        url = "https://rgd.mcw.edu/rgdweb/ontology/annot.html?acc_id={temp}";
                        file = "RGDgeneOntology.xml.gz";
                        manager.run(url,file);
                        break;
                }
            }
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    void run(String url, String file) throws Exception{
        logger.info(getVersion());
        logger.info("Creating file(s) start!");
        create(url,file);
        logger.info("Creating file(s) end");
    }

    void create(String url, String file) throws Exception{
        List<DataConverter> list = new ArrayList<>();
        DataConverter dc = new DataConverter();
        boolean ontology = false;
        // get data
        switch (file){
            case "RGDReferences.xml.gz":
                list = dc.getReferences();
                break;
            case "RGDgenes.xml.gz":
                list = dc.getGenes();
                break;
            case "RGDstrains.xml.gz":
                list = dc.getStrains();
                break;
            case "RGDqtls.xml.gz":
                list = dc.getQTLs();
                break;
            case "RGDdiseaseOntologies.xml.gz":
                list = dc.getDiseaseOntologies();
                ontology = true;
                break;
            case "RGDgeneOntology.xml.gz":
                list = dc.getGeneOntologies();
                ontology = true;
                break;
        }
        BufferedWriter out = openOutputFile(file);
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        out.write("<links>\n");

        for(int i = 0; i < list.size(); i++){
            out.write("\t<link providerId=\"2134\">\n"); // 1000 is a placeholder
            out.write("\t\t<resource>\n");
            if (ontology){
                    url = url.replace("{temp}",list.get(i).getAccId());
                    out.write("\t\t\t<url>" + url  + "</url>\n");
            }
            else {
                url = url.replace("{temp}", Integer.toString( list.get(i).getRgdId() ) );
                out.write("\t\t\t<url>" + url + "</url>\n");
            }
            out.write("\t\t\t<title>"+ list.get(i).getTitle() +"</title>\n");
            out.write("\t\t</resource>\n");
            out.write("\t\t<record>\n");
            if (list.get(i).getPmid() != null){
                out.write("\t\t\t<source>" + "PubMed" + "</source>\n");
                out.write("\t\t\t<id>" + list.get(i).getPmid() + "</id>\n");
            }
            else {
                out.write("\t\t\t<source>" + "RGD" + "</source>\n");
                out.write("\t\t\t<id>" + list.get(i).getRgdId() + "</id>\n");
            }
            out.write("\t\t</record>\n");
            out.write("\t</link>\n");
        }
        out.write("</links>");
        out.close();
        logger.info("\tCreated file: "+file);
    }

    BufferedWriter openOutputFile(String outputFile) throws IOException {
        if( outputFile.endsWith(".gz") ) {
            return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("data/"+outputFile))));
        } else {
            return new BufferedWriter(new FileWriter(outputFile));
        }
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
