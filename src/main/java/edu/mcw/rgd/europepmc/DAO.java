package edu.mcw.rgd.europepmc;

import edu.mcw.rgd.dao.impl.AnnotationDAO;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.ReferenceDAO;
import edu.mcw.rgd.dao.impl.XdbIdDAO;
import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.XdbId;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;

import java.util.List;

/**
 * wrapper for all database access used by the Europe PMC pipeline
 */
public class DAO {

    private final ReferenceDAO referenceDAO = new ReferenceDAO();
    private final XdbIdDAO xdbIdDAO = new XdbIdDAO();
    private final AssociationDAO associationDAO = new AssociationDAO();
    private final AnnotationDAO annotationDAO = new AnnotationDAO();
    private final OntologyXDAO ontologyDAO = new OntologyXDAO();

    public String getConnectionInfo() {
        return referenceDAO.getConnectionInfo();
    }

    public List<Reference> getActiveReferences() throws Exception {
        return referenceDAO.getActiveReferences();
    }

    public List<XdbId> getXdbIdsByRgdId(int xdbKey, int rgdId) throws Exception {
        return xdbIdDAO.getXdbIdsByRgdId(xdbKey, rgdId);
    }

    public List<GenomicElement> getElementsAssociatedWithReference(int refRgdId) throws Exception {
        return associationDAO.getElementsAssociatedWithReference(refRgdId);
    }

    public List<Annotation> getAnnotations(int rgdId) throws Exception {
        return annotationDAO.getAnnotations(rgdId);
    }

    public Term getTermByAccId(String termAcc) throws Exception {
        return ontologyDAO.getTermByAccId(termAcc);
    }
}
