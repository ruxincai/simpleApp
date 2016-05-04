package shu.example.handlers;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GetText extends Handler {

@Override
public void handle(@NotNull HttpServletRequest request,
		@NotNull HttpServletResponse response, @NotNull Connection db,
		@NotNull List<String> ids)
		throws IOException, SQLException {
	String text = !ids.isEmpty() ? ids.iterator().next() : null;
	PrintWriter writer = response.getWriter();
	writer.write("{\"text\": \"");
	writer.write(String.valueOf(text));
	writer.write("\"}");
}
}