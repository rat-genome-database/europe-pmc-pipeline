package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;

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

    private List<DataConverter> diseaseOnt = new ArrayList<>();
    private List<DataConverter> geneOnt = new ArrayList<>();
    private List<DataConverter> mammalianPhen = new ArrayList<>();
    private List<DataConverter> humanPhen = new ArrayList<>();
    private List<DataConverter> pathwayOnt = new ArrayList<>();

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
            List<GenomicElement> refObjs = associationDAO.getElementsAssociatedWithReference(dc.getRgdId());// get the objects related
            for (GenomicElement ge : refObjs)// loop thru objects related to reference
            {
                DataConverter dc2 = new DataConverter();
                dc2.setRgdId(ge.getRgdId());
                dc2.setPmid(dc.getPmid()); // store pubmed id from dc (references)
                dc2.setTitle(ge.getSymbol());
                dc2.setObjectKey(ge.getObjectKey()); // for sorting purposes
                dc2.setAccId(ge.getSoAccId());
                objectRef.add(dc2); // store in objectRef
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

    public void createOntologies() throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();
        AnnotationDAO adao = new AnnotationDAO();
        // RDO DOID, GO, MP, HP, PW
        for (DataConverter dc : objectRef) {

            List<Annotation> annots = adao.getAnnotations(dc.getRgdId());

            for (Annotation annot : annots){
                // during loop, sort into respective lists
                // instead of multiple long loops, just one and multiple lists
                addOntTerms(dc,annot);
//                String[] prefix = annot.getTermAcc().split(":");
//                if (ont.equals(prefix[0])){
//                    Term t = dao.getTermByAccId(annot.getTermAcc());
//                    DataConverter d = new DataConverter();
//                    if (t == null){
//                        continue;
//                    }
//                    d.setAccId(t.getAccId());
//                    d.setPmid(dc.getPmid());
//                    d.setTitle(t.getTerm());
//                    data.add(d);
//                }

            } // end annotations loop

        } // end of object Ref loop

        return;
    }

    public void addOntTerms(DataConverter dc, Annotation annot) throws Exception{
        List<DataConverter> data = new ArrayList<>();
        OntologyXDAO dao = new OntologyXDAO();

        Term t = dao.getTermByAccId(annot.getTermAcc());
        DataConverter d = new DataConverter();
        if (t == null){
            return;
        }
        d.setAccId(t.getAccId());
        d.setPmid(dc.getPmid());
        d.setTitle(t.getTerm());
        data.add(d);
        // get term and childs
//        List<TermWithStats> tws = dao.getActiveChildTerms(annot.getTermAcc(),3);
//        for (TermWithStats tw : tws){
//            DataConverter dc2 = new DataConverter();
//            dc2.setAccId(tw.getAccId());
//            dc2.setPmid(dc.getPmid());
//            dc2.setTitle(tw.getTerm());
//            data.add(dc2);
//        }

        String[] term = annot.getTermAcc().split(":");
        // during loop, sort into respective lists
        // instead of multiple long loops, just one and multiple lists
        switch (term[0]){
            case "DOID":
                diseaseOnt.addAll(data);
                break;
            case "GO":
                geneOnt.addAll(data);
                break;
            case "MP":
                mammalianPhen.addAll(data);
                break;
            case "HP":
                humanPhen.addAll(data);
                break;
            case "PW":
                pathwayOnt.addAll(data);
                break;
        }


    }

    public List<DataConverter> getOntology(String ont) throws Exception{
        switch (ont){
            case "DOID":
                return diseaseOnt;
            case "GO":
                return geneOnt;
            case "MP":
                return mammalianPhen;
            case "HP":
                return humanPhen;
            case "PW":
                return pathwayOnt;
            default:
                return diseaseOnt;
        }
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
