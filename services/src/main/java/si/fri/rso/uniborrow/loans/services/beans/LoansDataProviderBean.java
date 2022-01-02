package si.fri.rso.uniborrow.loans.services.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.uniborrow.loans.models.entities.AcceptedState;
import si.fri.rso.uniborrow.loans.models.entities.LoanEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;


@RequestScoped
public class LoansDataProviderBean {

    private Logger log = Logger.getLogger(LoansDataProviderBean.class.getName());

    @Inject
    private EntityManager em;

    @Timed
    public List<LoanEntity> getLoansFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, LoanEntity.class, queryParameters);
    }

    public LoanEntity getLoan(Integer id) {
        LoanEntity loanData = em.find(LoanEntity.class, id);
        if (loanData == null) {
            throw new NotFoundException();
        }
        return loanData;
    }

    @Counted
    public LoanEntity createLoan(LoanEntity loanEntity) {
        try {
            beginTx();
            em.persist(loanEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        if (loanEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }
        return loanEntity;
    }

    public LoanEntity patchLoan(Integer id, LoanEntity updatedLoanEntity) {
        LoanEntity c = em.find(LoanEntity.class, id);
        if (c == null) {
            return null;
        }
        try {
            beginTx();
            updatedLoanEntity.setId(c.getId());
            updatedLoanEntity = em.merge(updatedLoanEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return updatedLoanEntity;
    }

    public LoanEntity acceptLoan(Integer id) {
        return changeLoanState(id, AcceptedState.ACCEPTED);
    }

    public LoanEntity rejectLoan(Integer id) {
        return changeLoanState(id, AcceptedState.REJECTED);
    }

    private LoanEntity changeLoanState(Integer id, AcceptedState acceptedState) {
        LoanEntity c = em.find(LoanEntity.class, id);
        if (c == null || c.getAcceptedState() != AcceptedState.PENDING) {
            return null;
        }
        try {
            beginTx();
            c.setAcceptedState(acceptedState);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return c;
    }


    public List<LoanEntity> getLoansByItemId(Integer id) {
        return em.createNamedQuery("LoanEntity.getByItemId", LoanEntity.class).setParameter("itemId", id).getResultList();
    }

    public boolean deleteLoan(Integer id) {

        LoanEntity loanEntity = em.find(LoanEntity.class, id);

        if (loanEntity != null) {
            try {
                beginTx();
                em.remove(loanEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}