package nl.koppeltaal.spring.boot.starter.smartservice.service.context;

import jakarta.servlet.ServletRequest;

/**
 *
 */
public class TraceContextHelper {
	public static TraceContext findTraceContext(ServletRequest servletRequest) {
		return new TraceContext();
	}
}
