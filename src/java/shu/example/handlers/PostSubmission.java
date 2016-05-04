package shu.example.handlers;

import org.jetbrains.annotations.NotNull;
import shu.example.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class PostSubmission extends Handler {

private static final String QUERY =
		"INSERT INTO submission (userName, city, text, last_update) " +
				"VALUES(?, ?, ?, ?) RETURNING id";

@Override
public void handle(@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull Connection db,
		@NotNull List<String> ids)
		throws IOException, SQLException {
	JSONParser parser = new JSONParser(request.getReader());
	Object o;
	try {
		o = parser.parse();
	}
	catch (ParseException e) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				e.getMessage());
		return;
	}

	if (o == null) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"Missing request body");
		return;
	}
	if (!(o instanceof Map)) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"Expecting an object");
		return;
	}
	Map<?, ?> map = (Map<?, ?>) o;
	o = map.get("name");
	if (o == null) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"Missing name property");
		return;
	}
	if (!(o instanceof String)) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"name property must be a string");
		return;
	}
	String name = (String) o;

	o = map.get("city");
	if (!(o instanceof String)) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"city property must be a string");
		return;
	}
	String city = (String) o;

	Timestamp last_update = new Timestamp(System.currentTimeMillis());

	o = map.get("text");
	if (o == null) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"Missing text property");
		return;
	}
	if (!(o instanceof String)) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"text property must be a date");
		return;
	}
	String text = (String) o;

	try (PreparedStatement statement = db.prepareStatement(QUERY)) {
		statement.setString(1, name);
		statement.setString(2, city);
		statement.setString(3, text);
		statement.setTimestamp(4, last_update);
		try (ResultSet resultSet = statement.executeQuery()) {
			resultSet.next();
			response.setContentType(null);
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader("Location", request.getServletPath() +
					request.getPathInfo() + '/' + resultSet.getString(1));
		}
		catch (SQLException e) {
			if ("23505".equals(e.getSQLState())) {
				sendError(response, HttpServletResponse.SC_CONFLICT,
						"Duplicated name");
			}
			else {
				throw e;
			}
		}
	}
}

}
