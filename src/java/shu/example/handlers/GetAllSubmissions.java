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

public class GetAllSubmissions extends Handler {

private static final String QUERY =
		"SELECT s.id, userName, last_update, city, s.text, r.text " +
				"FROM submission s LEFT JOIN response r " +
				"ON s.id = r.submission_id ORDER BY 3";

@Override
public void handle(@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response, @NotNull Connection db,
		@NotNull List<String> ids)
		throws IOException, SQLException {
	try (PreparedStatement statement = db.prepareStatement(QUERY)) {
		Map<String, Map<String, Object>> map = new LinkedHashMap<>();
		try (ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				String id = resultSet.getString(1);
				Map<String, Object> subMap = map.get(id);
				if (subMap == null) {
					subMap = new HashMap<>();
					map.put(id, subMap);
					subMap.put("userName", resultSet.getString(2));
					subMap.put("last_update", resultSet.getTimestamp(3).getTime());
					subMap.put("city", resultSet.getString(4));
					subMap.put("text", resultSet.getString(5));
				}
				@SuppressWarnings("unchecked")
				List<String> responses = (List<String>)subMap.get("responses");
				if (responses == null) {
					responses = new ArrayList<>();
					subMap.put("responses", responses);
				}
				String resp = resultSet.getString(6);
				if (resp != null) {
					responses.add(resp);
				}
			}
		}
		PrintWriter writer = response.getWriter();
		writer.write('[');
		boolean first = true;
		for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
			String id = entry.getKey();
			Map<String, Object> subMap = entry.getValue();
			if (first) {
				first = false;
			}
			else {
				writer.write(',');
			}
			writer.write("{\"id\":");
			writer.write(id);
			writer.write(",\"userName\":\"");
			StringEscapeUtils.ESCAPE_JSON.translate(
					(String) subMap.get("userName"), writer);
			writer.write("\",\"last_update\": ");
			writer.write(String.valueOf(subMap.get("last_update")));
			writer.write(",\"city\":\"");
			StringEscapeUtils.ESCAPE_JSON.translate(
					(String) subMap.get("city"), writer);
			writer.write("\",\"text\":\"");
			StringEscapeUtils.ESCAPE_JSON.translate(
					(String) subMap.get("text"), writer);
			writer.write("\",\"responses\": [");
			@SuppressWarnings("unchecked")
			List<String> responses = (List<String>)subMap.get("responses");
			for (int i = 0; i < responses.size(); i++) {
				if (i != 0) {
					writer.print(',');
				}
				writer.write("\"");
				StringEscapeUtils.ESCAPE_JSON.translate(
						responses.get(i), writer);
				writer.write("\"");
			}
			writer.write("]}");
		}
		writer.write("]");
	}
}

}
