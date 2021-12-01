package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.Term;

import java.util.ArrayList;
import java.util.List;

public class DataConverter {
    private int rgdId;
    private String title;
    private String pmid = null;
    private String accId = null;
    private int objectKey;
    private List<DataConverter> objectRef = new ArrayList<>();
    private List<DataConverter> references = new ArrayList<>();

    public DataConverter(){}

    public void createReferences() throws Exception{
        ReferenceDAO rdao = new ReferenceDAO();
        XdbIdDAO xdbDAO = new XdbIdDAO();
        AssociationDAO associationDAO = new AssociationDAO();
        List<Reference> list  = rdao.getActiveReferences();

        for (Reference ref : list){
            DataConverter dc = new DataConverter();
            try{
                String pubid = xdbDAO.getXdbIdsByRgdId(2, ref.getRgdId()).get(0).getAccId();
                dc.setPmid(pubid);
            }
            catch (Exception e){
                continue;
            }
            dc.setRgdId(ref.getRgdId());
            dc.setTitle(ref.getTitle());
            references.add(dc);
        }

        for (DataConverter dc : references){
            // get the objects related, then store in objectRef
            // grab rgd id, and store pubmed id from dc
            // loop thru objects related to reference
            List<GenomicElement> refObjs = associationDAO.getElementsAssociatedWithReference(dc.getRgdId());
            for (GenomicElement ge : refObjs)
            {
                DataConverter dc2 = new DataConverter();
                dc2.setRgdId(ge.getRgdId());
                dc2.setPmid(dc.getPmid());
                dc2.setTitle(ge.getSymbol());
                dc2.setObjectKey(ge.getObjectKey());
                dc2.setAccId(ge.getSoAccId());
                objectRef.add(dc2);
            }
//            RgdId.getObjectTypeName(ge.getObjectKey())
        }

        return;
    }

    public List<DataConverter> getGenes() throws Exception {
        List<DataConverter> data = new ArrayList<>();

        for (DataConverter dc : objectRef) {
            if ( RgdId.getObjectTypeName(dc.getObjectKey()).equals("Gene") )
                data.add(dc);
        }

        return data;
    }

    public List<DataConverter> getOntologies(String ont) throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();
//        List<Term> terms = dao.getActiveTerms(ont); // RDO, MF, MP, HP, PW
//
//        for (Term t : terms){
//            DataConverter dc = new DataConverter();
//            dc.setTitle(t.getTerm());
//            dc.setAccId(t.getAccId());
//            data.add(dc);
//        }


        return data;
    }

    public List<DataConverter> getStrains() throws Exception{
        List<DataConverter> data = new ArrayList<>();

        for (DataConverter dc : objectRef) {
            if ( RgdId.getObjectTypeName(dc.getObjectKey()).equals("Strain") )
                data.add(dc);
        }

        return data;
    }

    public List<DataConverter> getQTLs() throws Exception{
        List<DataConverter> data = new ArrayList<>();

        for (DataConverter dc : objectRef) {
            if ( RgdId.getObjectTypeName(dc.getObjectKey()).equals("QTL") )
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

    public int getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(int objectKey) {
        this.objectKey = objectKey;
    }

    public List<DataConverter> getReferences() {
        return references;
    }

    public void setReferences(List<DataConverter> references) {
        this.references = references;
    }
}
