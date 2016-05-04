package shu.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import shu.example.handlers.Handler;

/**
 * API servlet.
 */
public class APIServlet extends BaseServlet {

/** The class logger. */
private static final Log LOG = LogFactory.getLog(APIServlet.class);
/** The root endpoint. */
private static final EndPoint ROOT = new EndPoint();

static {
	ClassLoader classLoader = APIServlet.class.getClassLoader();
	try (InputStream is = APIServlet.class
			.getResourceAsStream("endpoints.xml")) {
		assert is != null : "endpoints.xml is missing";
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(is);
		try {
			reader.nextTag();
			reader.require(XMLStreamConstants.START_ELEMENT, null, "endpoints");
			readEndpoint(reader, ROOT, classLoader);
			reader.require(XMLStreamConstants.END_ELEMENT, null, "endpoints");
		}
		catch (XMLStreamException e) {
			Location l = reader.getLocation();
			LOG.warn("Error reading endpoints.xml [" + l.getLineNumber() +
					", " + l.getColumnNumber() + ']', e);
		}
		finally {
			reader.close();
		}
	}
	catch (IOException | XMLStreamException e) {
		LOG.warn("Error reading endpoints.xml", e);
	}
}

@Override
public void service(@NotNull ServletRequest servletRequest,
		@NotNull ServletResponse servletResponse)
		throws ServletException, IOException {
	HttpServletRequest request = (HttpServletRequest) servletRequest;
	HttpServletResponse response = (HttpServletResponse) servletResponse;

	response.setContentType(TEXT_JSON);

	String pathInfo = request.getPathInfo();
	if (pathInfo == null) {
		Handler.sendNotFound(response);
		return;
	}

	String[] parts = StringUtils.split(pathInfo, '/');
	int i = 0, n = parts.length;
	List<String> ids = new ArrayList<>();
	EndPoint endPoint = ROOT;
	while (endPoint != null && i < n) {
		endPoint = endPoint.getChild(parts[i], ids);
		++i;
	}

	if (endPoint == null) {
		Handler.sendNotFound(response);
		return;
	}

	Handler handler = endPoint.getHandler(request.getMethod());
	if (handler == null) {
		response.setHeader("Allow", endPoint.getAllowed());
		Handler.sendError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				"Method not allowed");
		return;
	}

	try (Connection db = DATA_SOURCE.getConnection()) {
		handler.handle(request, response, db, ids);
	}
	catch (SQLException e) {
		LOG.warn("Error connecting to the database", e);
		Handler.sendError(response,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				e.getMessage());
	}
}

private static void readEndpoint(@NotNull XMLStreamReader reader,
		@NotNull EndPoint parent, @NotNull ClassLoader classLoader)
		throws XMLStreamException {
	reader.nextTag();
	while (reader.isStartElement()) {
		String localName = reader.getLocalName();
		switch (localName) {
			case "method": {
				String name = reader.getAttributeValue(null, "name");
				String handler = reader.getAttributeValue(null, "handler");
				try {
					parent.addHandler(name,
							Class.forName(handler, false, classLoader)
									.asSubclass(Handler.class).newInstance());
				}
				catch (Exception e) {
					LOG.warn("Error creating instance of " + handler, e);
				}
				reader.nextTag();
				break;
			}
			case "endpoint": {
				String name = reader.getAttributeValue(null, "name");
				EndPoint endPoint = new EndPoint();
				readEndpoint(reader, endPoint, classLoader);
				parent.addChild(name, endPoint);
				break;
			}
			case "default": {
				EndPoint endPoint = new EndPoint();
				readEndpoint(reader, endPoint, classLoader);
				parent.setDefaultChild(endPoint);
				break;
			}
			default:
				throw new XMLStreamException("Unexpected element: " + localName,
						reader.getLocation());
		}
		reader.require(XMLStreamConstants.END_ELEMENT, null, localName);
		reader.nextTag();
	}
}

}
