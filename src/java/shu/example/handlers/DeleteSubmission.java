package shu.example.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public class DeleteSubmission extends Handler {

private static final String QUERY =
		"DELETE FROM submission WHERE id = ?";

@Override
public void handle(@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response, @NotNull Connection db,
		@NotNull List<String> ids)
		throws IOException, SQLException {
	assert ids.size() == 1 : "Expecting one identifier";
	Integer id = Integer.parseInt(ids.get(0));
	try (PreparedStatement statement = db.prepareStatement(QUERY);) {
		statement.setInt(1, id);
		int status = statement.executeUpdate();
		if (status == 0) {
			sendNotFound(response);
		}
		else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}
}

}
