package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.ReferenceDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.Reference;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class DataConverter {
    private int rgdId;
    private String title;

    public DataConverter(){}

    public List<DataConverter> getReferences() throws Exception{
        ReferenceDAO rdao = new ReferenceDAO();
        List<DataConverter> data = new ArrayList<>();
        List<Reference> list  = rdao.getActiveReferences();

        for (Reference ref : list){
            DataConverter dc = new DataConverter();
            dc.setRgdId(ref.getRgdId());
            dc.setTitle(ref.getTitle());
            data.add(dc);
        }

        return data;
    }

    public List<DataConverter> getGenes() throws Exception {
        List<DataConverter> data = new ArrayList<>();
        GeneDAO dao = new GeneDAO();
        List<Gene> genes = dao.getActiveGenes(3);

        for (Gene g : genes){
            DataConverter dc = new DataConverter();
            dc.setRgdId(g.getRgdId());
            dc.setTitle(g.getSymbol());
            data.add(dc);
        }

        return data;
    }

    public int getRgdId() {
        return rgdId;
    }

    public void setRgdId(int rgdId) {
        this.rgdId = rgdId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
