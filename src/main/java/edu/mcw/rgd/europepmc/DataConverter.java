package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;

import java.util.ArrayList;
import java.util.List;

public class DataConverter {
    private int rgdId;
    private String title;
    private String pmid = null;
    private String accId = null;

    public DataConverter(){}

    public List<DataConverter> getReferences() throws Exception{
        ReferenceDAO rdao = new ReferenceDAO();
        XdbIdDAO xdbDAO = new XdbIdDAO();
        List<DataConverter> data = new ArrayList<>();
        List<Reference> list  = rdao.getActiveReferences();

        for (Reference ref : list){
            DataConverter dc = new DataConverter();
            try{
                String pubid = xdbDAO.getXdbIdsByRgdId(2, ref.getRgdId()).get(0).getAccId();
                dc.setPmid(pubid);
            }
            catch (Exception e){
                dc.setPmid(null);
            }
            dc.setRgdId(ref.getRgdId());
            dc.setTitle(ref.getTitle());
            data.add(dc);
        }

        return data;
    }

    public List<DataConverter> getGenes() throws Exception {
        List<DataConverter> data = new ArrayList<>();
        GeneDAO dao = new GeneDAO();
        List<Gene> genes = dao.getAllActiveGenes();

        for (Gene g : genes) {
            DataConverter dc = new DataConverter();
            dc.setRgdId(g.getRgdId());
            dc.setTitle(g.getSymbol());
            data.add(dc);
        }
        return data;
    }

    public List<DataConverter> getDiseaseOntologies() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();
        List<Term> terms = dao.getActiveTerms("RDO");

        for (Term t : terms){
            DataConverter dc = new DataConverter();
            dc.setTitle(t.getTerm());
            dc.setAccId(t.getAccId());
            data.add(dc);
        }
        return data;
    }

    public List<DataConverter> getGeneOntologies() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();
        List<Term> terms = dao.getActiveTerms("MF");

        for (Term t : terms){
            DataConverter dc = new DataConverter();
            dc.setTitle(t.getTerm());
            dc.setAccId(t.getAccId());
            data.add(dc);
        }
        return data;
    }

    public List<DataConverter> getMammalianPhenotype() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();
        List<Term> terms = dao.getActiveTerms("MP");

        for (Term t : terms){
            DataConverter dc = new DataConverter();
            dc.setTitle(t.getTerm());
            dc.setAccId(t.getAccId());
            data.add(dc);
        }
        return data;
    }

    public List<DataConverter> getStrains() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        StrainDAO dao = new StrainDAO();
        List<Strain> strains = dao.getActiveStrains();
        for (Strain strain : strains){
            DataConverter dc = new DataConverter();
            dc.setRgdId(strain.getRgdId());
            dc.setTitle(strain.getName());
            data.add(dc);
        }
        return data;
    }

    public List<DataConverter> getQTLs() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        QTLDAO dao = new QTLDAO();
        List<QTL> qtls = dao.getActiveQTLs(3);
        for (QTL qtl : qtls){
            DataConverter dc = new DataConverter();
            dc.setRgdId(qtl.getRgdId());
            dc.setTitle(qtl.getName());
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

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }
}
