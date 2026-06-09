package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
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

    private Logger logger = LogManager.getLogger("status");

    public DataConverter(){}

    public void createReferences(DAO dao) throws Exception{
        logger.debug("createReferences()  starting...");

        List<Reference> list  = dao.getActiveReferences();

        for (Reference ref : list){
            DataConverter dc = new DataConverter();
            try{
                String pubid = dao.getXdbIdsByRgdId(2, ref.getRgdId()).get(0).getAccId();
                dc.setPmid(pubid);
            }
            catch (Exception e){
                continue;
            }
            dc.setRgdId(ref.getRgdId());
            dc.setTitle(ref.getTitle());
            references.add(dc);
        }

        logger.debug("createReferences()  loaded PMIDs");

        for (DataConverter dc : references){
            List<GenomicElement> refObjs = dao.getElementsAssociatedWithReference(dc.getRgdId());// get the objects related
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

        logger.debug("createReferences()  loaded assoc objs");
    }

    public List<DataConverter> getGenes() throws Exception {
        List<DataConverter> data = new ArrayList<>();

        for (DataConverter dc : objectRef) {
            if ( RgdId.getObjectTypeName(dc.getObjectKey()).equals("Gene") )
                data.add(dc);
        }

        return data;
    }

    public void createOntologies(DAO dao) throws Exception{

        HashMap<String, Boolean> duplicate = new HashMap<>();
        // RDO DOID, GO, MP, HP, PW
        for (DataConverter dc : objectRef) {

            List<Annotation> annots = dao.getAnnotations(dc.getRgdId());

            for (Annotation annot : annots){
                // during loop, sort into respective lists

                // check for duplicates
                String[] term = annot.getTermAcc().split(":");
                switch (term[0]){
                    case "DOID":
                    case "GO":
                    case "MP":
                    case "HP":
                    case "PW":
                        if (duplicate.get(annot.getTermAcc()) == null) {
                            addOntTerms(dc, annot, dao);
                            duplicate.put(annot.getTermAcc(),true);
                        }
                        break;
                    default:
                        continue;
                }
            } // end annotations loop

        } // end of object Ref loop
    }

    public void addOntTerms(DataConverter dc, Annotation annot, DAO dao) throws Exception{
        List<DataConverter> data = new ArrayList<>();

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
