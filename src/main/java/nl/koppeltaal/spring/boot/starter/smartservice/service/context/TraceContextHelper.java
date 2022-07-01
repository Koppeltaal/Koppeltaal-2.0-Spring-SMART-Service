package nl.koppeltaal.spring.boot.starter.smartservice.service.context;

import javax.servlet.ServletRequest;

/**
 *
 */
public class TraceContextHelper {
	public static TraceContext findTraceContext(ServletRequest servletRequest) {
		return new TraceContext();
	}
}
