package nl.koppeltaal.spring.boot.starter.smartservice.service.context;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 *
 */
public class TraceContext {
	final String traceId;
	final String spanId;
	String parentSpanId;

	public TraceContext() {
		this.traceId = UUID.randomUUID().toString();
		this.spanId = UUID.randomUUID().toString();
	}
	public TraceContext(String traceId, String spanId) {
		this.traceId = traceId;
		this.spanId = spanId;
	}

	public TraceContext(String traceId, String spanId, String parentSpanId) {
		this.traceId = traceId;
		this.spanId = spanId;
		this.parentSpanId = parentSpanId;
	}

	public String getParentSpanId() {
		return StringUtils.isNotEmpty(parentSpanId) ? parentSpanId : spanId;
	}

	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
	}

	public String getSpanId() {
		return spanId;
	}


	public String getTraceId() {
		return traceId;
	}

}
