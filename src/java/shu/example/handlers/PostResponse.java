package shu.example.handlers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;
import shu.example.JSONParser;

public class PostResponse extends Handler {

private static final String QUERY =
		"INSERT INTO response (submission_id, text) " +
				"VALUES(?, ?) RETURNING id";

@Override
public void handle(@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response,
		@NotNull Connection db,
		@NotNull List<String> ids)
		throws IOException, SQLException {
	assert ids.size() == 1 : "Expecting one identifier";
	Integer submissionId = Integer.parseInt(ids.get(0));

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
	o = map.get("text");
	if (o == null) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"Missing text property");
		return;
	}
	if (!(o instanceof String)) {
		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
				"text property must be a string");
		return;
	}
	String text = (String) o;

	try (PreparedStatement statement = db.prepareStatement(QUERY)) {
		statement.setInt(1, submissionId);
		statement.setString(2, text);
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
