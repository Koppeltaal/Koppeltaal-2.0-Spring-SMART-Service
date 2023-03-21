package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.RestOperationTypeEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.gclient.ICriterion;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.BaseDto;
import nl.koppeltaal.spring.boot.starter.smartservice.service.context.TraceContext;
import org.hl7.fhir.r4.model.DomainResource;

import java.util.List;
import java.util.Map;

/**
 * Builder that allows the ease of extending the get resource logic.
 * It doesn't contain a build() function as it's unpacked inside the service and uses the existing
 * getResourceInternal logic
 *
 * @param <D>
 * @param <R>
 */
public class GetResourceBuilder<D extends BaseDto, R extends DomainResource> {

    private SortSpec sort;
    private ICriterion<?> criterion;

    private Map<String, List<IQueryParameterType>> criteria;
    private TraceContext traceContext;
    private boolean includeEndOfLife;

    public SortSpec getSort() {
        return sort;
    }

    public ICriterion<?> getCriterion() {
        return criterion;
    }

    public Map<String, List<IQueryParameterType>> getCriteria() {
        return criteria;
    }

    public TraceContext getTraceContext() {
        return traceContext;
    }

    public boolean isIncludeEndOfLife() {
        return includeEndOfLife;
    }

    public GetResourceBuilder<D, R> setSort(SortSpec sort) {
        this.sort = sort;
        return this;
    }

    public GetResourceBuilder<D, R> setCriterion(ICriterion<?> criterion) {
        this.criterion = criterion;
        return this;
    }

    public GetResourceBuilder<D, R> setCriteria(Map<String, List<IQueryParameterType>> criteria) {
        this.criteria = criteria;
        return this;
    }

    public GetResourceBuilder<D, R> setTraceContext(TraceContext traceContext) {
        this.traceContext = traceContext;
        return this;
    }

    public GetResourceBuilder<D, R> setIncludeEndOfLife(boolean includeEndOfLife) {
        this.includeEndOfLife = includeEndOfLife;
        return this;
    }
}
