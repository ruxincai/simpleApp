package shu.example;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.postgresql.ds.PGPoolingDataSource;

/**
 * Base implementation of {@link Servlet}.
 */
public abstract class BaseServlet implements Servlet {

/** The data source. */
protected static final PGPoolingDataSource DATA_SOURCE;
protected static final String TEXT_JSON = "text/json;charset=UTF-8";

static {
	DATA_SOURCE = new PGPoolingDataSource();
	DATA_SOURCE.setUser("postgres");
	DATA_SOURCE.setDatabaseName("homework");
}

@Override
public void init(ServletConfig servletConfig) throws ServletException {
}

@Override
public ServletConfig getServletConfig() {
	return null;
}

@Override
public String getServletInfo() {
	return null;
}

@Override
public void destroy() {
}
}
