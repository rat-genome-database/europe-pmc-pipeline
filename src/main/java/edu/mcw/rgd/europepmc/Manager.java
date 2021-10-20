package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.ReferenceDAO;
import edu.mcw.rgd.datamodel.Reference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Manager {
    public String version;
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
                        url = "https://dev.rgd.mcw.edu/rgdweb/report/reference/main.html?id=";
                        file = "RGDReferences";
                        manager.run(url,file);
                        break;
                    case "--genes":
                        url = "https://dev.rgd.mcw.edu/rgdweb/report/gene/main.html?id=";
                        file = "RGDgenes";
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
        List<DataConverter> list = new ArrayList<>();
        DataConverter dc = new DataConverter();
        switch (file){
            case "RGDReferences":
                list = dc.getReferences();
                break;
            case "RGDgenes":
                list = dc.getGenes();
                break;
        }
        BufferedWriter out = new BufferedWriter(new FileWriter("data\\"+file+".xml"));
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        out.write("<links>\n");
        // get data
//        String url = "https://dev.rgd.mcw.edu/rgdweb/report/reference/main.html?id=";
        for(int i = 0; i < list.size(); i++){
            out.write("\t<link providerId=\"1000\">\n"); // 1000 is a placeholder
            out.write("\t\t<resource>\n");
            out.write("\t\t\t<url>"+ url+list.get(i).getRgdId() +"</url>\n");
            out.write("\t\t\t<title>"+ list.get(i).getTitle() +"</title>\n");
            out.write("\t\t</resource>\n");
            out.write("\t\t<record>\n");
            out.write("\t\t\t<source>"+ "RGD" +"</source>\n");
            out.write("\t\t\t<id>" + list.get(i).getRgdId()+ "</id>\n");
            out.write("\t\t</record>\n");
            out.write("\t</link>\n");
        }
        out.write("</links>");
        out.close();

    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
